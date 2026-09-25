package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CitizenLedgerRepository
import com.example.model.Block
import com.example.model.BlockchainNode
import com.example.model.ChainAuditReport
import com.example.model.Citizen
import com.example.model.CitizenCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class MiningState {
    data object Idle : MiningState()
    data class Mining(val step: String) : MiningState()
    data class Success(val block: Block, val isNew: Boolean) : MiningState()
    data class Error(val message: String) : MiningState()
}

class CitizenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CitizenLedgerRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = CitizenLedgerRepository(db.citizenDao(), db.blockDao())
    }

    val allCitizens: StateFlow<List<Citizen>> = repository.allCitizens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBlocks: StateFlow<List<Block>> = repository.allBlocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow<CitizenCategory?>(null)
    val categoryFilter: StateFlow<CitizenCategory?> = _categoryFilter.asStateFlow()

    private val _miningState = MutableStateFlow<MiningState>(MiningState.Idle)
    val miningState: StateFlow<MiningState> = _miningState.asStateFlow()

    private val _auditReport = MutableStateFlow<ChainAuditReport?>(null)
    val auditReport: StateFlow<ChainAuditReport?> = _auditReport.asStateFlow()

    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val filteredCitizens: StateFlow<List<Citizen>> = combine(
        allCitizens,
        _searchQuery,
        _categoryFilter
    ) { citizens, query, category ->
        citizens.filter { citizen ->
            val matchQuery = query.isBlank() ||
                    citizen.fullName.contains(query, ignoreCase = true) ||
                    citizen.kkNumber.contains(query, ignoreCase = true) ||
                    citizen.nikHead.contains(query, ignoreCase = true) ||
                    citizen.addressStreet.contains(query, ignoreCase = true) ||
                    citizen.phone.contains(query, ignoreCase = true)

            val matchCategory = category == null || citizen.category == category

            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nodes: StateFlow<List<BlockchainNode>> = allBlocks.combine(_isAuditing) { blocks, _ ->
        repository.getDecentralizedNodes(blocks.size.toLong())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: CitizenCategory?) {
        _categoryFilter.value = category
    }

    fun registerCitizen(citizen: Citizen) {
        viewModelScope.launch {
            try {
                _miningState.value = MiningState.Mining("Memverifikasi NIK & No KK...")
                delay(400)
                _miningState.value = MiningState.Mining("Menghitung Hash SHA-256 & Proof-of-Work Nonce...")
                delay(600)
                val newBlock = repository.registerCitizen(citizen)
                _miningState.value = MiningState.Success(newBlock, isNew = true)
                _message.value = "Berhasil mencatat KK baru pada Blok #${newBlock.index}!"
            } catch (e: Exception) {
                _miningState.value = MiningState.Error(e.message ?: "Gagal menambang blok transaksi.")
            }
        }
    }

    fun updateCitizen(citizen: Citizen, previousCategory: CitizenCategory? = null) {
        viewModelScope.launch {
            try {
                _miningState.value = MiningState.Mining("Mencatat mutasi data kependudukan...")
                delay(400)
                _miningState.value = MiningState.Mining("Konsensus penambangan blok baru...")
                delay(500)
                val block = repository.updateCitizenData(citizen, previousCategory)
                _miningState.value = MiningState.Success(block, isNew = false)
                _message.value = "Data warga diperbarui secara permanen pada Blok #${block.index}."
            } catch (e: Exception) {
                _miningState.value = MiningState.Error(e.message ?: "Gagal memperbarui data warga.")
            }
        }
    }

    fun deleteCitizen(citizen: Citizen) {
        viewModelScope.launch {
            try {
                _miningState.value = MiningState.Mining("Mencatat mutasi penghapusan pada blockchain...")
                delay(500)
                val block = repository.deleteCitizen(citizen)
                _miningState.value = MiningState.Success(block, isNew = false)
                _message.value = "Penghapusan tercatat permanen di Buku Besar Blok #${block.index}."
            } catch (e: Exception) {
                _miningState.value = MiningState.Error(e.message ?: "Gagal menghapus data warga.")
            }
        }
    }

    fun runBlockchainAudit() {
        viewModelScope.launch {
            _isAuditing.value = true
            delay(600) // Visual feedback for user
            val report = repository.auditBlockchain()
            _auditReport.value = report
            _isAuditing.value = false
            if (report.isValid) {
                _message.value = "Audit Selesai: Seluruh ${report.totalBlocks} blok 100% valid dan aman!"
            } else {
                _message.value = "PERINGATAN: Terdeteksi manipulasi pada Blok #${report.corruptedBlockIndex}!"
            }
        }
    }

    fun simulateTamper(blockIndex: Long) {
        viewModelScope.launch {
            _miningState.value = MiningState.Mining("Mensimulasikan manipulasi ilegal data...")
            delay(400)
            repository.simulateTampering(blockIndex)
            _miningState.value = MiningState.Idle
            _message.value = "Simulasi: Blok #$blockIndex telah dimanipulasi secara ilegal! Jalankan Audit untuk melihat deteksi sistem."
            // Auto run audit to demonstrate detection
            runBlockchainAudit()
        }
    }

    fun restoreBlockchain() {
        viewModelScope.launch {
            _miningState.value = MiningState.Mining("Memulihkan rantai dari konsensus node cadangan...")
            delay(700)
            repository.restoreChainFromConsensus()
            _miningState.value = MiningState.Idle
            _message.value = "Konsensus Terpulihkan: Rantai blok diperbaiki 100% ke kondisi valid!"
            runBlockchainAudit()
        }
    }

    fun clearMiningState() {
        _miningState.value = MiningState.Idle
    }

    fun clearMessage() {
        _message.value = null
    }
}
