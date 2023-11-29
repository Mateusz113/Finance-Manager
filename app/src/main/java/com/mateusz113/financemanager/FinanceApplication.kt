package com.mateusz113.financemanager

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinanceApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //Ensure Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}