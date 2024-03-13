package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class, AppModule::class)
class PaymentAdditionViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: PaymentAdditionViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Inject
    lateinit var repository: PaymentRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        savedStateHandle = SavedStateHandle()
        viewModel = PaymentAdditionViewModel(repository, savedStateHandle, sharedPreferences)
    }

    @Test
    fun getPaymentDetails_retrievesCorrectDataFromId() = runTest {
        val viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
        val exampleTitle = "Title"
        val exampleDesc = "Desc"
        val exampleAmount = 12.toDouble()

        launch(viewModelCoroutineContext) {
            insertPaymentIntoRepository(
                title = exampleTitle,
                description = exampleDesc,
                amount = exampleAmount
            )
            val paymentId = repository.getPaymentListings().first().data!!.first().id
            savedStateHandle["paymentId"] = paymentId
        }
        launch(viewModelCoroutineContext) {
            viewModel.getPaymentDetails()
        }.join()

        assertThat(viewModel.state.value.title).contains(exampleTitle)
        assertThat(viewModel.state.value.description).contains(exampleDesc)
        assertThat(viewModel.state.value.amount.toDouble()).isEqualTo(exampleAmount)
    }

    @Test
    fun sendChangeTitleEvent_correctlyUpdatesTitle() {
        val newTitle = "New title"
        viewModel.onEvent(PaymentAdditionEvent.ChangeTitle(newTitle))
        assertThat(viewModel.state.value.title).contains(newTitle)
    }

    @Test
    fun sendChangeDescriptionEvent_correctlyUpdatesDescription() {
        val newDesc = "New desc"
        viewModel.onEvent(PaymentAdditionEvent.ChangeDescription(newDesc))
        assertThat(viewModel.state.value.description).contains(newDesc)
    }

    @Test
    fun sendChangeAmountEvent_correctlyUpdatesAmount() {
        val newAmount = "2000"
        viewModel.onEvent(PaymentAdditionEvent.ChangeAmount(newAmount))
        assertThat(viewModel.state.value.amount).contains(newAmount)
    }

    @Test
    fun sendChangeCategoryEvent_correctlyUpdatesCategory() {
        val newCategory = Category.Savings
        viewModel.onEvent(PaymentAdditionEvent.ChangeCategory(newCategory))
        assertThat(viewModel.state.value.category).isEqualTo(newCategory)
    }

    @Test
    fun sendChangeDateEvent_correctlyUpdatesDate() {
        val newDate = LocalDate.of(2012, 12, 12)
        viewModel.onEvent(PaymentAdditionEvent.ChangeDate(newDate))
        assertThat(viewModel.state.value.date).isEqualTo(newDate)
    }

    @Test
    fun sendAdditionConfirmEventWithoutSetPaymentId_correctlyAddsPaymentToRepository() = runTest {
        val viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
        val newTitle = "Example title"
        viewModel.onEvent(PaymentAdditionEvent.ChangeTitle(newTitle))
        viewModel.onEvent(PaymentAdditionEvent.ChangeAmount("12"))
        launch(viewModelCoroutineContext) {
            viewModel.onEvent(PaymentAdditionEvent.AdditionConfirm)
        }

        launch(viewModelCoroutineContext) {
            val paymentListingInserted =
                repository.getPaymentListingsWithFilter(FilterSettings(query = newTitle))
                    .first().data
            assertThat(
                paymentListingInserted
            ).isNotEmpty()
        }
    }

    @Test
    fun sendAdditionConfirmEventWithSetPaymentId_correctlyUpdatesPayment() = runTest {
        val viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext

        val initialTitle = "Example title"
        val initialDesc = "Example desc"
        val initialAmount = 10.toDouble()
        val initialCategory = Category.Housing
        val initialDate = LocalDate.now()

        insertPaymentIntoRepository(
            title = initialTitle,
            description = initialDesc,
            amount = initialAmount,
            category = initialCategory,
            date = initialDate
        )

        //Get the inserted payment
        val initialPayment = repository.getPaymentListings().first().data?.first()!!

        //Set up values to be able to insert payment into repo and be able to compare the result
        viewModel.onEvent(PaymentAdditionEvent.ChangeTitle("New title"))
        viewModel.onEvent(PaymentAdditionEvent.ChangeAmount("12"))
        viewModel.onEvent(PaymentAdditionEvent.ChangeDate(LocalDate.now().minusDays(1)))
        viewModel.onEvent(PaymentAdditionEvent.ChangeCategory(Category.Savings))

        //Update the value of the payment id in the saved state handle
        savedStateHandle["paymentId"] = initialPayment.id

        launch(viewModelCoroutineContext) { viewModel.onEvent(PaymentAdditionEvent.AdditionConfirm) }
        launch(viewModelCoroutineContext) {
            val updatedPayment = repository.getPaymentDetails(initialPayment.id).first().data!!
            assertThat(updatedPayment.title).isNotEqualTo(initialPayment.title)
            assertThat(updatedPayment.amount).isNotEqualTo(initialPayment.amount)
            assertThat(updatedPayment.date).isNotEqualTo(initialPayment.date)
            assertThat(updatedPayment.category).isNotEqualTo(initialPayment.category)
        }
    }

    @Test
    fun sendAddNewPhotoEvent_photoIsAddedToState() {
        val photoReplacement = Uri.parse("")
        viewModel.onEvent(PaymentAdditionEvent.AddNewPhoto(photoReplacement))
        assertThat(viewModel.state.value.newPhotos).contains(photoReplacement)
    }

    @Test
    fun sendRemovePhotoEvent_photoIsRemovedFromState() {
        val photoReplacement = Uri.parse("")
        viewModel.onEvent(PaymentAdditionEvent.AddNewPhoto(photoReplacement))
        assertThat(viewModel.state.value.newPhotos).contains(photoReplacement)
        viewModel.onEvent(PaymentAdditionEvent.RemovePhoto(photoReplacement))
        assertThat(viewModel.state.value.newPhotos).isEmpty()
    }

    @Test
    fun sendRemoveUploadedPhotoEvent_photoIsRemovedFromUploadedAndAddedToDeletedList() = runTest {
        val viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext

        launch(viewModelCoroutineContext) {
            insertPaymentIntoRepository()
            val paymentId = repository.getPaymentListings().first().data!!.first().id
            assertThat(paymentId).isNotEmpty()
            savedStateHandle["paymentId"] = paymentId
            assertThat(
                repository.getPaymentDetails(paymentId).first().data!!.photoUrls
            ).isNotEmpty()
        }

        launch(viewModelCoroutineContext) {
            viewModel.getPaymentDetails()
        }.join()
        assertThat(viewModel.state.value.uploadedPhotos).isNotEmpty()

        viewModel.onEvent(PaymentAdditionEvent.RemoveUploadedPhoto("Photo"))
        assertThat(viewModel.state.value.uploadedPhotos).isEmpty()
        assertThat(viewModel.state.value.deletedPhotos).contains("Photo")
    }

    @Test
    fun restoreDeletedUriPhoto_correctlyAddsPhotoBackToList() {
        val photoReplacement = Uri.parse("")
        viewModel.onEvent(PaymentAdditionEvent.RestoreDeletedPhoto(photoReplacement))
        assertThat(viewModel.state.value.newPhotos).contains(photoReplacement)
    }

    @Test
    fun restoreDeletedUrlPhoto_correctlyAddsPhotoBackToListAndRemovesItFromDeleted() = runTest {
        val viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
        val photoId = "Photo"

        launch(viewModelCoroutineContext) {
            insertPaymentIntoRepository()
            val paymentId = repository.getPaymentListings().first().data!!.first().id
            assertThat(paymentId).isNotEmpty()
            savedStateHandle["paymentId"] = paymentId
        }

        launch(viewModelCoroutineContext) {
            viewModel.getPaymentDetails()
        }.join()

        viewModel.onEvent(PaymentAdditionEvent.RemoveUploadedPhoto(photoId))
        viewModel.onEvent(PaymentAdditionEvent.RestoreDeletedPhoto(photoId))
        assertThat(viewModel.state.value.uploadedPhotos).contains(photoId)
        assertThat(viewModel.state.value.deletedPhotos).isEmpty()
    }

    @Test
    fun sendUpdateDialogStateEvent_correctlyUpdatesDialogState() {
        val newDialogState = true
        viewModel.onEvent(PaymentAdditionEvent.UpdateDialogState(newDialogState))
        assertThat(viewModel.state.value.isPhotoDialogOpen).isEqualTo(newDialogState)
    }

    @Test
    fun sendUpdateDialogPhotoEvent_correctlyUpdatesDialogPhoto() {
        val newDialogPhoto = Uri.parse("")
        viewModel.onEvent(PaymentAdditionEvent.UpdateDialogPhoto(newDialogPhoto))
        assertThat(viewModel.state.value.dialogPhoto).isEqualTo(newDialogPhoto)
    }

    private suspend fun insertPaymentIntoRepository(
        title: String = "Example title",
        description: String = "Example description",
        amount: Double = 12.toDouble(),
        category: Category = Category.Housing,
        date: LocalDate = LocalDate.now()
    ) {
        repository.addPayment(
            NewPaymentDetails(
                title = title,
                description = description,
                amount = amount,
                category = category,
                photoUris = emptyList(),
                photoUrls = listOf("Photo"),
                date = date
            )
        )
    }
}