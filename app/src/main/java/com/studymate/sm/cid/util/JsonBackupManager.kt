package com.studymate.sm.cid.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.studymate.sm.cid.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JsonBackupManager(
    private val context: Context,
    private val repository: StudyRepository
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun buatBackup(): BackupData = withContext(Dispatchers.IO) {
        BackupData(
            kategori = repository.getKategoriList(),
            pelajaran = repository.getPelajaranList(),
            jadwal = repository.getJadwalList(),
            catatanMateri = repository.getMateriList(),
            tugas = repository.getTugasList(),
            ujian = repository.getUjianList(),
            keuangan = repository.getKeuanganList(),
            kalender = repository.getKalenderList(),
            targetBelajar = repository.getTargetList()
        )
    }

    suspend fun simpanKeUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val data = buatBackup()
            val json = gson.toJson(data)
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.toByteArray())
            } ?: return@withContext Result.failure(IllegalStateException("Tidak bisa membuka file tujuan"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bacaDariUri(uri: Uri): Result<BackupData> = withContext(Dispatchers.IO) {
        try {
            val text = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: return@withContext Result.failure(IllegalStateException("Tidak bisa membaca file"))
            val data = gson.fromJson(text, BackupData::class.java)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importDanTimpaData(data: BackupData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            repository.clearAll()
            data.kategori.forEach { repository.saveKategori(it) }
            data.pelajaran.forEach { repository.savePelajaran(it) }
            data.jadwal.forEach { repository.saveJadwal(it) }
            data.catatanMateri.forEach { repository.saveMateri(it) }
            data.tugas.forEach { repository.saveTugas(it) }
            data.ujian.forEach { repository.saveUjian(it) }
            data.keuangan.forEach { repository.saveKeuangan(it) }
            data.kalender.forEach { repository.saveKalender(it) }
            data.targetBelajar.forEach { repository.saveTarget(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
