package com.mateusz113.financemanager.data.repository

import com.mateusz113.financemanager.data.mapper.toPaymentDetails
import com.mateusz113.financemanager.data.mapper.toPaymentListing
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.domain.model.TestPaymentInformation
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FakePaymentRepository : PaymentRepository {
    private val payments = mutableListOf<TestPaymentInformation>()
    override suspend fun getPaymentListings(): Flow<Resource<List<PaymentListing>>> {
        return flow {
            emit(
                Resource.Success(
                    data = payments.map {
                        it.toPaymentListing()
                    }
                )
            )
        }
    }

    override suspend fun getPaymentListingsWithFilter(filterSettings: FilterSettings): Flow<Resource<List<PaymentListing>>> {
        return flow {
            emit(
                Resource.Success(
                    data = payments.map { it.toPaymentListing() }.filter {
                        it.title.lowercase().contains(filterSettings.query.lowercase())
                                && (filterSettings.categories.isEmpty() ||
                                filterSettings.categories.contains(it.category))
                                && (filterSettings.minValue.isBlank() ||
                                filterSettings.minValue.toDouble() <= it.amount)
                                && (filterSettings.maxValue.isBlank() ||
                                filterSettings.maxValue.toDouble() >= it.amount)
                                && filterSettings.startDate <= it.date
                                && filterSettings.endDate >= it.date
                    }
                )
            )
        }
    }

    override suspend fun getPaymentDetails(id: String): Flow<Resource<PaymentDetails>> {
        return flow {
            val paymentDetails = payments.find { it.id == id }
            emit(
                paymentDetails?.let {
                    Resource.Success(
                        data = it.toPaymentDetails()
                    )
                } ?: Resource.Error(message = "Payment with given ID not found")
            )
        }
    }

    override suspend fun addPayment(payment: NewPaymentDetails) {
        payments.add(
            TestPaymentInformation(
                id = UUID.randomUUID().toString(),
                title = payment.title,
                description = payment.description,
                amount = payment.amount,
                date = payment.date,
                category = payment.category,
                photoUrls = payment.photoUrls,
                photoUris = payment.photoUris,
                deletedPhotos = payment.deletedPhotos
            )
        )
    }

    override suspend fun editPayment(id: String, newPaymentDetails: NewPaymentDetails) {
        val payment = payments.find { it.id == id }
        payment?.let {
            payments.remove(it)
            val updatedPayment = it.copy(
                title = newPaymentDetails.title,
                description = newPaymentDetails.description,
                amount = newPaymentDetails.amount,
                photoUris = newPaymentDetails.photoUris,
                photoUrls = newPaymentDetails.photoUrls,
                deletedPhotos = newPaymentDetails.deletedPhotos,
                date = newPaymentDetails.date,
                category = newPaymentDetails.category
            )
            payments.add(updatedPayment)
        } ?: println("Payment with given ID does not exist")
    }

    override suspend fun removePayment(id: String) {
        if (!payments.removeIf { it.id == id }) {
            println("Payment with given ID does not exist")
        }
    }
}
