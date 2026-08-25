package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.JadwalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface JadwalDao {
    @Query("SELECT * FROM jadwal ORDER BY hari ASC, jamMulai ASC")
    fun observeAll(): Flow<List<JadwalItem>>

    @Query("SELECT * FROM jadwal WHERE hari = :hari ORDER BY jamMulai ASC")
    fun observeByHari(hari: String): Flow<List<JadwalItem>>

    @Query("SELECT * FROM jadwal ORDER BY hari ASC, jamMulai ASC")
    suspend fun getAll(): List<JadwalItem>

    @Query("SELECT * FROM jadwal WHERE pelajaranId = :pelajaranId")
    suspend fun getByPelajaran(pelajaranId: Long): List<JadwalItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: JadwalItem): Long

    @Update
    suspend fun update(item: JadwalItem)

    @Delete
    suspend fun delete(item: JadwalItem)
}
