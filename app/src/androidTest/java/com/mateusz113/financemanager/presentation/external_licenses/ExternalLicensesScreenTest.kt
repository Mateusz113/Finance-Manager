package com.mateusz113.financemanager.presentation.external_licenses

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class ExternalLicensesScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var state: MutableState<ExternalLicensesState>
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                state = remember { mutableStateOf(ExternalLicensesState()) }

                ExternalLicensesScreenContent(
                    state = state.value,
                    topBarLabel = R.string.external_licenses_notice,
                    licensesList = ExternalLicense.values().toList(),
                    onItemClick = {
                        state.value = state.value.copy(
                            licenseText = context.getString(it),
                            isLicenseDialogOpen = true
                        )
                    },
                    onDialogDismiss = {
                        state.value = state.value.copy(isLicenseDialogOpen = false)
                    }
                )
            }
        }
    }

    @Test
    fun clickOnExternalLicense_dialogVisible() {
        composeRule.onNodeWithText(context.getString(R.string.ycharts_label))
            .performClick()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()
    }

    @Test
    fun clickOnExternalLicense_licenseTextIsCorrectlyUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.ycharts_label))
            .performClick()
        assertThat(state.value.licenseText).contains(context.getString(R.string.ycharts_license_text))
    }

    @Test
    fun dismissDialog_dialogIsNotVisible() {
        composeRule.onNodeWithText(context.getString(R.string.ycharts_label))
            .performClick()
        pressBack()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .isNotDisplayed()
    }
}
