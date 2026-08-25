package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jadwal")
data class JadwalItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pelajaranId: Long,
    val hari: String,
    val jamMulai: String,
    val jamSelesai: String,
    val catatan: String = ""
)
