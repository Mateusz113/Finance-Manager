package com.mateusz113.financemanager.di

import com.mateusz113.financemanager.util.SharedPreferencesSetup
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RepositoryModule::class])
@Singleton
interface SharedPrefsSetupComponent {
    fun getSharedPreferencesSetup(): SharedPreferencesSetup
}