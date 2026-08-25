package com.studymate.sm.cid

import android.app.Application
import com.studymate.sm.cid.data.AppDatabase
import com.studymate.sm.cid.repository.StudyRepository

class StudyMateApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: StudyRepository by lazy { StudyRepository.getInstance(database) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: StudyMateApp
            private set
    }
}
