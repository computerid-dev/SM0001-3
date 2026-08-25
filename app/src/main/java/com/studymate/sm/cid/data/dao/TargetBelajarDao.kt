package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.TargetBelajar
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetBelajarDao {
    @Query("SELECT * FROM target_belajar ORDER BY selesai ASC, id DESC")
    fun observeAll(): Flow<List<TargetBelajar>>

    @Query("SELECT * FROM target_belajar ORDER BY id DESC")
    suspend fun getAll(): List<TargetBelajar>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TargetBelajar): Long

    @Update
    suspend fun update(item: TargetBelajar)

    @Delete
    suspend fun delete(item: TargetBelajar)
}
