package com.mateusz113.financemanager.presentation.payments.payment_details

import android.content.SharedPreferences
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
class PaymentDetailsViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val savedStatePaymentId = "id"

    private lateinit var viewModel: PaymentDetailsViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Inject
    lateinit var repository: PaymentRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        savedStateHandle = SavedStateHandle()
        sharedPreferences.edit().clear().apply()
        viewModel = PaymentDetailsViewModel(savedStateHandle, sharedPreferences, repository)
    }

    @Test
    fun initializeViewModel_correctlyInitializesState() = runTest {
        val coroutineContext = viewModel.viewModelScope.coroutineContext
        val newCurrency = Currency.CAD
        val newSymbolPlacement = SymbolPlacement.Prefix
        launch(coroutineContext) {
            updateSharedPreferences(
                currency = newCurrency,
                symbolPlacement = newSymbolPlacement
            )
        }
        val paymentId = async(coroutineContext) { insertPaymentToRepo() }.await()
        savedStateHandle[savedStatePaymentId] = paymentId
        launch(coroutineContext) {
            viewModel = PaymentDetailsViewModel(savedStateHandle, sharedPreferences, repository)
        }.join()

        assertThat(viewModel.state.value.paymentDetails).isEqualTo(
            repository.getPaymentDetails(
                paymentId
            ).first().data!!
        )
        assertThat(viewModel.state.value.currency).isEqualTo(newCurrency)
        assertThat(viewModel.state.value.isCurrencyPrefix).isEqualTo(true)
    }

    @Test
    fun sendRefreshEvent_updatesStateWithNewInformation() = runTest {
        val coroutineContext = viewModel.viewModelScope.coroutineContext
        assertThat(viewModel.state.value.paymentDetails).isNull()
        val paymentId = async(coroutineContext) {
            insertPaymentToRepo()
        }.await()
        savedStateHandle[savedStatePaymentId] = paymentId
        launch(coroutineContext) { viewModel.onEvent(PaymentDetailsEvent.Refresh) }.join()
        assertThat(viewModel.state.value.paymentDetails).isEqualTo(
            repository.getPaymentDetails(paymentId).first().data!!
        )
    }

    @Test
    fun sendUpdateDialogStateEvent_updatesIsOpenInState() {
        viewModel.onEvent(PaymentDetailsEvent.UpdateDialogState(true))
        assertThat(viewModel.state.value.isPhotoDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateDialogPhoto_updatesPhotoInState() {
        val photoUrlDummy = "photo"
        viewModel.onEvent(PaymentDetailsEvent.UpdateDialogPhoto(photoUrlDummy))
        assertThat(viewModel.state.value.dialogPhoto).isEqualTo(photoUrlDummy)
    }

    @Test
    fun updateSharedPreferences_listenerUpdatesState() = runTest{
        val currency = Currency.EUR
        val symbolPlacement = SymbolPlacement.Suffix
        updateSharedPreferences(currency, symbolPlacement)
        //Delay function to allow viewmodel to update its state
        launch(viewModel.viewModelScope.coroutineContext) { delay(500) }.join()
        assertThat(viewModel.state.value.currency).isEqualTo(currency)
        assertThat(viewModel.state.value.isCurrencyPrefix).isEqualTo(false)
    }

    //Returns id of inserted payment
    private suspend fun insertPaymentToRepo(): String {
        val title = "Title"
        val description = "Desc"
        val amount = 12.toDouble()
        val date = LocalDate.now()
        val category = Category.Savings

        repository.addPayment(
            NewPaymentDetails(
                title = title,
                description = description,
                amount = amount,
                photoUris = emptyList(),
                date = date,
                category = category,
            )
        )
        //Get the payment from repository
        return repository.getPaymentListings().first().data!!.first().id
    }

    private fun updateSharedPreferences(
        currency: Currency = Currency.PLN,
        symbolPlacement: SymbolPlacement = SymbolPlacement.InAppControl
    ) {
        val userId = Firebase.auth.currentUser?.uid
        sharedPreferences.edit {
            this.putString("${userId}Currency", currency.name)
            this.putString("${userId}SymbolPlacement", symbolPlacement.name)
        }
    }
}
