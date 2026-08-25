package com.studymate.sm.cid.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {

    private val tanggalFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    private val tanggalWaktuFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun formatTanggal(millis: Long): String = tanggalFormat.format(Date(millis))

    fun formatTanggalWaktu(millis: Long): String = tanggalWaktuFormat.format(Date(millis))

    fun formatRupiah(nilai: Long): String {
        val formatted = String.format(Locale("id", "ID"), "%,d", nilai).replace(",", ".")
        return "Rp$formatted"
    }

    fun hariIni(): Long = System.currentTimeMillis()

    val namaHari = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
}
