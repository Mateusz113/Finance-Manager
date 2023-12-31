package com.mateusz113.financemanager.domain.repository

import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun getPaymentListings(): Flow<Resource<List<PaymentListing>>>
    suspend fun getPaymentListingsWithFilter(filterSettings: FilterSettings): Flow<Resource<List<PaymentListing>>>
    suspend fun getPaymentDetails(id: String): Flow<Resource<PaymentDetails>>
    suspend fun addPayment(payment: NewPaymentDetails)
    suspend fun editPayment(id: String, newPaymentDetails: NewPaymentDetails)
    suspend fun removePayment(id: String)
}