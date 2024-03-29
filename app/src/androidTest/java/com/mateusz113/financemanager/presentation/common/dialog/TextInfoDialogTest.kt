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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class TextInfoDialogTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val dialogText: String = "Text"

    private lateinit var isDialogOpen: MutableState<Boolean>
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

                Surface(
                    modifier = Modifier.testTag(TestTags.SURFACE)
                ) {
                    TextInfoDialog(
                        dialogText = dialogText,
                        isDialogOpen = isDialogOpen.value,
                        onDismiss = { isDialogOpen.value = false }
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
    fun textValueIsNotBlank_displaysTextCorrectly() {
        composeRule.onNodeWithText(dialogText).assertIsDisplayed()
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
