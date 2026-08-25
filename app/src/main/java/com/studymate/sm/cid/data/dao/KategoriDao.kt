package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.Kategori
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    fun observeAll(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    suspend fun getAll(): List<Kategori>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kategori: Kategori): Long

    @Update
    suspend fun update(kategori: Kategori)

    @Delete
    suspend fun delete(kategori: Kategori)
}
