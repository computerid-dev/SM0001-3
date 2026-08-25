package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kategori")
data class Kategori(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String
)
