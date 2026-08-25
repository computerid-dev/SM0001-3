package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.Ujian
import kotlinx.coroutines.flow.Flow

@Dao
interface UjianDao {
    @Query("SELECT * FROM ujian ORDER BY tanggal ASC")
    fun observeAll(): Flow<List<Ujian>>

    @Query("SELECT * FROM ujian WHERE tanggal >= :from ORDER BY tanggal ASC LIMIT :limit")
    fun observeUpcoming(from: Long, limit: Int = 5): Flow<List<Ujian>>

    @Query("SELECT * FROM ujian ORDER BY tanggal ASC")
    suspend fun getAll(): List<Ujian>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Ujian): Long

    @Update
    suspend fun update(item: Ujian)

    @Delete
    suspend fun delete(item: Ujian)
}
