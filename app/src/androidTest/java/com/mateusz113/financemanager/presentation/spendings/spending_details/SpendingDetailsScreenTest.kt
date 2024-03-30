package com.mateusz113.financemanager.presentation.spendings.spending_details

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class SpendingDetailsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var clickedPaymentListing: PaymentListing
    private lateinit var paymentListings: MutableList<PaymentListing>
    private lateinit var state: MutableState<SpendingDetailsState>
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        paymentListings = mutableListOf()
        Category.values().forEachIndexed { index, category ->
            paymentListings.add(
                PaymentListing(
                    id = index.toString(),
                    title = category.name,
                    amount = 1.toDouble(),
                    date = LocalDate.now(),
                    category = category,
                )
            )
        }
        state = mutableStateOf(
            SpendingDetailsState(
                listingsMap = Category.values().associateWith { category ->
                    paymentListings.filter { it.category == category }
                }
            )
        )
        context = composeRule.activity.applicationContext

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                SpendingDetailsScreenContent(
                    state = state.value,
                    onRefresh = {
                        state.value = state.value.copy(isLoading = true)
                    },
                    onFilterDialogOpen = {
                        state.value = state.value.copy(isFilterDialogOpen = true)
                    },
                    onSearchValueChange = { query ->
                        state.value = state.value.copy(
                            filterSettings = state.value.filterSettings.copy(query = query)
                        )
                    },
                    onKeyClick = { slice ->
                        state.value = state.value.copy(
                            isKeyDialogOpen = true,
                            currentSlice = slice
                        )
                    },
                    onPaymentFilterDialogDismiss = {
                        state.value = state.value.copy(
                            isFilterDialogOpen = false
                        )
                    },
                    onFilterSettingsUpdate = { filterSettings ->
                        state.value = state.value.copy(
                            isFilterDialogOpen = false,
                            filterSettings = filterSettings
                        )
                    },
                    onPaymentListingsDialogDismiss = {
                        state.value = state.value.copy(
                            isKeyDialogOpen = false
                        )
                    },
                    onPaymentClick = {
                        state.value = state.value.copy(
                            isKeyDialogOpen = false
                        )
                        clickedPaymentListing = it
                    }
                )
            }
        }
    }

    @Test
    fun refresh_stateIsUpdated() {
        composeRule.onNodeWithTag(TestTags.SCROLLABLE_COLUMN).performTouchInput { swipeDown() }
        assertThat(state.value.isLoading).isTrue()
    }

    @Test
    fun insertInitialData_correctlyDisplaysInformation() {
        val sumOfPayments = state.value.listingsMap.values.fold(0.0) { acc, listingsList ->
            acc + listingsList.sumOf { it.amount }
        }
        //Each payment amount is equal to 1, so check that each category has one payment
        assertThat(sumOfPayments).isEqualTo(Category.values().size.toDouble())
        Category.values().forEach { category ->
            composeRule.onNodeWithText(category.name).assertIsDisplayed()
        }
    }

    @Test
    fun insertQuery_stateQueryIsUpdated() {
        val query = "Query"
        composeRule.onNodeWithText(context.getString(R.string.search_for_payment))
            .performTextInput(query)
        assertThat(state.value.filterSettings.query).isEqualTo(query)
    }

    @Test
    fun clickFilterButton_filterDialogIsVisibleAndStateIsUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.filter)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.isFilterDialogOpen).isTrue()
    }

    @Test
    fun dismissFilterDialog_dialogIsNotVisibleAndStateIsUpdated() {
        state.value = state.value.copy(
            isFilterDialogOpen = true
        )
        pressBack()
        composeRule.waitForIdle()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isFilterDialogOpen).isFalse()
    }

    @Test
    fun updateFilterAndApply_dialogIsNotVisibleAndFilterSettingsAreUpdated() {
        val minValue = "12"
        val maxValue = "50"
        state.value = state.value.copy(
            isFilterDialogOpen = true
        )
        composeRule.onNodeWithText(context.getString(R.string.min_payment_value))
            .performTextInput(minValue)
        composeRule.onNodeWithText(context.getString(R.string.max_payment_value))
            .performTextInput(maxValue)
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isFilterDialogOpen).isFalse()
        assertThat(state.value.filterSettings.minValue).isEqualTo(minValue)
        assertThat(state.value.filterSettings.maxValue).isEqualTo(maxValue)
    }

    @Test
    fun clickRandomKey_dialogWithPaymentsIsVisibleAndStateIsUpdated() {
        Category.values().random().run {
            composeRule.onNodeWithText(this.name).performClick()
        }
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.isKeyDialogOpen).isTrue()
    }

    @Test
    fun dismissKeyDialog_dialogIsNotVisibleAndStateIsUpdated() {
        state.value = state.value.copy(
            isKeyDialogOpen = true
        )
        pressBack()
        composeRule.waitForIdle()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isKeyDialogOpen).isFalse()
    }

    @Test
    fun clickPaymentInKeyDialog_passesCorrectDataToLambdaAndDialogIsNotVisible() {
        Category.values().random().run {
            composeRule.onNodeWithText(this.name).performClick()
            composeRule.onNodeWithTag(TestTags.PAYMENT_LISTING_INFO).performClick()
            composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
                .assertIsNotDisplayed()
            assertThat(state.value.isKeyDialogOpen).isFalse()
            assertThat(clickedPaymentListing).isNotNull()
            assertThat(clickedPaymentListing.category).isEqualTo(this)
        }
    }
}
