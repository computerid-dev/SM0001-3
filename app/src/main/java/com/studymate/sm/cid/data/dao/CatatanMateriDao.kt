package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.CatatanMateri
import kotlinx.coroutines.flow.Flow

@Dao
interface CatatanMateriDao {
    @Query("SELECT * FROM catatan_materi ORDER BY tanggalDibuat DESC")
    fun observeAll(): Flow<List<CatatanMateri>>

    @Query("SELECT * FROM catatan_materi ORDER BY tanggalDibuat DESC")
    suspend fun getAll(): List<CatatanMateri>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CatatanMateri): Long

    @Update
    suspend fun update(item: CatatanMateri)

    @Delete
    suspend fun delete(item: CatatanMateri)
}
