package com.mateusz113.financemanager.presentation.external_licenses

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.AnnotatedString
import androidx.test.espresso.Espresso.pressBack
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import com.mateusz113.financemanager.domain.enumeration.LicenseType
import com.mateusz113.financemanager.util.TestTags
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
        state = mutableStateOf(
            ExternalLicensesState(
                externalLicensesMap = LicenseType.values().associateWith { licenseType ->
                    ExternalLicense.values().filter { it.licenseType == licenseType }
                }
            )
        )

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                ExternalLicensesScreenContent(
                    state = state.value,
                    topBarLabel = R.string.external_licenses_notice,
                    onItemClick = { textIds ->
                        var licenseText = ""
                        textIds.forEach { id ->
                            licenseText += context.getString(id)
                        }
                        state.value = state.value.copy(
                            licenseText = licenseText,
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
    fun clickOnLicenses_dialogsAreVisibleWithCorrectTexts() {
        val lazyColumn = composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
        LicenseType.values().forEach { license ->
            val licenseLabel = context.getString(license.label)
            lazyColumn.performScrollToNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Text, listOf(
                        AnnotatedString(licenseLabel)
                    )
                )
            )
            composeRule.onNodeWithText(licenseLabel).performClick()
            composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
                .assertIsDisplayed()

            val licenseText = buildString {
                license.licenseTextParts.forEach { id ->
                    append(context.getString(id))
                }
            }
            composeRule.onNodeWithText(licenseText).assertIsDisplayed()
        }
    }

    @Test
    fun loadTheExternalLicensesScreen_allTheExternalLicensesAreVisible() {
        val lazyColumn = composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
        ExternalLicense.values().forEach { externalLicense ->
            lazyColumn.performScrollToNode(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Text, listOf(
                        AnnotatedString(
                            context.getString(
                                R.string.external_license_template,
                                context.getString(externalLicense.label),
                                context.getString(externalLicense.copyright)
                            )
                        )
                    )
                )
            ).assertIsDisplayed()
        }
    }

    @Test
    fun dismissDialog_dialogIsNotVisible() {
        //Programmatically open dialog
        state.value = state.value.copy(
            isLicenseDialogOpen = true
        )
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsDisplayed()

        pressBack()
        composeRule.waitForIdle()

        assertThat(state.value.isLicenseDialogOpen).isFalse()
        composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.IsDialog, Unit))
            .assertIsNotDisplayed()
    }
}
