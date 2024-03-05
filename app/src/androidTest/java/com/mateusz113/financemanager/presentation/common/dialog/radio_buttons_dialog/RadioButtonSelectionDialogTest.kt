package com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog

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
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class RadioButtonSelectionDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val surfaceTestTag: String = "Surface"

    private lateinit var dialogInfo: MutableState<RadioButtonsDialogInfo<Currency>>
    private lateinit var isDialogOpen: MutableState<Boolean>
    private lateinit var context: Context
    private lateinit var initialOption: Currency

    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        initialOption = Currency.PLN
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                dialogInfo = remember {
                    mutableStateOf(
                        RadioButtonsDialogInfo(
                            label = R.string.currency,
                            currentOption = initialOption,
                            optionsLabelsMap = Currency.labelMap,
                            optionsList = Currency.values().toList()
                        )
                    )
                }
                isDialogOpen = remember {
                    mutableStateOf(true)
                }

                Surface(
                    modifier = Modifier.testTag(surfaceTestTag)
                ) {
                    RadioButtonSelectionDialog(
                        dialogInfo = dialogInfo.value,
                        isDialogOpen = isDialogOpen.value,
                        onOptionSelect = {
                            dialogInfo.value = dialogInfo.value.copy(
                                currentOption = it
                            )
                            isDialogOpen.value = false
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
    fun pressTheInitialOption_dialogIsNotVisibleAndNoValueUpdate() {
        composeRule.onNodeWithText(context.getString(Currency.labelMap[initialOption]!!))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(dialogInfo.value.currentOption).isEqualTo(initialOption)
    }

    @Test
    fun pressDifferentOption_dialogIsNotVisibleAndValueUpdated() {
        composeRule.onNodeWithText(context.getString(Currency.labelMap[Currency.AUD]!!))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(dialogInfo.value.currentOption).isNotEqualTo(initialOption)
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
