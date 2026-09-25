package com.example.blockchain

import com.example.model.Block
import com.example.model.ChainAuditReport
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BlockchainEngine {

    const val GENESIS_PREV_HASH = "0000000000000000000000000000000000000000000000000000000000000000"
    private const val DEFAULT_DIFFICULTY = "00" // Mining target: hash starts with 2 zeros for fast mobile proof-of-work

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateBlockHash(
        index: Long,
        timestamp: Long,
        previousHash: String,
        actionType: String,
        citizenKk: String,
        payload: String,
        nonce: Long
    ): String {
        val rawData = "$index-$timestamp-$previousHash-$actionType-$citizenKk-$payload-$nonce"
        return sha256(rawData)
    }

    fun calculateBlockHash(block: Block): String {
        return calculateBlockHash(
            index = block.index,
            timestamp = block.timestamp,
            previousHash = block.previousHash,
            actionType = block.actionType,
            citizenKk = block.citizenKk,
            payload = block.fullDataPayload,
            nonce = block.nonce
        )
    }

    fun mineBlock(
        index: Long,
        previousHash: String,
        actionType: String,
        citizenKk: String,
        citizenName: String,
        payloadSummary: String,
        fullDataPayload: String,
        validatorNode: String = "Node-RT03-Ketua",
        difficulty: String = DEFAULT_DIFFICULTY
    ): Block {
        val timestamp = System.currentTimeMillis()
        var nonce = 0L
        var hash = ""

        // Proof of Work simulation
        while (true) {
            hash = calculateBlockHash(
                index = index,
                timestamp = timestamp,
                previousHash = previousHash,
                actionType = actionType,
                citizenKk = citizenKk,
                payload = fullDataPayload,
                nonce = nonce
            )
            if (hash.startsWith(difficulty)) {
                break
            }
            nonce++
        }

        return Block(
            index = index,
            timestamp = timestamp,
            previousHash = previousHash,
            hash = hash,
            nonce = nonce,
            actionType = actionType,
            citizenKk = citizenKk,
            citizenName = citizenName,
            payloadSummary = payloadSummary,
            fullDataPayload = fullDataPayload,
            validatorNode = validatorNode,
            isCorrupted = false
        )
    }

    fun createGenesisBlock(): Block {
        return mineBlock(
            index = 0L,
            previousHash = GENESIS_PREV_HASH,
            actionType = "GENESIS",
            citizenKk = "0000000000000000",
            citizenName = "SISTEM BUKU BESAR RT 03",
            payloadSummary = "Inisialisasi Blockchain Kependudukan Rukun Tetangga 03 / RW 07",
            fullDataPayload = "{\"network\":\"RT03-Sukamaju-Chain\",\"protocol\":\"v1.0\",\"status\":\"INITIALIZED\"}",
            validatorNode = "Genesis-Consensus-Node"
        )
    }

    fun validateChain(blocks: List<Block>): ChainAuditReport {
        if (blocks.isEmpty()) {
            return ChainAuditReport(
                isValid = true,
                totalBlocks = 0,
                logs = listOf("Rantai blok kosong (belum ada transaksi).")
            )
        }

        val sortedBlocks = blocks.sortedBy { it.index }
        val logs = mutableListOf<String>()

        // Check Genesis Block
        val genesis = sortedBlocks[0]
        if (genesis.index != 0L) {
            return ChainAuditReport(
                isValid = false,
                totalBlocks = sortedBlocks.size,
                corruptedBlockIndex = genesis.index,
                failureReason = "Blok pertama bukan Genesis Block (Index != 0)",
                logs = listOf("Kegagalan: Index blok awal adalah ${genesis.index}, seharusnya 0.")
            )
        }

        if (genesis.isCorrupted) {
            return ChainAuditReport(
                isValid = false,
                totalBlocks = sortedBlocks.size,
                corruptedBlockIndex = 0L,
                failureReason = "Genesis Block terdeteksi telah dimanipulasi secara ilegal!",
                logs = listOf("Blok #0 (Genesis) tanda tangan data tidak cocok dengan data asli.")
            )
        }

        val calculatedGenesisHash = calculateBlockHash(genesis)
        if (calculatedGenesisHash != genesis.hash) {
            return ChainAuditReport(
                isValid = false,
                totalBlocks = sortedBlocks.size,
                corruptedBlockIndex = 0L,
                failureReason = "Integritas Hash Genesis Block rusak: Dihitung=$calculatedGenesisHash != Tercatat=${genesis.hash}",
                logs = listOf("Hash blok Genesis tidak valid!")
            )
        }

        logs.add("✓ Blok #0 (Genesis): Terverifikasi sah. Hash: ${formatShortHash(genesis.hash)}")

        // Validate sequence
        for (i in 1 until sortedBlocks.size) {
            val currentBlock = sortedBlocks[i]
            val prevBlock = sortedBlocks[i - 1]

            if (currentBlock.index != prevBlock.index + 1) {
                val error = "Urutan index blok terputus di #${currentBlock.index} (sebelumnya #${prevBlock.index})"
                logs.add("✗ $error")
                return ChainAuditReport(
                    isValid = false,
                    totalBlocks = sortedBlocks.size,
                    corruptedBlockIndex = currentBlock.index,
                    failureReason = error,
                    logs = logs
                )
            }

            if (currentBlock.isCorrupted) {
                val error = "Blok #${currentBlock.index} terdeteksi dimanipulasi data payloadnya (Tampered Data)!"
                logs.add("✗ $error")
                return ChainAuditReport(
                    isValid = false,
                    totalBlocks = sortedBlocks.size,
                    corruptedBlockIndex = currentBlock.index,
                    failureReason = error,
                    logs = logs
                )
            }

            // Check previous hash pointer
            if (currentBlock.previousHash != prevBlock.hash) {
                val error = "Blok #${currentBlock.index} memiliki Previous Hash yang TIDAK cocok dengan Hash Blok #${prevBlock.index}!"
                logs.add("✗ $error (Tercatat: ${formatShortHash(currentBlock.previousHash)}, Hash Sebelumnya: ${formatShortHash(prevBlock.hash)})")
                return ChainAuditReport(
                    isValid = false,
                    totalBlocks = sortedBlocks.size,
                    corruptedBlockIndex = currentBlock.index,
                    failureReason = error,
                    logs = logs
                )
            }

            // Recalculate hash
            val calculatedHash = calculateBlockHash(currentBlock)
            if (calculatedHash != currentBlock.hash) {
                val error = "Hash Blok #${currentBlock.index} tidak valid! Dihitung=$calculatedHash vs Disimpan=${currentBlock.hash}"
                logs.add("✗ $error")
                return ChainAuditReport(
                    isValid = false,
                    totalBlocks = sortedBlocks.size,
                    corruptedBlockIndex = currentBlock.index,
                    failureReason = error,
                    logs = logs
                )
            }

            logs.add("✓ Blok #${currentBlock.index} [${currentBlock.actionType}]: Terverifikasi sah. Hash: ${formatShortHash(currentBlock.hash)}")
        }

        return ChainAuditReport(
            isValid = true,
            totalBlocks = sortedBlocks.size,
            corruptedBlockIndex = null,
            failureReason = null,
            logs = logs
        )
    }

    fun formatShortHash(hash: String): String {
        if (hash.length <= 16) return hash
        return "${hash.take(8)}...${hash.takeLast(6)}"
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
}
