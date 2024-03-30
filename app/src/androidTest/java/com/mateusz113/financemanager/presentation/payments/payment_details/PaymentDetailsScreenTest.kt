package com.mateusz113.financemanager.presentation.payments.payment_details

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentDetailsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val title = "Example title"
    private val description = "Example desc"
    private val amount = 10.toDouble()
    private val category = Category.Savings
    private val date = LocalDate.now()

    private lateinit var state: MutableState<PaymentDetailsState<String>>
    private lateinit var context: Context
    private var wasEditClicked by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        hiltRule.inject()
        state = mutableStateOf(
            PaymentDetailsState(
                paymentDetails = PaymentDetails(
                    title = title,
                    description = description,
                    amount = amount,
                    category = category,
                    date = date,
                    photoUrls = listOf("https://w.example.c/image.jpg")
                )
            )
        )
        context = composeRule.activity.applicationContext
        wasEditClicked = false

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PaymentDetailsScreenContent(
                    state = state.value,
                    onRefresh = {
                        state.value = state.value.copy(
                            isLoading = true
                        )
                    },
                    onPhotoClick = {
                        state.value = state.value.copy(
                            dialogPhoto = it,
                            isPhotoDialogOpen = true
                        )
                    },
                    onPhotoDialogDismiss = {
                        state.value = state.value.copy(
                            isPhotoDialogOpen = false
                        )
                    },
                    onEditClick = {
                        wasEditClicked = true
                    }
                )
            }
        }
    }

    @Test
    fun correctDataIsInserted_displaysInformationOnScreen() {
        val formattedAmount = if (state.value.isCurrencyPrefix == true) {
            "${state.value.currency.symbol} $amount"
        } else {
            "$amount ${state.value.currency.symbol}"
        }
        val formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        composeRule.onNodeWithText(title).assertIsDisplayed()
        composeRule.onNodeWithText(description).assertIsDisplayed()
        composeRule.onNodeWithText(formattedAmount).assertIsDisplayed()
        composeRule.onNodeWithText(category.name).assertIsDisplayed()
        composeRule.onNodeWithText(formattedDate).assertIsDisplayed()
    }

    @Test
    fun swipeRefreshIsTriggered_isLoadingVariableIsUpdated() {
        assertThat(state.value.isLoading).isFalse()
        composeRule.onNodeWithTag(TestTags.SWIPE_REFRESH)
            .performTouchInput { swipeDown() }
        assertThat(state.value.isLoading).isTrue()
    }

    //Test works only in separation
    //Reason unknown
    @Test
    fun clickOnPhoto_dialogIsVisible() {
        assertThat(state.value.paymentDetails).isNotNull()
        assertThat(state.value.paymentDetails?.photoUrls).isNotEmpty()
        assertThat(state.value.paymentDetails!!.photoUrls[0]).contains("https://w.example.c/image.jpg")
        composeRule.onNodeWithTag(TestTags.PHOTO).performClick()
        assertThat(state.value.dialogPhoto).isEqualTo(state.value.paymentDetails!!.photoUrls[0])
        assertThat(state.value.isPhotoDialogOpen).isTrue()
    }

    @Test
    fun errorIsPresent_displaysErrorInformationOnScreen() {
        val errorMessage = "Error message"
        state.value = state.value.copy(
            error = errorMessage
        )
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun dismissDialog_dialogIsClosed() {
        state.value = state.value.copy(
            isPhotoDialogOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        pressBack()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun editClicked_correctlyExecutesOnClickBlock() {
        composeRule.onNodeWithText(context.getString(R.string.edit_payment)).performClick()
        assertThat(wasEditClicked).isTrue()
    }
}
