package com.mateusz113.financemanager.presentation.payments.payment_listings

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SortingMethod
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class, AppModule::class)
class PaymentListingsViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: PaymentListingsViewModel
    private lateinit var viewModelCoroutineContext: CoroutineContext

    @Inject
    lateinit var repository: PaymentRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        sharedPreferences.edit().clear().apply()
        viewModel = PaymentListingsViewModel(repository, sharedPreferences)
        viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
    }

    @Test
    fun initViewModel_correctlyInitializesState() = runTest {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val currency = Currency.EUR

        launch(viewModelCoroutineContext) { addPaymentToRepo() }.join()
        sharedPreferences.edit().apply {
            this.putString("${userId}Currency", currency.name)
            this.putString("${userId}SymbolPlacement", SymbolPlacement.Suffix.name)
        }.apply()

        launch(viewModelCoroutineContext) {
            viewModel = PaymentListingsViewModel(repository, sharedPreferences)
        }.join()

        assertThat(viewModel.state.value.payments).isNotEmpty()
        assertThat(viewModel.state.value.isCurrencyPrefix).isFalse()
        assertThat(viewModel.state.value.currency).isEqualTo(currency)
    }

    @Test
    fun sendRefreshEvent_updatesStateWithMostRecentValues() = runTest {
        assertThat(viewModel.state.value.payments).isEmpty()
        launch(viewModelCoroutineContext) { addPaymentToRepo() }.join()
        launch(viewModelCoroutineContext) { viewModel.onEvent(PaymentListingsEvent.Refresh) }.join()
        assertThat(viewModel.state.value.payments).isNotEmpty()
    }

    @Test
    fun sendSearchPaymentEvent_retrievesPaymentsMatchingQuery() = runBlocking {
        val paymentToInsert = NewPaymentDetails(
            title = "Title",
            description = "Desc",
            amount = 12.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now(),
            category = Category.Housing
        )
        launch(viewModelCoroutineContext) { addPaymentToRepo(paymentToInsert) }.join()
        assertThat(viewModel.state.value.payments).isEmpty()
        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.SearchPayment(
                    paymentToInsert.title
                )
            )
        }.join()
        //Waits until the search is finished
        delay(800)
        assertThat(viewModel.state.value.payments).isNotEmpty()
    }

    @Test
    fun sendUpdateFilterDialogStateEvent_updatesFilterDialogFlagInState() {
        viewModel.onEvent(PaymentListingsEvent.UpdateFilterDialogState(true))
        assertThat(viewModel.state.value.isFilterDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateFilterSettingsEvent_updatesFilterSettingsInStateAndRetrievesMatchingDataFromRepo() =
        runTest {
            val paymentToInsert1 = NewPaymentDetails(
                title = "Title",
                description = "Desc",
                amount = 12.toDouble(),
                photoUris = emptyList(),
                date = LocalDate.now(),
                category = Category.Housing
            )
            val paymentToInsert2 = NewPaymentDetails(
                title = "Title",
                description = "Desc",
                amount = 20.toDouble(),
                photoUris = emptyList(),
                date = LocalDate.now(),
                category = Category.Housing
            )

            launch(viewModelCoroutineContext) {
                addPaymentToRepo(paymentToInsert1)
                addPaymentToRepo(paymentToInsert2)
            }.join()

            launch(viewModelCoroutineContext) {
                viewModel.onEvent(
                    PaymentListingsEvent.UpdateFilterSettings(
                        FilterSettings(
                            minValue = "20"
                        )
                    )
                )
            }.join()

            //Check that it correctly retrieves only payments that match filter
            assertThat(viewModel.state.value.payments.size).isEqualTo(1)
        }

    @Test
    fun sendUpdateDeleteDialogStateEvent_updatesDeleteDialogFlagInState() {
        viewModel.onEvent(PaymentListingsEvent.UpdateDeleteDialogState(true))
        assertThat(viewModel.state.value.isDeleteDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateDeleteDialogInfoEvent_updatesDeletableDialogInfoInState() {
        val title = "Title"
        val id = "Id"
        viewModel.onEvent(
            PaymentListingsEvent.UpdateDeleteDialogInfo(
                title = title,
                id = id
            )
        )
        assertThat(viewModel.state.value.deleteDialogPaymentTitle).isEqualTo(title)
        assertThat(viewModel.state.value.deleteDialogPaymentId).isEqualTo(id)
    }

    @Test
    fun sendDeletePaymentEvent_removesPaymentFromStateAndRepository() = runBlocking {
        val paymentId = async(viewModelCoroutineContext) { addPaymentToRepo() }.await()
        launch(viewModelCoroutineContext) { viewModel.onEvent(PaymentListingsEvent.Refresh) }.join()
        assertThat(repository.getPaymentListings().first().data).isNotEmpty()
        assertThat(viewModel.state.value.payments).isNotEmpty()
        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.DeletePayment(
                    paymentId
                )
            )
        }.join()
        assertThat(repository.getPaymentListings().first().data).isEmpty()
        assertThat(viewModel.state.value.payments).isEmpty()
    }

    @Test
    fun sendUpdateSortingDialogState_updateSortingDialogFlagInState() {
        viewModel.onEvent(PaymentListingsEvent.UpdateSortingDialogState(true))
        assertThat(viewModel.state.value.isSortingMethodDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateSortingMethod_updatesSortingMethodInStateAndSortsPayments() = runTest {
        val paymentToInsert1 = NewPaymentDetails(
            title = "Title",
            description = "Desc",
            amount = 12.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now(),
            category = Category.Housing
        )
        val paymentToInsert2 = NewPaymentDetails(
            title = "Title",
            description = "Desc",
            amount = 20.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now(),
            category = Category.Housing
        )

        launch(viewModelCoroutineContext) {
            addPaymentToRepo(paymentToInsert1)
            addPaymentToRepo(paymentToInsert2)
        }.join()

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.Refresh
            )
        }.join()

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.AmountAscending
                )
            )
        }.join()

        //Sorting method is assuming that amount is ascending
        assertThat(viewModel.state.value.payments[0].amount).isLessThan(viewModel.state.value.payments[1].amount)
    }

    @Test
    fun checkAllSortingMethods_paymentAreSortedCorrectly() = runTest {
        val paymentToInsert1 = NewPaymentDetails(
            title = "Title",
            description = "Desc",
            amount = 12.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now().minusDays(1),
            category = Category.Housing
        )
        val paymentToInsert2 = NewPaymentDetails(
            title = "Different title",
            description = "Desc",
            amount = 20.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now(),
            category = Category.Housing
        )

        launch(viewModelCoroutineContext) {
            addPaymentToRepo(paymentToInsert1)
            addPaymentToRepo(paymentToInsert2)
        }.join()

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.Refresh
            )
        }.join()

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.Alphabetically
                )
            )
        }.join()
        assertThat(viewModel.state.value.payments[0].title).isLessThan(viewModel.state.value.payments[1].title)

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.AmountAscending
                )
            )
        }.join()
        assertThat(viewModel.state.value.payments[0].amount).isLessThan(viewModel.state.value.payments[1].amount)

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.AmountDescending
                )
            )
        }.join()
        assertThat(viewModel.state.value.payments[0].amount).isGreaterThan(viewModel.state.value.payments[1].amount)

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.OldToNew
                )
            )
        }.join()
        assertThat(viewModel.state.value.payments[0].date).isLessThan(viewModel.state.value.payments[1].date)

        launch(viewModelCoroutineContext) {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingMethod(
                    SortingMethod.NewToOld
                )
            )
        }.join()
        assertThat(viewModel.state.value.payments[0].date).isGreaterThan(viewModel.state.value.payments[1].date)
    }

    @Test
    fun updateCurrencyAndSymbolValuesInSharedPrefs_listenerUpdatesState() = runBlocking {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val currency = Currency.EUR
        sharedPreferences.edit().apply {
            this.putString("${userId}Currency", currency.name)
            this.putString("${userId}SymbolPlacement", SymbolPlacement.Suffix.name)
        }.apply()
        //Wait for listeners to update state
        delay(500L)
        assertThat(viewModel.state.value.isCurrencyPrefix).isFalse()
        assertThat(viewModel.state.value.currency).isEqualTo(currency)
    }

    //Returns id of inserted payment
    private suspend fun addPaymentToRepo(
        paymentDetails: NewPaymentDetails = NewPaymentDetails(
            title = "Title",
            description = "Desc",
            amount = 12.toDouble(),
            photoUris = emptyList(),
            date = LocalDate.now(),
            category = Category.Savings
        )
    ): String {
        repository.addPayment(paymentDetails)
        return repository.getPaymentListings().first().data!!.first().id
    }
}
