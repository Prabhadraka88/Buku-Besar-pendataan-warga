package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Block
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Query("SELECT * FROM blockchain_ledger ORDER BY `index` ASC")
    fun getAllBlocks(): Flow<List<Block>>

    @Query("SELECT * FROM blockchain_ledger ORDER BY `index` ASC")
    suspend fun getAllBlocksSync(): List<Block>

    @Query("SELECT * FROM blockchain_ledger ORDER BY `index` DESC LIMIT 1")
    suspend fun getLatestBlock(): Block?

    @Query("SELECT * FROM blockchain_ledger WHERE `index` = :index LIMIT 1")
    suspend fun getBlockByIndex(index: Long): Block?

    @Query("SELECT * FROM blockchain_ledger WHERE citizenKk = :citizenKk ORDER BY `index` DESC")
    fun getBlocksByCitizenKk(citizenKk: String): Flow<List<Block>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: Block)

    @Update
    suspend fun updateBlock(block: Block)

    @Query("UPDATE blockchain_ledger SET isCorrupted = :isCorrupted, fullDataPayload = :tamperedPayload WHERE `index` = :index")
    suspend fun tamperBlock(index: Long, isCorrupted: Boolean, tamperedPayload: String)

    @Query("DELETE FROM blockchain_ledger")
    suspend fun clearAllBlocks()

    @Query("SELECT COUNT(*) FROM blockchain_ledger")
    fun getBlockCount(): Flow<Int>
}
