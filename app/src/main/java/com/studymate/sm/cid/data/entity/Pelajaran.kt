package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pelajaran")
data class Pelajaran(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val guru: String = "",
    val kategoriId: Long? = null,
    val catatan: String = ""
)
