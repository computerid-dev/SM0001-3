package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.KeuanganEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface KeuanganDao {
    @Query("SELECT * FROM keuangan ORDER BY tanggal DESC")
    fun observeAll(): Flow<List<KeuanganEntry>>

    @Query("SELECT * FROM keuangan ORDER BY tanggal DESC LIMIT 1")
    fun observeToday(): Flow<KeuanganEntry?>

    @Query("SELECT * FROM keuangan ORDER BY tanggal DESC")
    suspend fun getAll(): List<KeuanganEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: KeuanganEntry): Long

    @Update
    suspend fun update(item: KeuanganEntry)

    @Delete
    suspend fun delete(item: KeuanganEntry)
}
