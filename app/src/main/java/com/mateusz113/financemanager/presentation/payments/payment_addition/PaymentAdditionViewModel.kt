package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaymentAdditionViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var _state = MutableStateFlow(PaymentAdditionState())
    val state = _state.asStateFlow()

    init {
        getPaymentDetails()
    }

    fun onEvent(event: PaymentAdditionEvent) {
        when (event) {
            is PaymentAdditionEvent.ChangeTitle -> {
                _state.value = _state.value.copy(
                    title = event.title
                )
            }

            is PaymentAdditionEvent.ChangeDescription -> {
                _state.value = _state.value.copy(
                    description = event.description
                )
            }

            is PaymentAdditionEvent.ChangeAmount -> {
                _state.value = _state.value.copy(
                    amount = event.amount
                )
            }

            is PaymentAdditionEvent.ChangeCategory -> {
                _state.value = _state.value.copy(
                    category = event.category
                )
            }

            is PaymentAdditionEvent.ChangeDate -> {
                _state.value = _state.value.copy(
                    date = event.date
                )
            }

            is PaymentAdditionEvent.AdditionConfirm -> {
                paymentAddition()
            }

            is PaymentAdditionEvent.AddNewPhoto -> {
                addPhoto(event.photoUri)
            }

            is PaymentAdditionEvent.RemovePhoto -> {
                removePhoto(event.photoUri)
            }

            is PaymentAdditionEvent.RemoveUploadedPhoto -> {
                removeUploadedPhoto(event.photoUrl)
            }

            is PaymentAdditionEvent.RestoreDeletedPhoto<*> -> {
                when (event.photo) {
                    is Uri -> {
                        addPhoto(event.photo)
                    }

                    is String -> {
                        restoreDeletedUploadedPhoto(event.photo)
                    }

                    else -> {}
                }
            }

            is PaymentAdditionEvent.UpdateDialogState -> {
                _state.value = _state.value.copy(
                    isPhotoDialogOpen = event.isOpen
                )
            }

            is PaymentAdditionEvent.UpdateDialogPhoto -> {
                _state.value = _state.value.copy(
                    dialogPhoto = event.photo
                )
            }
        }
    }

    private fun restoreDeletedUploadedPhoto(photo: String) {
        val updatedUploadedPhotosList = _state.value.uploadedPhotos.toMutableList()
        val updatedDeletedPhotosList = _state.value.deletedPhotos.toMutableList()
        updatedUploadedPhotosList.add(photo)
        updatedDeletedPhotosList.remove(photo)
        _state.value = _state.value.copy(
            uploadedPhotos = updatedUploadedPhotosList,
            deletedPhotos = updatedDeletedPhotosList
        )
    }


    private fun removeUploadedPhoto(photoUrl: String) {
        val updatedUploadedPhotosList = _state.value.uploadedPhotos.toMutableList()
        val updatedDeletedPhotosList = _state.value.deletedPhotos.toMutableList()
        updatedUploadedPhotosList.remove(photoUrl)
        updatedDeletedPhotosList.add(photoUrl)
        _state.value = _state.value.copy(
            uploadedPhotos = updatedUploadedPhotosList,
            deletedPhotos = updatedDeletedPhotosList
        )
    }

    private fun removePhoto(photoUri: Uri) {
        val updatedPhotoList = _state.value.newPhotos.toMutableList()
        updatedPhotoList.remove(photoUri)
        _state.value = _state.value.copy(
            newPhotos = updatedPhotoList
        )
    }

    private fun addPhoto(photoUri: Uri) {
        val updatedPhotoList = _state.value.newPhotos.toMutableList()
        updatedPhotoList.add(photoUri)
        _state.value = _state.value.copy(
            newPhotos = updatedPhotoList
        )
    }

    private fun getPaymentDetails() {
        viewModelScope.launch {
            val paymentId = savedStateHandle.get<String>("paymentId")
            paymentId?.let { id ->
                repository.getPaymentDetails(id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val paymentDetails = result.data
                            paymentDetails?.let { details ->
                                _state.value = _state.value.copy(
                                    title = details.title,
                                    description = details.description,
                                    amount = details.amount.toString(),
                                    uploadedPhotos = details.photoUrls,
                                    category = details.category,
                                    date = details.date,
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                error = result.message
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }
            }
        }
    }

    private fun formatAmount() {
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.decimalFormatSymbols =
            DecimalFormatSymbols.getInstance(Locale.ENGLISH)
        val formattedAmount = decimalFormat.format(
            _state.value.amount.toFloat()
        )
        _state.value = _state.value.copy(
            amount = formattedAmount
        )
    }

    private fun updatePaymentsNumberInSharedPrefs() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { id ->
            var currentPaymentsNum = sharedPreferences.getInt("${id}PaymentsNum", 0)
            currentPaymentsNum++
            sharedPreferences.edit().apply {
                this.putInt("${id}PaymentsNum", currentPaymentsNum)
            }.apply()
        }
    }

    private fun paymentAddition() {
        viewModelScope.launch {
            formatAmount()
            val paymentDetails = NewPaymentDetails(
                title = _state.value.title,
                description = _state.value.description,
                amount = _state.value.amount.toFloat(),
                photoUrls = _state.value.uploadedPhotos,
                photoUris = _state.value.newPhotos,
                deletedPhotos = _state.value.deletedPhotos,
                date = _state.value.date,
                category = _state.value.category
            )
            val paymentId = savedStateHandle.get<String>("paymentId")
            paymentId?.let {
                repository.editPayment(paymentId, paymentDetails)
                return@launch
            }
            updatePaymentsNumberInSharedPrefs()
            repository.addPayment(paymentDetails)
        }
    }
}