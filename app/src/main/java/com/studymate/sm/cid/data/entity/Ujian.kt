package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ujian")
data class Ujian(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val pelajaranId: Long? = null,
    val jenis: String = "Ulangan Harian",
    val tanggal: Long = System.currentTimeMillis(),
    val materi: String = "",
    val catatan: String = ""
)
