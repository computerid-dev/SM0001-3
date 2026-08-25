package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.Tugas
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {
    @Query("SELECT * FROM tugas ORDER BY selesai ASC, deadline ASC")
    fun observeAll(): Flow<List<Tugas>>

    @Query("SELECT * FROM tugas WHERE selesai = 0 ORDER BY deadline ASC LIMIT :limit")
    fun observeUpcoming(limit: Int = 5): Flow<List<Tugas>>

    @Query("SELECT * FROM tugas ORDER BY deadline ASC")
    suspend fun getAll(): List<Tugas>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Tugas): Long

    @Update
    suspend fun update(item: Tugas)

    @Delete
    suspend fun delete(item: Tugas)
}
