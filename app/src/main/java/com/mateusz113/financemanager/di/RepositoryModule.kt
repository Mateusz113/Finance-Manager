package com.mateusz113.financemanager.di

import com.mateusz113.financemanager.data.repository.PaymentRepositoryImpl
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepository: PaymentRepositoryImpl
    ): PaymentRepository
}