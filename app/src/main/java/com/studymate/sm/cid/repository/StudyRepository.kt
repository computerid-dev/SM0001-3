package com.studymate.sm.cid.repository

import com.studymate.sm.cid.data.AppDatabase
import com.studymate.sm.cid.data.entity.*

/**
 * Repository tunggal yang membungkus seluruh DAO Room.
 * Semua ViewModel mengakses data lewat kelas ini agar sumber data terpusat
 * dan mudah dipakai ulang untuk backup / import JSON.
 */
class StudyRepository(private val db: AppDatabase) {

    // Kategori
    fun observeKategori() = db.kategoriDao().observeAll()
    suspend fun getKategoriList() = db.kategoriDao().getAll()
    suspend fun saveKategori(item: Kategori) = db.kategoriDao().insert(item)
    suspend fun updateKategori(item: Kategori) = db.kategoriDao().update(item)
    suspend fun deleteKategori(item: Kategori) = db.kategoriDao().delete(item)

    // Pelajaran
    fun observePelajaran() = db.pelajaranDao().observeAll()
    suspend fun getPelajaranList() = db.pelajaranDao().getAll()
    suspend fun getPelajaranById(id: Long) = db.pelajaranDao().getById(id)
    suspend fun savePelajaran(item: Pelajaran) = db.pelajaranDao().insert(item)
    suspend fun updatePelajaran(item: Pelajaran) = db.pelajaranDao().update(item)
    suspend fun deletePelajaran(item: Pelajaran) = db.pelajaranDao().delete(item)

    // Jadwal
    fun observeJadwal() = db.jadwalDao().observeAll()
    fun observeJadwalByHari(hari: String) = db.jadwalDao().observeByHari(hari)
    suspend fun getJadwalList() = db.jadwalDao().getAll()
    suspend fun saveJadwal(item: JadwalItem) = db.jadwalDao().insert(item)
    suspend fun updateJadwal(item: JadwalItem) = db.jadwalDao().update(item)
    suspend fun deleteJadwal(item: JadwalItem) = db.jadwalDao().delete(item)

    // Catatan Materi
    fun observeMateri() = db.catatanMateriDao().observeAll()
    suspend fun getMateriList() = db.catatanMateriDao().getAll()
    suspend fun saveMateri(item: CatatanMateri) = db.catatanMateriDao().insert(item)
    suspend fun updateMateri(item: CatatanMateri) = db.catatanMateriDao().update(item)
    suspend fun deleteMateri(item: CatatanMateri) = db.catatanMateriDao().delete(item)

    // Tugas
    fun observeTugas() = db.tugasDao().observeAll()
    fun observeTugasUpcoming(limit: Int = 5) = db.tugasDao().observeUpcoming(limit)
    suspend fun getTugasList() = db.tugasDao().getAll()
    suspend fun saveTugas(item: Tugas) = db.tugasDao().insert(item)
    suspend fun updateTugas(item: Tugas) = db.tugasDao().update(item)
    suspend fun deleteTugas(item: Tugas) = db.tugasDao().delete(item)

    // Ujian
    fun observeUjian() = db.ujianDao().observeAll()
    fun observeUjianUpcoming(from: Long, limit: Int = 5) = db.ujianDao().observeUpcoming(from, limit)
    suspend fun getUjianList() = db.ujianDao().getAll()
    suspend fun saveUjian(item: Ujian) = db.ujianDao().insert(item)
    suspend fun updateUjian(item: Ujian) = db.ujianDao().update(item)
    suspend fun deleteUjian(item: Ujian) = db.ujianDao().delete(item)

    // Keuangan
    fun observeKeuangan() = db.keuanganDao().observeAll()
    suspend fun getKeuanganList() = db.keuanganDao().getAll()
    suspend fun saveKeuangan(item: KeuanganEntry) = db.keuanganDao().insert(item)
    suspend fun updateKeuangan(item: KeuanganEntry) = db.keuanganDao().update(item)
    suspend fun deleteKeuangan(item: KeuanganEntry) = db.keuanganDao().delete(item)

    // Kalender
    fun observeKalender() = db.kalenderDao().observeAll()
    suspend fun getKalenderList() = db.kalenderDao().getAll()
    suspend fun saveKalender(item: KalenderEvent) = db.kalenderDao().insert(item)
    suspend fun updateKalender(item: KalenderEvent) = db.kalenderDao().update(item)
    suspend fun deleteKalender(item: KalenderEvent) = db.kalenderDao().delete(item)

    // Target Belajar
    fun observeTarget() = db.targetBelajarDao().observeAll()
    suspend fun getTargetList() = db.targetBelajarDao().getAll()
    suspend fun saveTarget(item: TargetBelajar) = db.targetBelajarDao().insert(item)
    suspend fun updateTarget(item: TargetBelajar) = db.targetBelajarDao().update(item)
    suspend fun deleteTarget(item: TargetBelajar) = db.targetBelajarDao().delete(item)

    // Reset seluruh data (dipakai saat import JSON / reset dari Pengaturan)
    suspend fun clearAll() {
        getKategoriList().forEach { deleteKategori(it) }
        getPelajaranList().forEach { deletePelajaran(it) }
        getJadwalList().forEach { deleteJadwal(it) }
        getMateriList().forEach { deleteMateri(it) }
        getTugasList().forEach { deleteTugas(it) }
        getUjianList().forEach { deleteUjian(it) }
        getKeuanganList().forEach { deleteKeuangan(it) }
        getKalenderList().forEach { deleteKalender(it) }
        getTargetList().forEach { deleteTarget(it) }
    }

    companion object {
        @Volatile
        private var INSTANCE: StudyRepository? = null

        fun getInstance(db: AppDatabase): StudyRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StudyRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }
}
