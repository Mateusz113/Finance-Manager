package com.mateusz113.financemanager.presentation.sign_in

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
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class RegisterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context
    private lateinit var retrievedEmail: String
    private lateinit var retrievedPassword: String
    private var wasRegisterClicked by Delegates.notNull<Boolean>()
    private var wasGoogleSignInClicked by Delegates.notNull<Boolean>()
    private var wasFacebookSignInClicked by Delegates.notNull<Boolean>()
    private var wasGitHubSignInClicked by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        retrievedEmail = ""
        retrievedPassword = ""
        wasRegisterClicked = false
        wasGoogleSignInClicked = false
        wasFacebookSignInClicked = false
        wasGitHubSignInClicked = false

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                SignInScreenContent(
                    onRegisterClick = {
                        wasRegisterClicked = true
                    },
                    onGoogleSignInClick = {
                        wasGoogleSignInClicked = true
                    },
                    onFacebookSignInClick = {
                        wasFacebookSignInClicked = true
                    },
                    onGitHubSignInClick = {
                        wasGitHubSignInClicked = true
                    },
                    onFirebaseSignInClick = { email, password ->
                        retrievedEmail = email
                        retrievedPassword = password
                    }
                )
            }
        }
    }

    @Test
    fun leaveEmailBlankAndClickLogin_valuesAreNotPropagated() {
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.login)).performClick()

        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertIncorrectlyFormattedEmailAndClickLogin_valuesAreNotPropagated() {
        val incorrectlyFormattedEmail = "email"
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(incorrectlyFormattedEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.login)).performClick()

        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun leavePasswordBlankAndClickLogin_valuesAreNotPropagated() {
        val correctEmail = "email@email.com"

        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.login)).performClick()

        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertTooShortPasswordAndClickLogin_valuesAreNotPropagated() {
        val correctEmail = "email@email.com"
        val tooShortPassword = "pass"

        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(tooShortPassword)
        composeRule.onNodeWithText(context.getString(R.string.login)).performClick()

        assertThat(retrievedEmail).isEmpty()
        assertThat(retrievedPassword).isEmpty()
    }
    @Test
    fun insertCorrectValues_valuesArePropagated() {
        val correctEmail = "email@email.com"
        val correctPassword = "CorrectPassword"

        composeRule.onNodeWithText(context.getString(R.string.email)).performTextInput(correctEmail)
        composeRule.onNodeWithText(context.getString(R.string.password)).performTextInput(correctPassword)
        composeRule.onNodeWithText(context.getString(R.string.login)).performClick()

        assertThat(retrievedEmail).isEqualTo(correctEmail)
        assertThat(retrievedPassword).isEqualTo(correctPassword)
    }

    @Test
    fun clickRegister_executesCorrectOnClick(){
        composeRule.onNodeWithText(context.getString(R.string.register)).performClick()
        assertThat(wasRegisterClicked).isTrue()
    }

    @Test
    fun clickGoogleSignIn_executesCorrectOnClick(){
        composeRule.onNodeWithText(context.getString(R.string.google_sign_in)).performClick()
        assertThat(wasGoogleSignInClicked).isTrue()
    }

    @Test
    fun clickFacebookSignIn_executesCorrectOnClick(){
        composeRule.onNodeWithText(context.getString(R.string.facebook_sign_in)).performClick()
        assertThat(wasFacebookSignInClicked).isTrue()
    }

    @Test
    fun clickGitHubSignIn_executesCorrectOnClick(){
        composeRule.onNodeWithText(context.getString(R.string.github_sign_in)).performClick()
        assertThat(wasGitHubSignInClicked).isTrue()
    }
}
