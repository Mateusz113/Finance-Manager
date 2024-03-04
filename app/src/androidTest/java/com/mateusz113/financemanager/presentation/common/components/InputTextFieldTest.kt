package com.mateusz113.financemanager.presentation.common.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
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
class InputTextFieldTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var text: String
    private lateinit var textToType: String

    @Before
    fun setUp() {
        hiltRule.inject()
        text = ""
        textToType = "Example test"
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                InputTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    }
                )
            }
        }
    }

    @Test
    fun typeExampleTextInField_textValueIsUpdatedCorrectly() {
        composeRule.onNodeWithText(text).performTextInput(textToType)
        assertThat(text).contains(textToType)
    }
}
