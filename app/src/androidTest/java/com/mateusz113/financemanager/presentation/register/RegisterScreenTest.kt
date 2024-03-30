package com.mateusz113.financemanager.presentation.register

import android.content.Context
import androidx.activity.compose.setContent
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

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class RegisterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context
    private lateinit var retrievedDisplayName: String
    private lateinit var retrievedEmail: String
    private lateinit var retrievedPassword: String

    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        retrievedDisplayName = ""
        retrievedEmail = ""
        retrievedPassword = ""

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                RegisterScreen(
                    onRegisterClick = { displayName, email, password ->
                        retrievedDisplayName = displayName
                        retrievedEmail = email
                        retrievedPassword = password
                    }
                )
            }
        }
    }

    @Test
    fun leaveDisplayNameBlankAndClickButton_valuesAreNotPropagated() {
        val correctEmail = "email@email.com"
        val correctPassword = "CorrectPassword"
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertTooLongDisplayNameAndClickButton_valuesAreNotPropagated() {
        val tooLongDisplayName = "DisplayName".repeat(20)
        val correctEmail = "email@email.com"
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(tooLongDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }

    @Test
    fun leaveEmailBlankAndClickButton_valuesAreNotPropagated() {
        val correctDisplayName = "DisplayName"
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(correctDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertIncorrectlyFormattedEmailAndClickButton_valuesAreNotPropagated() {
        val correctDisplayName = "DisplayName"
        val incorrectlyFormattedEmail = "email"
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(correctDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(incorrectlyFormattedEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun leavePasswordBlankAndClickButton_valuesAreNotPropagated() {
        val correctDisplayName = "DisplayName"
        val correctEmail = "email@email.com"

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(correctDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertTooShortPasswordAndClickButton_valuesAreNotPropagated() {
        val correctDisplayName = "DisplayName"
        val correctEmail = "email@email.com"
        val tooShortPassword = "pass"

        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(correctDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(tooShortPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEmpty()
        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertCorrectValuesAndClickButton_valuesAreNotPropagated() {
        val correctDisplayName = "DisplayName"
        val correctEmail = "email@email.com"
        val correctPassword = "CorrectPassword"
        composeRule.onNodeWithText(context.getString(R.string.username)).performTextInput(correctDisplayName)
        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()

        assertThat(retrievedDisplayName).isEqualTo(correctDisplayName)
        assertThat(retrievedEmail).isEqualTo(correctEmail)
        assertThat(retrievedPassword).isEqualTo(correctPassword)
    }
}
