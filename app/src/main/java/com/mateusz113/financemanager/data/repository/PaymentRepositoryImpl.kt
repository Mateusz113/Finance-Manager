package com.mateusz113.financemanager.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mateusz113.financemanager.data.mapper.toPaymentDetails
import com.mateusz113.financemanager.data.mapper.toPaymentDetailsDto
import com.mateusz113.financemanager.data.mapper.toPaymentListing
import com.mateusz113.financemanager.data.mapper.toPaymentListingDto
import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

    //TODO: Fix references to the database after changing the structure

    override suspend fun getPaymentListings(): Flow<Resource<List<PaymentListing>>> {
        return flow {
            emit(Resource.Loading(true))

            var result: Resource<List<PaymentListing>> = Resource.Error(message = "Error occurred")
            val paymentListingsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsListings")

            paymentListingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val paymentListings = mutableListOf<PaymentListingDto>()
                    snapshot.children.forEach { listingSnapshot ->
                        val paymentListing = listingSnapshot.getValue(PaymentListingDto::class.java)
                        paymentListing?.let { listing ->
                            paymentListings.add(listing.copy(id = listingSnapshot.key))
                        }
                    }
                    result = Resource.Success(
                        data = paymentListings.map {
                            it.toPaymentListing()
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    result = Resource.Error(message = error.message)
                }
            })

            emit(result)
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getPaymentDetails(id: String): Flow<Resource<PaymentDetails>> {
        return flow {
            emit(Resource.Loading(true))

            var result: Resource<PaymentDetails> = Resource.Error(message = "Error occurred")
            val paymentsDetailsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsDetails/$id")

            paymentsDetailsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val paymentDetailsDto = snapshot.getValue(PaymentDetailsDto::class.java)
                    result = Resource.Success(
                        data = paymentDetailsDto?.toPaymentDetails()
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    result = Resource.Error(message = error.message)
                }
            })

            emit(result)
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun addPayment(payment: PaymentDetails) {
        val newPaymentId = firebaseDatabase.getReference("paymentDetails").push().key

        val paymentDetailsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails")
                .child(newPaymentId!!)
        val paymentListingsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentListings")
                .child(newPaymentId)

        val paymentDetailsDto = payment.toPaymentDetailsDto()
        val paymentListingDto = payment.toPaymentListingDto()

        paymentDetailsRef.setValue(paymentDetailsDto)
        paymentListingsRef.setValue(paymentListingDto)
    }

    override suspend fun removePayment(id: String) {
        val paymentDetailRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails/$id")
        val paymentListingRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsListings/$id")
        paymentDetailRef.removeValue()
        paymentListingRef.removeValue()
    }
}