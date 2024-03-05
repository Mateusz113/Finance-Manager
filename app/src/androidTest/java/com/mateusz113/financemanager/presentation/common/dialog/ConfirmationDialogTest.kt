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
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class ConfirmationDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val surfaceTestTag: String = "Surface"
    private val dialogTitle: String = "Title"
    private val dialogText: String = "Text"

    private lateinit var isDialogOpen: MutableState<Boolean>
    private lateinit var wasConfirmed: MutableState<Boolean>
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                isDialogOpen = remember {
                    mutableStateOf(true)
                }
                wasConfirmed = remember {
                    mutableStateOf(false)
                }

                Surface(
                    modifier = Modifier.testTag(surfaceTestTag)
                ) {
                    ConfirmationDialog(
                        dialogTitle = "",
                        dialogText = "",
                        isDialogOpen = isDialogOpen.value,
                        onDismiss = { isDialogOpen.value = false },
                        onConfirm = {
                            wasConfirmed.value = true
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
    fun textValuesAreNotBlank_displaysTitleAndTextCorrectly(){
        composeRule.onNodeWithText(dialogTitle).isDisplayed()
        composeRule.onNodeWithText(dialogText).isDisplayed()
    }

    @Test
    fun pressCancelButton_dialogIsNotVisibleAndWasUpdatedIsFalse() {
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(wasConfirmed.value).isFalse()
    }
    @Test
    fun pressConfirmButton_dialogIsNotVisibleAndWasUpdatedIsTrue() {
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
        assertThat(wasConfirmed.value).isTrue()
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
