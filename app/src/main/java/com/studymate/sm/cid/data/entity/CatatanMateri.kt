package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catatan_materi")
data class CatatanMateri(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val judul: String,
    val pelajaranId: Long? = null,
    val bab: String = "",
    val halaman: String = "",
    val isi: String = "",
    val catatan: String = "",
    val tanggalDibuat: Long = System.currentTimeMillis()
)
