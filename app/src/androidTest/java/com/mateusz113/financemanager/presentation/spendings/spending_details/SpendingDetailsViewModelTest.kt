package com.mateusz113.financemanager.presentation.spendings.spending_details

import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.viewModelScope
import co.yml.charts.ui.piechart.models.PieChartData
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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
class SpendingDetailsViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: SpendingDetailsViewModel
    private lateinit var viewModelCoroutineContext: CoroutineContext

    @Inject
    lateinit var repository: PaymentRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        sharedPreferences.edit().clear().apply()
        viewModel = SpendingDetailsViewModel(repository, sharedPreferences)
        viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
    }

    @Test
    fun initViewModel_correctlyInitializesState() = runTest {
        val categoryToCheck = Category.Savings

        assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isEmpty()
        assertThat(viewModel.state.value.currency).isEqualTo(Currency.PLN)
        assertThat(viewModel.state.value.isCurrencyPrefix).isNull()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val newCurrency = Currency.EUR
        sharedPreferences.edit().apply {
            this.putString("${userId}Currency", newCurrency.name)
            this.putString("${userId}SymbolPlacement", SymbolPlacement.Prefix.name)
        }.apply()

        launch(viewModelCoroutineContext) {
            addPaymentToRepo(
                NewPaymentDetails(
                    title = "",
                    description = "",
                    amount = 2.0,
                    photoUris = emptyList(),
                    category = categoryToCheck,
                    date = LocalDate.now()
                )
            )
        }.join()

        launch(viewModelCoroutineContext) {
            viewModel = SpendingDetailsViewModel(repository, sharedPreferences)
        }.join()

        assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isNotEmpty()
        assertThat(viewModel.state.value.currency).isEqualTo(newCurrency)
        assertThat(viewModel.state.value.isCurrencyPrefix).isTrue()
    }

    @Test
    fun sendUpdateFilterDialogStateEvent_updatesIsFilterDialogOpenFlag() {
        assertThat(viewModel.state.value.isFilterDialogOpen).isFalse()
        viewModel.onEvent(SpendingDetailsEvent.UpdateFilterDialogState(true))
        assertThat(viewModel.state.value.isFilterDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateFilterSettingsEvent_updatesFilterSettingsAndReloadsPaymentsFromRepository() =
        runTest {
            val categoryToCheck = Category.Savings
            launch(viewModelCoroutineContext) { assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isEmpty() }
            launch(viewModelCoroutineContext) { addPaymentToRepo() }.join()
            launch(viewModelCoroutineContext) {
                viewModel.onEvent(
                    SpendingDetailsEvent.UpdateFilterSettings(
                        FilterSettings(
                            categories = mutableListOf(categoryToCheck)
                        )
                    )
                )
            }.join()

            assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isNotEmpty()
        }

    @Test
    fun sendUpdateSliceDialogStateEvent_updatesIsKeyDialogOpenFlag() {
        assertThat(viewModel.state.value.isKeyDialogOpen).isFalse()
        viewModel.onEvent(SpendingDetailsEvent.UpdateSliceDialogState(true))
        assertThat(viewModel.state.value.isKeyDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateCurrentSliceEvent_updatesCurrentSlice() {
        val slice = PieChartData.Slice(
            label = "Label",
            value = 10f,
            color = Color.Blue
        )
        assertThat(viewModel.state.value.currentSlice).isNotEqualTo(slice)
        viewModel.onEvent(SpendingDetailsEvent.UpdateCurrentSlice(slice))
        assertThat(viewModel.state.value.currentSlice).isEqualTo(slice)
    }

    @Test
    fun sendSearchForPaymentEvent_updatesTheQueryInStateAndReloadsPaymentsFromRepository() =
        runBlocking {
            val categoryToCheck = Category.Savings
            val title = "Title"
            launch(viewModelCoroutineContext) { assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isEmpty() }
            launch(viewModelCoroutineContext) {
                addPaymentToRepo(
                    NewPaymentDetails(
                        title = title,
                        description = "",
                        amount = 2.3,
                        photoUris = listOf(),
                        date = LocalDate.now(),
                        category = categoryToCheck
                    )
                )
            }.join()

            launch(viewModelCoroutineContext) {
                viewModel.onEvent(
                    SpendingDetailsEvent.SearchForPayment(query = title)
                )
            }.join()

            //Wait for viewmodel to finish
            delay(1000L)

            assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isNotEmpty()
        }

    @Test
    fun sendRefreshEvent_reloadsPaymentsFromRepository() =
        runTest {
            val categoryToCheck = Category.Savings
            launch(viewModelCoroutineContext) { assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isEmpty() }
            launch(viewModelCoroutineContext) { addPaymentToRepo() }.join()
            launch(viewModelCoroutineContext) { viewModel.onEvent(SpendingDetailsEvent.Refresh) }.join()
            assertThat(viewModel.state.value.listingsMap[categoryToCheck]).isNotEmpty()
        }

    @Test
    fun updateSharedPreferences_listenersTriggerAndStateIsUpdated() = runBlocking {
        assertThat(viewModel.state.value.currency).isEqualTo(Currency.PLN)
        assertThat(viewModel.state.value.isCurrencyPrefix).isNull()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val newCurrency = Currency.EUR
        sharedPreferences.edit().apply {
            this.putString("${userId}Currency", newCurrency.name)
            this.putString("${userId}SymbolPlacement", SymbolPlacement.Prefix.name)
        }.apply()
        //Wait for the listener to finish the update
        delay(500L)

        assertThat(viewModel.state.value.currency).isEqualTo(newCurrency)
        assertThat(viewModel.state.value.isCurrencyPrefix).isTrue()
    }

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
