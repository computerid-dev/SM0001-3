package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kalender")
data class KalenderEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val tanggal: Long = System.currentTimeMillis(),
    val deskripsi: String = "",
    val kategori: String = "Umum"
)
