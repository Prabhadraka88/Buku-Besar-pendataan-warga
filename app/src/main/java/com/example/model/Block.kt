package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blockchain_ledger")
data class Block(
    @PrimaryKey
    val index: Long,
    val timestamp: Long,
    val previousHash: String,
    val hash: String,
    val nonce: Long,
    val actionType: String, // GENESIS, REGISTRASI_KK, PERBARUI_DATA, MUTASI_KATEGORI, STATUS_IURAN, HAPUS_WARGA
    val citizenKk: String,
    val citizenName: String,
    val payloadSummary: String,
    val fullDataPayload: String,
    val validatorNode: String = "Node-RT03-Utama",
    val isCorrupted: Boolean = false
)
