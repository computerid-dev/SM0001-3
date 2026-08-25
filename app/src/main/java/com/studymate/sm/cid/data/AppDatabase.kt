package com.studymate.sm.cid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studymate.sm.cid.data.dao.*
import com.studymate.sm.cid.data.entity.*

@Database(
    entities = [
        Kategori::class,
        Pelajaran::class,
        JadwalItem::class,
        CatatanMateri::class,
        Tugas::class,
        Ujian::class,
        KeuanganEntry::class,
        KalenderEvent::class,
        TargetBelajar::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun kategoriDao(): KategoriDao
    abstract fun pelajaranDao(): PelajaranDao
    abstract fun jadwalDao(): JadwalDao
    abstract fun catatanMateriDao(): CatatanMateriDao
    abstract fun tugasDao(): TugasDao
    abstract fun ujianDao(): UjianDao
    abstract fun keuanganDao(): KeuanganDao
    abstract fun kalenderDao(): KalenderDao
    abstract fun targetBelajarDao(): TargetBelajarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studymate.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
