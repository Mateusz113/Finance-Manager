package com.mateusz113.financemanager.presentation.common.dialog

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
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
class PaymentListingsCollectionDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val surfaceTestTag: String = "Surface"
    private val paymentListings: List<PaymentListing> = listOf(
        PaymentListing("A", "A", 1.toDouble(), LocalDate.now(), Category.Health),
        PaymentListing("B", "B", 2.toDouble(), LocalDate.now(), Category.Groceries),
        PaymentListing("C", "C", 3.toDouble(), LocalDate.now(), Category.Education),
        PaymentListing("D", "D", 4.toDouble(), LocalDate.now(), Category.Entertainment),
        PaymentListing("E", "E", 5.toDouble(), LocalDate.now(), Category.Personal),
        PaymentListing("F", "F", 6.toDouble(), LocalDate.now(), Category.Savings),
        PaymentListing("G", "G", 7.toDouble(), LocalDate.now(), Category.Housing),
        PaymentListing("H", "H", 8.toDouble(), LocalDate.now(), Category.Transportation)
    )
    private val currency: Currency = Currency.PLN

    private lateinit var isDialogOpen: MutableState<Boolean>
    private lateinit var context: Context
    private lateinit var clickedListing: PaymentListing


    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                isDialogOpen = remember {
                    mutableStateOf(true)
                }

                Surface(
                    modifier = Modifier.testTag(surfaceTestTag)
                ) {
                    PaymentListingsCollectionDialog(
                        paymentListings = paymentListings,
                        currency = currency,
                        isCurrencyPrefix = currency.isPrefix,
                        isDialogOpen = isDialogOpen.value,
                        onPaymentClick = {
                            isDialogOpen.value = false
                            clickedListing = it
                        },
                        onDismiss = {
                            isDialogOpen.value = false
                        }
                    )
                }
            }
        }
    }

    @Test
    fun isOpenFlagSetToTrue_dialogIsVisible() {
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isDisplayed()
    }

    @Test
    fun paymentListingsAreNotEmpty_displaysInformationCorrectly() {
        val column = composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
        paymentListings.forEachIndexed { index, payment ->
            column.performScrollToIndex(index)
            composeRule.onNodeWithText(payment.title).assertExists()
            composeRule.onNodeWithText(payment.amount.toString()).assertExists()
            composeRule.onNodeWithText(payment.category.name).assertExists()
        }
    }

    @Test
    fun clickRandomPayment_returnsCorrectData() {
        val randomIndex = paymentListings.indices.random()
        val randomListing = paymentListings[randomIndex]
        val column = composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
        column.performScrollToIndex(randomIndex)
        composeRule.onNodeWithText(randomListing.title).performClick()
        assertThat(clickedListing.id).contains(randomListing.id)
        assertThat(clickedListing.title).contains(randomListing.title)
        assertThat(clickedListing.amount).isEqualTo(randomListing.amount)
        assertThat(clickedListing.date).isEqualTo(randomListing.date)
        assertThat(clickedListing.category).isEqualTo(randomListing.category)
    }

    @Test
    fun pressBackWhenDialogIsOpen_dialogIsNotVisible() {
        pressBack()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
    }

    @Test
    fun backgroundClickedWhenDialogIsOpen_dialogIsNotVisible() {
        composeRule.onNodeWithTag(surfaceTestTag).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
    }
}
