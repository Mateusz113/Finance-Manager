package com.mateusz113.financemanager.presentation.common.components

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentSearchBarTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var text: String
    private lateinit var textToType: String
    private var wasFilterButtonClicked by Delegates.notNull<Boolean>()
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        text = ""
        textToType = "Example text"
        wasFilterButtonClicked = false
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PaymentSearchBar(
                    modifier = Modifier,
                    value = text,
                    onSearchValueChange = { text = it },
                    onFilterDialogOpen = { wasFilterButtonClicked = true }
                )
            }
        }
    }

    @Test
    fun typeExampleTextIntoTextField_updatesTextValueCorrectly() {
        composeRule.onNodeWithText(text).performTextInput(textToType)
        assertThat(text).contains(textToType)
    }

    @Test
    fun clickOnFilterButton_wasClickedFlagIsTrue() {
        composeRule.onNodeWithText(context.getString(R.string.filter)).performClick()
        assertThat(wasFilterButtonClicked).isTrue()
    }
}
