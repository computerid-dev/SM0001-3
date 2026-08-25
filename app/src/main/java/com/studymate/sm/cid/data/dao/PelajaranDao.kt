package com.studymate.sm.cid.data.dao

import androidx.room.*
import com.studymate.sm.cid.data.entity.Pelajaran
import kotlinx.coroutines.flow.Flow

@Dao
interface PelajaranDao {
    @Query("SELECT * FROM pelajaran ORDER BY nama ASC")
    fun observeAll(): Flow<List<Pelajaran>>

    @Query("SELECT * FROM pelajaran ORDER BY nama ASC")
    suspend fun getAll(): List<Pelajaran>

    @Query("SELECT * FROM pelajaran WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Pelajaran?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pelajaran: Pelajaran): Long

    @Update
    suspend fun update(pelajaran: Pelajaran)

    @Delete
    suspend fun delete(pelajaran: Pelajaran)
}
