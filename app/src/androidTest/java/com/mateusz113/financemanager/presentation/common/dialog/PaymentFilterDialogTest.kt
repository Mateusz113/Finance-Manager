package com.mateusz113.financemanager.presentation.common.dialog

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentFilterDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val buttonLabel: String = "Reopen dialog"
    private val defaultFilterSettings = FilterSettings()
    private val minValueInsertText = "50"
    private val maxValueInsertText = "500"

    private lateinit var filterSettings: MutableState<FilterSettings>
    private lateinit var isDialogOpen: MutableState<Boolean>
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                isDialogOpen = remember {
                    mutableStateOf(true)
                }

                filterSettings = remember {
                    mutableStateOf(FilterSettings())
                }

                Surface(
                    modifier = Modifier.testTag(TestTags.SURFACE)
                ) {
                    Button(onClick = { isDialogOpen.value = true }) {
                        Text(text = buttonLabel)
                    }

                    PaymentFilterDialog(
                        filterSettings = filterSettings.value,
                        isDialogOpen = isDialogOpen.value,
                        onFilterSettingsUpdate = {
                            isDialogOpen.value = false
                            filterSettings.value = it
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
            .assertIsDisplayed()
    }

    @Test
    fun clickOnOptionSelector_listOpens() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsPopup, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun selectCategoriesFromSelectorAndApply_dialogIsNotVisibleAndUpdatesFilter() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        composeRule.onNodeWithText(Category.values().first().name).performClick()
        composeRule.onNodeWithTag(TestTags.SURFACE).performClick()
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()
        assertThat(filterSettings.value.categories).contains(Category.values().first())
    }

    @Test
    fun insertCorrectAmountValuesAndApply_dialogInNotVisibleAndFilterIsUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.min_payment_value))
            .performTextInput(minValueInsertText)
        composeRule.onNodeWithText(context.getString(R.string.max_payment_value))
            .performTextInput(maxValueInsertText)
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()
        assertThat(filterSettings.value.minValue).contains(minValueInsertText)
        assertThat(filterSettings.value.maxValue).contains(maxValueInsertText)
    }

    @Test
    fun insertIncorrectAmountValuesAndApply_dialogIsStillVisible() {
        composeRule.onNodeWithText(context.getString(R.string.min_payment_value))
            .performTextInput(maxValueInsertText)
        composeRule.onNodeWithText(context.getString(R.string.max_payment_value))
            .performTextInput(minValueInsertText)
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun clickOnDatesIcons_correctDialogsOpen() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.start_date_picker))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.pick_start_date)).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.SURFACE).performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.end_date_picker))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.pick_end_date)).assertIsDisplayed()
    }

    @Test
    fun updateStartDate_dialogIsNotVisibleAndFilterIsUpdated() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.start_date_picker))
            .performClick()
        composeRule.onNodeWithTag("dialog_date_selection_${filterSettings.value.startDate.dayOfMonth + 1}")
            .performClick()
        composeRule.onNodeWithText("APPLY").performClick()
        composeRule.onNodeWithTag(TestTags.SURFACE).performClick()
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()

        assertThat(filterSettings.value.startDate).isGreaterThan(defaultFilterSettings.startDate)
    }

    @Test
    fun updateEndDate_dialogIsNotVisibleAndFilterIsUpdated() {
        val dayOfMonth = if (defaultFilterSettings.endDate.dayOfMonth - 1 < 1) {
            1
        } else {
            defaultFilterSettings.endDate.dayOfMonth - 1
        }
        composeRule.onNodeWithContentDescription(context.getString(R.string.end_date_picker))
            .performClick()
        composeRule.onNodeWithTag("dialog_date_selection_$dayOfMonth").performClick()
        composeRule.onNodeWithText("APPLY").performClick()
        composeRule.onNodeWithTag(TestTags.SURFACE).performClick()
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()

        assertThat(filterSettings.value.endDate).isAtMost(defaultFilterSettings.endDate)
    }

    @Test
    fun updateFilterReopenDialogAndClickReset_settingsResetToDefault() {
        composeRule.onNodeWithText(context.getString(R.string.min_payment_value))
            .performTextInput(minValueInsertText)
        composeRule.onNodeWithText(context.getString(R.string.apply)).performClick()

        assertThat(filterSettings.value.minValue).contains(minValueInsertText)

        composeRule.onNodeWithText(buttonLabel).performClick()
        composeRule.onNodeWithText(context.getString(R.string.reset_filter)).performClick()

        assertThat(filterSettings.value).isEqualTo(defaultFilterSettings)
    }

    @Test
    fun closeDialogWithoutUpdating_settingsStayDefault() {
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        assertThat(filterSettings.value).isEqualTo(defaultFilterSettings)
    }


    @Test
    fun dismissDialog_dialogIsNotVisible() {
        pressBack()
        composeRule.waitForIdle()
        assertThat(isDialogOpen.value).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }
}
