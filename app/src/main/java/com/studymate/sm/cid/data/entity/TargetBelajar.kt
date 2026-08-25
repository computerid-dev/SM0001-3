package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "target_belajar")
data class TargetBelajar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val deskripsi: String = "",
    val targetJumlah: Int = 100,
    val progress: Int = 0,
    val selesai: Boolean = false
)
