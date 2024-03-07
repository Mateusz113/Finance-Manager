package com.mateusz113.financemanager.presentation.common.option_picker

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
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
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class DatePickerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val surfaceTestTag = "SURFACE_TEST_TAG"
    private val buttonLabel = "BUTTON_LABEL"

    private lateinit var state: MaterialDialogState
    private lateinit var context: Context
    private lateinit var initialDate: LocalDate
    private lateinit var date: LocalDate

    @Before
    fun setUp() {
        context = composeRule.activity.applicationContext
        initialDate = LocalDate.of(2024, 3, 7)
        date = initialDate

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                state = rememberMaterialDialogState()
                //Always show dialog at the start of test
                state.show()

                Surface(
                    modifier = Modifier.testTag(surfaceTestTag)
                ) {
                    DatePicker(
                        datePickerState = state,
                        date = date,
                        title = R.string.title,
                        dateValidator = { newDate ->
                            newDate < date
                        },
                        onDateChange = { newDate ->
                            date = newDate
                        }
                    )
                }
            }
        }
    }

    @Test
    fun pressBackgroundWhenDialogIsOpen_dialogIsNotVisible() {
        composeRule.onNodeWithTag(surfaceTestTag).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
    }

    @Test
    fun pressBackButtonWhenDialogIsOpen_dialogIsNotVisible() {
        pressBack()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
    }

    @Test
    fun pressValidDateAndApply_dateIsUpdatedAndDialogIsNotVisible() {
        val newDayToClick = date.dayOfMonth - 1
        composeRule.onNodeWithTag("dialog_date_selection_$newDayToClick").performClick()
        composeRule.onNodeWithText("APPLY").performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(date).isNotEqualTo(initialDate)
    }

    @Test
    fun pressNotValidDateAndApply_dateIsNotUpdatedAndDialogIsNotVisible() {
        val newDayToClick = date.dayOfMonth + 1
        composeRule.onNodeWithTag("dialog_date_selection_$newDayToClick").performClick()
        composeRule.onNodeWithText("APPLY").performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(date).isEqualTo(initialDate)
    }
}
