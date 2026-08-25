package com.studymate.sm.cid.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keuangan")
data class KeuanganEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tanggal: Long = System.currentTimeMillis(),
    val uangSangu: Long = 0,
    val pengeluaran: Long = 0,
    val keterangan: String = ""
)
