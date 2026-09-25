package com.example.model

data class BlockchainNode(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val ipAddress: String,
    val isOnline: Boolean,
    val syncedBlockHeight: Long,
    val latencyMs: Int,
    val isConsensusLeader: Boolean = false
)

data class ChainAuditReport(
    val isValid: Boolean,
    val totalBlocks: Int,
    val corruptedBlockIndex: Long? = null,
    val auditTimestamp: Long = System.currentTimeMillis(),
    val failureReason: String? = null,
    val logs: List<String> = emptyList()
)
