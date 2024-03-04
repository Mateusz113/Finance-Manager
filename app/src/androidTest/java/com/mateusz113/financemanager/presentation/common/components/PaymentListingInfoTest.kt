package com.mateusz113.financemanager.presentation.common.components

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentListing
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentListingInfoTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var paymentListing: PaymentListing
    private var wasDeleteClicked by Delegates.notNull<Boolean>()
    private lateinit var context: Context
    private var isDeletable = true

    @Before
    fun setUp() {
        context = composeRule.activity.applicationContext
        paymentListing = PaymentListing(
            id = "ID",
            title = "Title",
            amount = 16.toDouble(),
            date = LocalDate.now(),
            category = Category.Education
        )
        wasDeleteClicked = false
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PaymentListingsInfo(
                    modifier = Modifier,
                    paymentListing = paymentListing,
                    currency = Currency.PLN,
                    isCurrencyPrefix = Currency.PLN.isPrefix,
                    isDeletable = isDeletable,
                    onPaymentDelete = {
                        wasDeleteClicked = true
                    }
                )
            }
        }
    }

    @Test
    fun deletableFlagIsTrue_displaysDeleteIcon() {
        composeRule.onNodeWithContentDescription(
            context.getString(
                R.string.remove_payment
            )
        ).isDisplayed()
    }

    @Test
    fun deletableFlagIsFalse_deleteIconIsNotVisible() {
        isDeletable = false
        setUp()

        composeRule.onNodeWithContentDescription(
            context.getString(
                R.string.remove_payment
            )
        ).assertDoesNotExist()
    }

    @Test
    fun paymentInfoIsProvided_infoIsDisplayedCorrectly() {
        composeRule.onNodeWithText(paymentListing.title).isDisplayed()
        composeRule.onNodeWithText(paymentListing.amount.toString()).isDisplayed()
        composeRule.onNodeWithText(paymentListing.date.toString()).isDisplayed()
        composeRule.onNodeWithText(paymentListing.category.name).isDisplayed()
    }

    @Test
    fun deleteIconClicked_wasClickedFlagIsTrue() {
        composeRule.onNodeWithContentDescription(
            context.getString(
                R.string.remove_payment
            )
        ).performClick()
        assertThat(wasDeleteClicked).isTrue()
    }
}
