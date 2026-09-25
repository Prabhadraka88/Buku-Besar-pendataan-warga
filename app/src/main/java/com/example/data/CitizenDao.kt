package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Citizen
import com.example.model.CitizenCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenDao {
    @Query("SELECT * FROM citizens ORDER BY updatedAt DESC")
    fun getAllCitizens(): Flow<List<Citizen>>

    @Query("SELECT * FROM citizens WHERE id = :id LIMIT 1")
    fun getCitizenById(id: Long): Flow<Citizen?>

    @Query("SELECT * FROM citizens WHERE id = :id LIMIT 1")
    suspend fun getCitizenByIdSync(id: Long): Citizen?

    @Query("SELECT * FROM citizens WHERE kkNumber = :kkNumber LIMIT 1")
    suspend fun getCitizenByKk(kkNumber: String): Citizen?

    @Query("SELECT * FROM citizens WHERE category = :category ORDER BY fullName ASC")
    fun getCitizensByCategory(category: CitizenCategory): Flow<List<Citizen>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizen(citizen: Citizen): Long

    @Update
    suspend fun updateCitizen(citizen: Citizen)

    @Delete
    suspend fun deleteCitizen(citizen: Citizen)

    @Query("DELETE FROM citizens WHERE id = :id")
    suspend fun deleteCitizenById(id: Long)

    @Query("SELECT COUNT(*) FROM citizens")
    fun getCitizenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM citizens WHERE category = :category")
    fun getCitizenCountByCategory(category: CitizenCategory): Flow<Int>
}
