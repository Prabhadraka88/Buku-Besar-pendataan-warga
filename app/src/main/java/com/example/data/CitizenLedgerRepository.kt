package com.example.data

import com.example.blockchain.BlockchainEngine
import com.example.model.Block
import com.example.model.BlockchainNode
import com.example.model.ChainAuditReport
import com.example.model.Citizen
import com.example.model.CitizenCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CitizenLedgerRepository(
    private val citizenDao: CitizenDao,
    private val blockDao: BlockDao
) {

    val allCitizens: Flow<List<Citizen>> = citizenDao.getAllCitizens()
    val allBlocks: Flow<List<Block>> = blockDao.getAllBlocks()

    fun getCitizenById(id: Long): Flow<Citizen?> = citizenDao.getCitizenById(id)

    fun getBlocksByCitizenKk(kk: String): Flow<List<Block>> = blockDao.getBlocksByCitizenKk(kk)

    suspend fun registerCitizen(citizen: Citizen): Block = withContext(Dispatchers.IO) {
        val latestBlock = blockDao.getLatestBlock()
        val nextIndex = (latestBlock?.index ?: 0L) + 1L
        val previousHash = latestBlock?.hash ?: BlockchainEngine.GENESIS_PREV_HASH

        val summary = "Registrasi KK [${citizen.category.shortLabel}]: ${citizen.fullName} (${citizen.addressStreet})"
        val payload = "{\"action\":\"REGISTRASI_KK\",\"kk\":\"${citizen.kkNumber}\",\"nik\":\"${citizen.nikHead}\",\"nama\":\"${citizen.fullName}\",\"kategori\":\"${citizen.category.name}\",\"alamat\":\"${citizen.addressStreet}\",\"telp\":\"${citizen.phone}\",\"jiwa\":${citizen.familyMembersCount}}"

        val newBlock = BlockchainEngine.mineBlock(
            index = nextIndex,
            previousHash = previousHash,
            actionType = "REGISTRASI_KK",
            citizenKk = citizen.kkNumber,
            citizenName = citizen.fullName,
            payloadSummary = summary,
            fullDataPayload = payload,
            validatorNode = "Node-RT03-Ketua"
        )

        blockDao.insertBlock(newBlock)
        val citizenWithBlock = citizen.copy(
            lastBlockHash = newBlock.hash,
            updatedAt = System.currentTimeMillis()
        )
        citizenDao.insertCitizen(citizenWithBlock)
        newBlock
    }

    suspend fun updateCitizenData(citizen: Citizen, previousCategory: CitizenCategory? = null): Block = withContext(Dispatchers.IO) {
        val latestBlock = blockDao.getLatestBlock()
        val nextIndex = (latestBlock?.index ?: 0L) + 1L
        val previousHash = latestBlock?.hash ?: BlockchainEngine.GENESIS_PREV_HASH

        val isCategoryChanged = previousCategory != null && previousCategory != citizen.category
        val actionType = if (isCategoryChanged) "MUTASI_KATEGORI" else "PERBARUI_DATA"
        val summary = if (isCategoryChanged) {
            "Mutasi Kategori: ${citizen.fullName} dari ${previousCategory?.shortLabel} ke ${citizen.category.shortLabel}"
        } else {
            "Pembaruan Data KK: ${citizen.fullName} (${citizen.addressStreet})"
        }

        val payload = "{\"action\":\"$actionType\",\"kk\":\"${citizen.kkNumber}\",\"nama\":\"${citizen.fullName}\",\"kategori\":\"${citizen.category.name}\",\"alamat\":\"${citizen.addressStreet}\",\"telp\":\"${citizen.phone}\",\"iuran\":\"${citizen.feeStatus}\"}"

        val newBlock = BlockchainEngine.mineBlock(
            index = nextIndex,
            previousHash = previousHash,
            actionType = actionType,
            citizenKk = citizen.kkNumber,
            citizenName = citizen.fullName,
            payloadSummary = summary,
            fullDataPayload = payload,
            validatorNode = "Node-RT03-Sekretaris"
        )

        blockDao.insertBlock(newBlock)
        val updatedCitizen = citizen.copy(
            lastBlockHash = newBlock.hash,
            updatedAt = System.currentTimeMillis()
        )
        citizenDao.updateCitizen(updatedCitizen)
        newBlock
    }

    suspend fun deleteCitizen(citizen: Citizen): Block = withContext(Dispatchers.IO) {
        val latestBlock = blockDao.getLatestBlock()
        val nextIndex = (latestBlock?.index ?: 0L) + 1L
        val previousHash = latestBlock?.hash ?: BlockchainEngine.GENESIS_PREV_HASH

        val summary = "Pencatatan Pindah/Hapus Warga: ${citizen.fullName} (KK: ${citizen.kkNumber})"
        val payload = "{\"action\":\"HAPUS_WARGA\",\"kk\":\"${citizen.kkNumber}\",\"nama\":\"${citizen.fullName}\",\"alasan\":\"Pindah Domisili / Nonaktif\"}"

        val newBlock = BlockchainEngine.mineBlock(
            index = nextIndex,
            previousHash = previousHash,
            actionType = "HAPUS_WARGA",
            citizenKk = citizen.kkNumber,
            citizenName = citizen.fullName,
            payloadSummary = summary,
            fullDataPayload = payload,
            validatorNode = "Node-RT03-Ketua"
        )

        blockDao.insertBlock(newBlock)
        citizenDao.deleteCitizen(citizen)
        newBlock
    }

    suspend fun auditBlockchain(): ChainAuditReport = withContext(Dispatchers.IO) {
        val blocks = blockDao.getAllBlocksSync()
        BlockchainEngine.validateChain(blocks)
    }

    suspend fun simulateTampering(blockIndex: Long): Boolean = withContext(Dispatchers.IO) {
        val block = blockDao.getBlockByIndex(blockIndex) ?: return@withContext false
        val tamperedPayload = block.fullDataPayload.replace("\"kategori\":\"WARGA_ASLI\"", "\"kategori\":\"WARGA_ILEGAL_PALSU\"")
            .replace("\"nama\":\"", "\"nama\":\"[TAMPERED] ")
        blockDao.tamperBlock(blockIndex, isCorrupted = true, tamperedPayload = tamperedPayload)
        true
    }

    suspend fun restoreChainFromConsensus(): Boolean = withContext(Dispatchers.IO) {
        val blocks = blockDao.getAllBlocksSync().sortedBy { it.index }
        var prevHash = BlockchainEngine.GENESIS_PREV_HASH

        for (b in blocks) {
            val validHash = BlockchainEngine.calculateBlockHash(
                index = b.index,
                timestamp = b.timestamp,
                previousHash = prevHash,
                actionType = b.actionType,
                citizenKk = b.citizenKk,
                payload = b.fullDataPayload.replace("[TAMPERED] ", "").replace("WARGA_ILEGAL_PALSU", "WARGA_ASLI"),
                nonce = b.nonce
            )
            val repairedBlock = b.copy(
                previousHash = prevHash,
                hash = validHash,
                isCorrupted = false,
                fullDataPayload = b.fullDataPayload.replace("[TAMPERED] ", "").replace("WARGA_ILEGAL_PALSU", "WARGA_ASLI")
            )
            blockDao.updateBlock(repairedBlock)
            prevHash = repairedBlock.hash
        }
        true
    }

    fun getDecentralizedNodes(currentHeight: Long): List<BlockchainNode> {
        return listOf(
            BlockchainNode(
                id = "NODE-01-PRIMARY",
                name = "Node RT-03 Utama (Ketua RT)",
                role = "Validator & Miner Utama",
                location = "Kediaman Bpk. Ketua RT 03",
                ipAddress = "192.168.1.101:8545",
                isOnline = true,
                syncedBlockHeight = currentHeight,
                latencyMs = 12,
                isConsensusLeader = true
            ),
            BlockchainNode(
                id = "NODE-02-SECRETARY",
                name = "Node Sekretaris RT",
                role = "Consensus Peer & Verifier",
                location = "Sekretariat RT 03",
                ipAddress = "192.168.1.102:8545",
                isOnline = true,
                syncedBlockHeight = currentHeight,
                latencyMs = 18
            ),
            BlockchainNode(
                id = "NODE-03-TREASURER",
                name = "Node Bendahara RT",
                role = "Audit Ledger Peer",
                location = "Rumah Bendahara RT",
                ipAddress = "192.168.1.103:8545",
                isOnline = true,
                syncedBlockHeight = currentHeight,
                latencyMs = 24
            ),
            BlockchainNode(
                id = "NODE-04-SECURITY",
                name = "Node Pos Kamling / Portal Publik",
                role = "Public Read-Only Node",
                location = "Pos Ronda RT 03",
                ipAddress = "192.168.1.104:8545",
                isOnline = true,
                syncedBlockHeight = currentHeight,
                latencyMs = 35
            )
        )
    }
}
