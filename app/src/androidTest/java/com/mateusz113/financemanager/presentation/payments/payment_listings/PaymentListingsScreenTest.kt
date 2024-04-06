package com.mateusz113.financemanager.presentation.payments.payment_listings

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import com.mateusz113.financemanager.domain.enumeration.SortingMethod
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
import java.time.Month
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentListingsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val examplePayment = PaymentListing(
        id = "id",
        title = "title",
        amount = 12.toDouble(),
        date = LocalDate.now(),
        category = Category.Savings
    )

    private lateinit var state: MutableState<PaymentListingsState>
    private lateinit var context: Context
    private var wasPaymentAddClicked by Delegates.notNull<Boolean>()
    private lateinit var clickedPaymentId: String

    @Before
    fun setUp() {
        hiltRule.inject()
        state = mutableStateOf(
            PaymentListingsState(
                payments = listOf(examplePayment)
            )
        )
        context = composeRule.activity.applicationContext
        wasPaymentAddClicked = false
        clickedPaymentId = ""

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PaymentListingsScreenContent(
                    state = state.value,
                    onFABClick = { wasPaymentAddClicked = true },
                    onRefresh = { state.value = state.value.copy(isLoading = true) },
                    onOpenFilterDialog = {
                        state.value = state.value.copy(
                            isFilterDialogOpen = true
                        )
                    },
                    onSearchValueChange = { query ->
                        state.value = state.value.copy(
                            filterSettings = state.value.filterSettings.copy(query = query)
                        )
                    },
                    onSortingButtonClick = {
                        state.value = state.value.copy(isSortingMethodDialogOpen = true)
                    },
                    onPaymentClick = { index ->
                        clickedPaymentId = state.value.payments[index].id
                    },
                    onPaymentDeleteClick = { index ->
                        state.value = state.value.copy(
                            isDeleteDialogOpen = true,
                            deleteDialogPaymentTitle = state.value.payments[index].title,
                            deleteDialogPaymentId = state.value.payments[index].id
                        )
                    },
                    onFilterDialogDismiss = {
                        state.value = state.value.copy(
                            isFilterDialogOpen = false
                        )
                    },
                    onFilterSettingsUpdate = { filterSettings ->
                        state.value = state.value.copy(
                            filterSettings = filterSettings,
                            isFilterDialogOpen = false
                        )
                    },
                    onConfirmationDialogDismiss = {
                        state.value = state.value.copy(
                            isDeleteDialogOpen = false
                        )
                    },
                    onConfirmationDialogConfirm = {
                        state.value = state.value.copy(
                            payments = state.value.payments.toMutableList()
                                .filter { it.id != state.value.deleteDialogPaymentId },
                            isDeleteDialogOpen = false
                        )
                    },
                    onSortingDialogDismiss = {
                        state.value = state.value.copy(
                            isSortingMethodDialogOpen = false
                        )
                    },
                    onSortingDialogSelect = { sortingMethod ->
                        state.value = state.value.copy(
                            sortingSettingsInfo = state.value.sortingSettingsInfo.copy(
                                currentOption = sortingMethod
                            ),
                            isSortingMethodDialogOpen = false
                        )
                    }
                )
            }
        }
    }

    @Test
    fun clickFAB_correctlyExecutesOnClick() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_payment))
            .performClick()
        assertThat(wasPaymentAddClicked).isTrue()
    }

    @Test
    fun refresh_isLoadingInStateIsUpdated() {
        assertThat(state.value.isLoading).isFalse()
        composeRule.onNodeWithTag(TestTags.LAZY_COLUMN).performTouchInput {
            swipeDown()
        }
        composeRule.waitForIdle()
        assertThat(state.value.isLoading).isTrue()
    }

    @Test
    fun insertTextIntoTextField_queryInStateIsUpdated() {
        val textToInsert = "Dummy text"
        composeRule.onNodeWithText(context.getString(R.string.search_for_payment))
            .performTextInput(textToInsert)
        assertThat(state.value.filterSettings.query).contains(textToInsert)
    }

    @Test
    fun clickFilterButton_dialogWithFilterSettingsIsShown() {
        composeRule.onNodeWithText(context.getString(R.string.filter))
            .performClick()
        assertThat(state.value.isFilterDialogOpen).isTrue()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun dismissOpenFilterDialog_dialogIsClosedWithoutChanges() {
        composeRule.onNodeWithText(context.getString(R.string.filter))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        assertThat(state.value.isFilterDialogOpen).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun changeSettingsInFilterDialogAndApply_filterSettingsInStateAreUpdatedAndDialogIsClosed() {
        val minValueToInsert = "15"
        val maxValueToInsert = "50"
        composeRule.onNodeWithText(context.getString(R.string.filter))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.min_payment_value))
            .performTextInput(minValueToInsert)
        composeRule.onNodeWithText(context.getString(R.string.max_payment_value))
            .performTextInput(maxValueToInsert)
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()
        assertThat(state.value.filterSettings.minValue).contains(minValueToInsert)
        assertThat(state.value.filterSettings.maxValue).contains(maxValueToInsert)
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun clickResetFilterInFilterDialog_filterSettingsInStateAreUpdatedAndDialogIsClosed() {
        state.value = state.value.copy(
            filterSettings = state.value.filterSettings.copy(
                query = "Example query",
                categories = mutableListOf(Category.Housing),
                minValue = "12",
                maxValue = "50",
                startDate = LocalDate.now().minusDays(10),
                endDate = LocalDate.now().minusDays(2)
            )
        )
        composeRule.onNodeWithText(context.getString(R.string.filter))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.reset_filter))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()

        assertThat(state.value.isFilterDialogOpen).isFalse()
        assertThat(state.value.filterSettings.query).isEmpty()
        assertThat(state.value.filterSettings.categories).isEmpty()
        assertThat(state.value.filterSettings.minValue).isEmpty()
        assertThat(state.value.filterSettings.maxValue).isEmpty()
        assertThat(state.value.filterSettings.startDate).isEqualTo(
            LocalDate.of(
                2011,
                Month.JULY,
                22
            )
        )
        assertThat(state.value.filterSettings.endDate).isEqualTo(LocalDate.now())
    }

    @Test
    fun clickSortButton_sortingMethodDialogIsShown() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.sorting_button))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun dismissSortDialog_sortingMethodDialogIsClosed() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.sorting_button))
            .performClick()
        pressBack()
        composeRule.waitForIdle()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun selectNewSortingMethodInSortDialog_sortingMethodIsUpdatedInStateAndDialogIsClosed() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.sorting_button))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.new_to_old)).performClick()
        assertThat(state.value.sortingSettingsInfo.currentOption).isEqualTo(SortingMethod.NewToOld)
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun clickOnPayment_stateDataAboutClickedPaymentIsUpdated() {
        composeRule.onNodeWithText(examplePayment.title).performClick()
        assertThat(clickedPaymentId).isEqualTo(examplePayment.id)
    }

    @Test
    fun clickOnPaymentDelete_stateDataAboutPaymentToDeleteIsUpdatedAndDialogIsShown() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_payment)).performClick()
        assertThat(state.value.isDeleteDialogOpen).isTrue()
        assertThat(state.value.deleteDialogPaymentId).isEqualTo(examplePayment.id)
        assertThat(state.value.deleteDialogPaymentTitle).isEqualTo(examplePayment.title)
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun dismissDeletePaymentDialog_dialogIsClosedWithoutDeletingPayment() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_payment)).performClick()
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        assertThat(state.value.isDeleteDialogOpen).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun confirmOnDeletePaymentDialog_paymentIsDeletedAndDialogIsClosed() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_payment)).performClick()
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(state.value.isDeleteDialogOpen).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.payments).isEmpty()
    }
}
