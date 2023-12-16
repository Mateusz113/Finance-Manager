package com.mateusz113.financemanager.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mateusz113.financemanager.data.mapper.toPaymentDetails
import com.mateusz113.financemanager.data.mapper.toPaymentDetailsDto
import com.mateusz113.financemanager.data.mapper.toPaymentListing
import com.mateusz113.financemanager.data.mapper.toPaymentListingDto
import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import javax.inject.Inject

const val PAYMENT_ADD_TAG = "PAYMENT_ADD"
const val PAYMENT_REMOVE_TAG = "PAYMENT_REMOVE"

class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {
    private val firebaseDatabase =
        FirebaseDatabase.getInstance("https://financemanager-aa563-default-rtdb.europe-west1.firebasedatabase.app")
    private val firebaseStorage = FirebaseStorage.getInstance()

    override suspend fun getPaymentListings(): Flow<Resource<List<PaymentListing>>> {
        return callbackFlow {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
            val paymentListingsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsListings")
            val paymentsListingsEventListener = paymentListingsRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val paymentListings = mutableListOf<PaymentListing>()
                        snapshot.children.forEach { listingSnapshot ->
                            val paymentListingDto =
                                listingSnapshot.getValue(PaymentListingDto::class.java)
                            paymentListingDto?.let { listing ->
                                listing.id = listingSnapshot.key
                                paymentListings.add(
                                    listing.toPaymentListing()
                                )
                            }
                        }
                        trySend(Resource.Success(data = paymentListings.toList()))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(message = error.message))
                    }
                }
            )
            // Cancel the listener when the flow is closed
            awaitClose { paymentListingsRef.removeEventListener(paymentsListingsEventListener) }
        }
            .onStart { emit(Resource.Loading(true)) }
            .onCompletion { throwable ->
                if (throwable == null) {
                    emit(Resource.Loading(false))
                } else {
                    Log.d("GET_DETAILS", throwable.toString())
                }
            }
    }

    override suspend fun getPaymentListingsWithFilter(filterSettings: FilterSettings): Flow<Resource<List<PaymentListing>>> {
        return callbackFlow {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
            val paymentListingsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsListings")
            val paymentsListingsEventListener = paymentListingsRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var paymentListings = mutableListOf<PaymentListing>()
                        snapshot.children.forEach { listingSnapshot ->
                            val paymentListingDto =
                                listingSnapshot.getValue(PaymentListingDto::class.java)
                            paymentListingDto?.let { listing ->
                                listing.id = listingSnapshot.key
                                paymentListings.add(
                                    listing.toPaymentListing()
                                )
                            }
                        }
                        trySend(
                            Resource.Success(
                                data = paymentListings.filter {
                                    it.title.lowercase().contains(filterSettings.query.lowercase())
                                            && (filterSettings.categories.isEmpty() ||
                                            filterSettings.categories.contains(it.category))
                                            && (filterSettings.minValue.isBlank() ||
                                            filterSettings.minValue.toFloat() <= it.amount)
                                            && (filterSettings.maxValue.isBlank() ||
                                            filterSettings.maxValue.toFloat() >= it.amount)
                                            && filterSettings.startDate <= it.date
                                            && filterSettings.endDate >= it.date
                                })
                        )
                        if (isActive) {
                            close()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(message = error.message))
                        if (isActive) {
                            close()
                        }
                    }
                }
            )
            // Cancel the listener when the flow is closed
            awaitClose { paymentListingsRef.removeEventListener(paymentsListingsEventListener) }
        }
            .onStart { emit(Resource.Loading(true)) }
            .onCompletion { throwable ->
                if (throwable == null) {
                    emit(Resource.Loading(false))
                } else {
                    Log.d("GET_DETAILS", throwable.toString())
                }
            }
    }

    override suspend fun getPaymentDetails(id: String): Flow<Resource<PaymentDetails>> {
        return callbackFlow {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
            val paymentsDetailsRef =
                firebaseDatabase.getReference("users/$currentUserId/paymentsDetails/$id")
            val paymentDetailsEventListener =
                paymentsDetailsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val paymentDetailsDto = snapshot.getValue(PaymentDetailsDto::class.java)
                        trySend(Resource.Success(data = paymentDetailsDto?.toPaymentDetails()))
                        //Close the scope after sending the result
                        if (isActive) {
                            close()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(message = error.message))
                        //Close the scope after sending the result
                        if (isActive) {
                            close()
                        }
                    }
                })

            //Cancel the listener when the flow is closed
            awaitClose {
                paymentsDetailsRef.removeEventListener(paymentDetailsEventListener)
            }
        }
            .onStart {
                emit(Resource.Loading(true))
            }
            .onCompletion { throwable ->
                if (throwable == null) {
                    emit(Resource.Loading(false))
                } else {
                    Log.d("GET_DETAILS", throwable.toString())
                }
            }
    }

    override suspend fun addPayment(payment: NewPaymentDetails) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        val newPaymentId = firebaseDatabase.getReference("paymentDetails").push().key

        val paymentDetailsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails")
                .child(newPaymentId!!)
        val paymentListingsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsListings")
                .child(newPaymentId)
        val storageReference =
            firebaseStorage.getReference("users/$currentUserId/payments/$newPaymentId/images/")

        val imageUrls = mutableListOf<String>()
        val paymentDetailsDto = payment.toPaymentDetailsDto()
        val paymentListingDto = payment.toPaymentListingDto()

        if (payment.photoUris.isNotEmpty()) {
            payment.photoUris.forEachIndexed { i, uri ->
                val imageStorageReference = storageReference.child("image_$i.jpg")
                val uploadTask = imageStorageReference.putFile(uri)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.message?.let {
                            Log.d(PAYMENT_ADD_TAG, it)
                        }
                    }
                    // Continue with the task to get the download URL
                    imageStorageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result.toString()
                        imageUrls.add(downloadUrl)

                        //Proceed only if all the images were uploaded
                        if (imageUrls.size == payment.photoUris.size) {
                            paymentDetailsDto.photoUrls = imageUrls
                            uploadPaymentInformation(
                                paymentDetailsRef,
                                paymentListingsRef,
                                paymentDetailsDto,
                                paymentListingDto
                            )
                        }
                    } else {
                        task.exception?.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                    }
                }.addOnFailureListener { e ->
                    e.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                }
            }
        } else {
            uploadPaymentInformation(
                paymentDetailsRef,
                paymentListingsRef,
                paymentDetailsDto,
                paymentListingDto
            )
        }
    }

    override suspend fun editPayment(id: String, newPaymentDetails: NewPaymentDetails) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        val paymentDetailsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsDetails/$id")
        val paymentListingsRef =
            firebaseDatabase.getReference("users/$currentUserId/paymentsListings/$id")
        val storageReference =
            firebaseStorage.getReference("users/$currentUserId/payments/$id/images/")

        val imageUrls = mutableListOf<String>()
        val paymentDetailsDto = newPaymentDetails.toPaymentDetailsDto()
        val paymentListingDto = newPaymentDetails.toPaymentListingDto()

        if (newPaymentDetails.photoUris.isNotEmpty()) {
            newPaymentDetails.photoUris.forEachIndexed { i, uri ->
                val imageStorageReference = storageReference.child("image_$i.jpg")
                val uploadTask = imageStorageReference.putFile(uri)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.message?.let {
                            Log.d(PAYMENT_ADD_TAG, it)
                        }
                    }
                    // Continue with the task to get the download URL
                    imageStorageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result.toString()
                        imageUrls.add(downloadUrl)

                        //Proceed only if all the images were uploaded
                        if (imageUrls.size == newPaymentDetails.photoUris.size) {
                            paymentDetailsDto.photoUrls?.let { imageUrls.addAll(it) }
                            paymentDetailsDto.photoUrls = imageUrls
                            deletePhotos(newPaymentDetails)
                            uploadPaymentInformation(
                                paymentDetailsRef,
                                paymentListingsRef,
                                paymentDetailsDto,
                                paymentListingDto
                            )
                        }
                    } else {
                        task.exception?.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                    }
                }.addOnFailureListener { e ->
                    e.message?.let { Log.d(PAYMENT_ADD_TAG, it) }
                }
            }
        } else {
            uploadPaymentInformation(
                paymentDetailsRef,
                paymentListingsRef,
                paymentDetailsDto,
                paymentListingDto
            )
        }
    }

    override suspend fun removePayment(id: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
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

    private fun deletePhotos(
        payment: NewPaymentDetails,
    ) {
        if (payment.deletedPhotos.isNotEmpty()) {
            payment.deletedPhotos.forEach { photoUrl ->
                firebaseStorage.getReferenceFromUrl(photoUrl).delete()
                    .addOnSuccessListener {
                        Log.d("PAYMENT_EDIT", "Deleted payment: $photoUrl")
                    }
                    .addOnFailureListener {
                        Log.d("PAYMENT_EDIT", "Failed to delete the picture: ${it.message}")
                    }
            }
        }
    }

    private fun uploadPaymentInformation(
        paymentDetailsRef: DatabaseReference,
        paymentListingsRef: DatabaseReference,
        paymentDetailsDto: PaymentDetailsDto,
        paymentListingDto: PaymentListingDto
    ) {
        paymentDetailsRef.setValue(paymentDetailsDto)
            .addOnSuccessListener {
                Log.d("PAYMENT_EDIT", "Added details: $paymentDetailsDto")
            }
            .addOnFailureListener { e ->
                e.message?.let { Log.d(PAYMENT_ADD_TAG, "Details: $it") }
            }
        paymentListingsRef.setValue(paymentListingDto)
            .addOnSuccessListener {
                Log.d("PAYMENT_EDIT", "Added listing: $paymentListingDto")
            }
            .addOnFailureListener { e ->
                e.message?.let { Log.d(PAYMENT_ADD_TAG, "Listings: $it") }
            }
    }
}