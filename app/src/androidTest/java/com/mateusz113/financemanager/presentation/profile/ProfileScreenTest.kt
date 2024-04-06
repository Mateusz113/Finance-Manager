package com.mateusz113.financemanager.presentation.profile

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class ProfileScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var state: MutableState<ProfileState>
    private lateinit var context: Context
    private var wasSettingsButtonClicked by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        hiltRule.inject()
        state = mutableStateOf(ProfileState())
        context = composeRule.activity.applicationContext
        wasSettingsButtonClicked = false

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                ProfileScreenContent(
                    state = state.value,
                    onSettingsClick = {
                        wasSettingsButtonClicked = true
                    },
                    onSignOutClick = {
                        state.value = state.value.copy(
                            isSignOutDialogOpen = true
                        )
                    },
                    onDeleteClick = {
                        state.value = state.value.copy(
                            isDeletionConfirmOpen = true
                        )
                    },
                    onSignOutDialogDismiss = {
                        state.value = state.value.copy(
                            isSignOutDialogOpen = false
                        )
                    },
                    onSignOutDialogConfirm = {
                        state.value = state.value.copy(
                            isSignOutDialogOpen = false,
                            shouldSignOut = true
                        )
                    },
                    onDeleteDialogDismiss = {
                        state.value = state.value.copy(
                            isDeletionConfirmOpen = false
                        )
                    },
                    onDeleteDialogConfirm = {
                        state.value = state.value.copy(
                            isDeletionConfirmOpen = false,
                            shouldDelete = true
                        )
                    }
                )
            }
        }
    }

    @Test
    fun clickSettingsButton_executesCorrectOnClickListener() {
        composeRule.onNodeWithText(context.getString(R.string.settings)).performClick()
        assertThat(wasSettingsButtonClicked).isTrue()
    }

    @Test
    fun clickSignOutButton_updatesStateAndDialogIsShown() {
        composeRule.onNodeWithText(context.getString(R.string.sign_out)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.isSignOutDialogOpen).isTrue()
    }

    @Test
    fun dismissSignOutDialog_updateStateAccordinglyAndDialogIsClosed() {
        state.value = state.value.copy(
            isSignOutDialogOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isSignOutDialogOpen).isFalse()
    }

    @Test
    fun acceptInSignOutDialog_updateStateAccordinglyAndDialogIsClosed() {
        state.value = state.value.copy(
            isSignOutDialogOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isSignOutDialogOpen).isFalse()
        assertThat(state.value.shouldSignOut).isTrue()
    }

    @Test
    fun deleteAccountIsClicked_updatesStateAndDialogIsShown() {
        composeRule.onNodeWithText(context.getString(R.string.delete_account)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        assertThat(state.value.isDeletionConfirmOpen).isTrue()
    }

    @Test
    fun dismissAccountDeleteDialog_updatesStateAccordinglyAndDialogIsClosed() {
        state.value = state.value.copy(
            isDeletionConfirmOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isDeletionConfirmOpen).isFalse()
    }

    @Test
    fun acceptInDeleteAccountDialog_updateStateAccordinglyAndDialogIsClosed() {
        state.value = state.value.copy(
            isDeletionConfirmOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
        assertThat(state.value.isDeletionConfirmOpen).isFalse()
        assertThat(state.value.shouldDelete).isTrue()
    }
}
