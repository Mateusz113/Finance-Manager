package com.mateusz113.financemanager.presentation.settings

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
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.AnnotatedString
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonsDialogInfo
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class SettingsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var state: MutableState<SettingsState>
    private lateinit var context: Context
    private var wasExternalLicensesButtonClicked by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        hiltRule.inject()
        state = mutableStateOf(SettingsState())
        context = composeRule.activity.applicationContext
        wasExternalLicensesButtonClicked = false

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                SettingsScreenContent(
                    state = state.value,
                    onCurrencyClick = {
                        state.value = state.value.copy(
                            isDialogOpen = true,
                            dialogInfo = RadioButtonsDialogInfo(
                                label = R.string.currency,
                                currentOption = state.value.currentCurrency,
                                optionsLabelsMap = Currency.labelMap,
                                optionsList = Currency.values().toList()
                            )
                        )
                    },
                    onSymbolPlacementClick = {
                        state.value = state.value.copy(
                            isDialogOpen = true,
                            dialogInfo = RadioButtonsDialogInfo(
                                label = R.string.symbol_placement,
                                currentOption = state.value.currentSymbolPlacement,
                                optionsLabelsMap = SymbolPlacement.labelMap,
                                optionsList = SymbolPlacement.values().toList()
                            )
                        )
                    },
                    onDialogDismiss = {
                        state.value = state.value.copy(
                            isDialogOpen = false
                        )
                    },
                    onOptionSelect = { option ->
                        state.value = state.value.copy(isDialogOpen = false)
                        when (option) {
                            is Currency -> {
                                state.value = state.value.copy(
                                    currentCurrency = option
                                )
                            }

                            is SymbolPlacement -> {
                                state.value = state.value.copy(
                                    currentSymbolPlacement = option
                                )
                            }
                        }
                    },
                    onExternalLicensesButtonClick = {
                        wasExternalLicensesButtonClicked = true
                    }
                )
            }
        }
    }

    @Test
    fun dismissOpenedDialog_dialogIsClosed() {
        state.value = state.value.copy(
            isDialogOpen = true
        )
        pressBack()
        composeRule.waitForIdle()
        assertThat(state.value.isDialogOpen).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }

    @Test
    fun clickOnCurrencyChangeRow_dialogWithCorrectDataIsOpened() {
        composeRule.onNodeWithText(context.getString(R.string.currency_option)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.dialogInfo.label).isEqualTo(R.string.currency)
        assertThat(state.value.dialogInfo.currentOption).isEqualTo(state.value.currentCurrency)
        assertThat(state.value.dialogInfo.optionsLabelsMap).isEqualTo(Currency.labelMap)
        assertThat(state.value.dialogInfo.optionsList).isEqualTo(Currency.values().toList())
    }

    @Test
    fun clickOnSymbolPlacementChangeRow_dialogWithCorrectDataIsOpened() {
        composeRule.onNodeWithText(context.getString(R.string.symbol_placement_option)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.dialogInfo.label).isEqualTo(R.string.symbol_placement)
        assertThat(state.value.dialogInfo.currentOption).isEqualTo(state.value.currentSymbolPlacement)
        assertThat(state.value.dialogInfo.optionsLabelsMap).isEqualTo(SymbolPlacement.labelMap)
        assertThat(state.value.dialogInfo.optionsList).isEqualTo(SymbolPlacement.values().toList())
    }

    @Test
    fun openCurrencySelectionAndChooseDifferentOption_stateIsUpdatedAndDialogIsClosed() {
        val newCurrency = Currency.CHF
        val newCurrencyLabel = context.getString(Currency.labelMap[newCurrency]!!)
        composeRule.onNodeWithText(context.getString(R.string.currency_option)).performClick()
        val scrollableColumn = composeRule.onNodeWithTag(TestTags.SCROLLABLE_COLUMN)
        scrollableColumn.performScrollToNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Text, listOf(
                    AnnotatedString(newCurrencyLabel)
                )
            )
        )
        composeRule.onNodeWithText(newCurrencyLabel).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.currentCurrency).isEqualTo(newCurrency)
    }

    @Test
    fun openSymbolPlacementSelectionAndChooseDifferentOption_stateIsUpdatedAndDialogIsClosed() {
        val newSymbolPlacement = SymbolPlacement.Prefix
        val newSymbolPlacementLabel =
            context.getString(SymbolPlacement.labelMap[newSymbolPlacement]!!)
        composeRule.onNodeWithText(context.getString(R.string.symbol_placement_option)).performClick()
        val scrollableColumn = composeRule.onNodeWithTag(TestTags.SCROLLABLE_COLUMN)
        scrollableColumn.performScrollToNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Text, listOf(
                    AnnotatedString(newSymbolPlacementLabel)
                )
            )
        )
        composeRule.onNodeWithText(newSymbolPlacementLabel).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.currentSymbolPlacement).isEqualTo(newSymbolPlacement)
    }

    @Test
    fun clickExternalLicensesLink_correctOnClickIsExecuted() {
        composeRule.onNodeWithText(context.getString(R.string.external_licenses)).performClick()
        assertThat(wasExternalLicensesButtonClicked).isTrue()
    }
}
