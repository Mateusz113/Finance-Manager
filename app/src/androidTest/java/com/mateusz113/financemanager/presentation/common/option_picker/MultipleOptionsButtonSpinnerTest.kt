package com.mateusz113.financemanager.presentation.common.option_picker

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.AnnotatedString
import androidx.test.espresso.Espresso
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class MultipleOptionsButtonSpinnerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val surfaceTestTag = "SURFACE_TEST_TAG"

    private lateinit var selectedOptions: MutableList<Category>
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = composeRule.activity.applicationContext
        selectedOptions = mutableListOf()
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                Surface(
                    modifier = Modifier.testTag(surfaceTestTag)
                ) {
                    MultipleOptionsButtonSpinner(
                        options = Category.values().toList(),
                        selectedOptions = selectedOptions,
                        onOptionSelect = {
                            selectedOptions.add(it)
                        },
                        noSelectionLabel = "No selection"
                    )
                }
            }
        }
    }

    @Test
    fun clickOnSpinner_dropdownMenuIsVisible() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU).isDisplayed()
    }

    @Test
    fun pressBackButtonWhenMenuIsOpen_dropdownMenuIsNotVisible() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        Espresso.pressBack()
        composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU).isNotDisplayed()
    }

    @Test
    fun pressBackgroundWhenMenuIsOpen_dropdownMenuIsNotVisible() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        composeRule.onNodeWithTag(surfaceTestTag).performClick()
        composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU).isNotDisplayed()
    }

    @Test
    fun selectOptions_correctlyReturnsCallbackAndMenuIsVisible() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        val menu = composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU)
        var randomCategories = Category.values()
        randomCategories.shuffle()
        randomCategories = randomCategories.sliceArray(0..3)
        randomCategories.forEach { randomCategory ->
            menu.performScrollToNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Text, listOf(
                        AnnotatedString(randomCategory.name)
                    )
                )
            )
            composeRule.onNodeWithText(randomCategory.name).performClick()
        }
        composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU).isDisplayed()
        randomCategories.forEach { category ->
            assertThat(selectedOptions.contains(category)).isTrue()
        }
    }
}
