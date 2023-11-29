package com.mateusz113.financemanager.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mateusz113.financemanager.data.mapper.toPaymentDetails
import com.mateusz113.financemanager.data.mapper.toPaymentDetailsDto
import com.mateusz113.financemanager.data.mapper.toPaymentListing
import com.mateusz113.financemanager.data.mapper.toPaymentListingDto
import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

val LISTINGS_GET_TAG = "LISTINGS_GET"
val DETAILS_GET_TAG = "DETAILS_GET"
val PAYMENT_ADD_TAG = "PAYMENT_ADD"
val PAYMENT_REMOVE_TAG = "PAYMENT_REMOVE"

class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val firebaseStorage = FirebaseStorage.getInstance()

    override suspend fun getPaymentListings(): Flow<Resource<List<PaymentListing>>> {
        return flow {
            emit(Resource.Loading(true))

            var result: Resource<List<PaymentListing>> = Resource.Error(message = "Error occurred")
            val paymentListingsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsListings")

            paymentListingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val paymentListings = mutableListOf<PaymentListing>()
                    snapshot.children.forEach { listingSnapshot ->
                        val paymentListingDto =
                            listingSnapshot.getValue(PaymentListingDto::class.java)
                        paymentListingDto?.let { listing ->
                            paymentListings.add(
                                listing.copy(id = listingSnapshot.key).toPaymentListing()
                            )
                            //Log the information about the listing retrieved
                            Log.d(LISTINGS_GET_TAG, "Listing retrieved: $listing")
                        }
                    }

                    result = Resource.Success(data = paymentListings)
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
                    //Log the data about the payment details retrieved
                    Log.d(DETAILS_GET_TAG, "Details retrieved: $paymentDetailsDto")
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

    override suspend fun addPayment(payment: NewPaymentDetails) {
        val newPaymentId = firebaseDatabase.getReference("paymentDetails").push().key

        val paymentDetailsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails")
                .child(newPaymentId!!)
        val paymentListingsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentListings")
                .child(newPaymentId)
        val storageReference =
            firebaseStorage.getReference("users/$currentUserId/payments/$newPaymentId/images/")

        val imageUrls = mutableListOf<String>()

        payment.photoUris.forEachIndexed { i, uri ->
            val imageStorageReference = storageReference.child("image_$i.jpg")
            val uploadTask = imageStorageReference.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                }
                // Continue with the task to get the download URL
                imageStorageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result.toString()
                    imageUrls.add(downloadUrl)
                    //Proceed only if all the images were uploaded
                    if (imageUrls.size == payment.photoUris.size) {
                        var paymentDetailsDto = payment.toPaymentDetailsDto()
                        val paymentListingDto = payment.toPaymentListingDto()
                        paymentDetailsDto = paymentDetailsDto.copy(photoUrls = imageUrls)

                        paymentDetailsRef.setValue(paymentDetailsDto).addOnFailureListener { e ->
                            e.message?.let { Log.d(PAYMENT_REMOVE_TAG, it) }
                        }
                        paymentListingsRef.setValue(paymentListingDto).addOnFailureListener { e ->
                            e.message?.let { Log.d(PAYMENT_REMOVE_TAG, it) }
                        }
                    }
                } else {
                    task.exception?.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                }
            }
        }
    }

    override suspend fun removePayment(id: String) {
        val paymentDetailRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails/$id")
        val paymentListingRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsListings/$id")
        val storageReference =
            firebaseStorage.getReference("users/$currentUserId/payments/$id")
        storageReference.delete().addOnFailureListener { e ->
            e.message?.let { Log.d(PAYMENT_REMOVE_TAG, it) }
        }
        paymentDetailRef.removeValue().addOnFailureListener { e ->
            e.message?.let { Log.d(PAYMENT_REMOVE_TAG, it) }
        }
        paymentListingRef.removeValue().addOnFailureListener { e ->
            e.message?.let { Log.d(PAYMENT_REMOVE_TAG, it) }
        }
    }
}