package com.studymate.sm.cid.util

import com.studymate.sm.cid.data.entity.*

/**
 * Struktur data yang dipakai untuk backup & import JSON (fitur 7 & 8).
 * Mencakup seluruh tabel: kategori, pelajaran, jadwal, materi, tugas,
 * ujian, keuangan, kalender, dan target belajar.
 */
data class BackupData(
    val versiAplikasi: String = "1.0.0",
    val tanggalBackup: Long = System.currentTimeMillis(),
    val kategori: List<Kategori> = emptyList(),
    val pelajaran: List<Pelajaran> = emptyList(),
    val jadwal: List<JadwalItem> = emptyList(),
    val catatanMateri: List<CatatanMateri> = emptyList(),
    val tugas: List<Tugas> = emptyList(),
    val ujian: List<Ujian> = emptyList(),
    val keuangan: List<KeuanganEntry> = emptyList(),
    val kalender: List<KalenderEvent> = emptyList(),
    val targetBelajar: List<TargetBelajar> = emptyList()
)
