package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val judul: String,
    val pelajaranId: Long? = null,
    val deskripsi: String = "",
    val tanggalDibuat: Long = System.currentTimeMillis(),
    val deadline: Long? = null,
    val selesai: Boolean = false,
    val catatan: String = ""
)
