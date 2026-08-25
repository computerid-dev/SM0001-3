package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.KalenderEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface KalenderDao {
    @Query("SELECT * FROM kalender ORDER BY tanggal ASC")
    fun observeAll(): Flow<List<KalenderEvent>>

    @Query("SELECT * FROM kalender ORDER BY tanggal ASC")
    suspend fun getAll(): List<KalenderEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KalenderEvent): Long

    @Update
    suspend fun update(item: KalenderEvent)

    @Delete
    suspend fun delete(item: KalenderEvent)
}
