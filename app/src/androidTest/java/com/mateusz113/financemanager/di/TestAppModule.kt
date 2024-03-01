package com.mateusz113.financemanager.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mateusz113.financemanager.data.repository.FakePaymentRepository
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Singleton
    fun providePaymentRepository(): PaymentRepository {
        return FakePaymentRepository()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("TestSharedPrefs", Context.MODE_PRIVATE)
    }
}