package com.mateusz113.financemanager.presentation.external_licenses

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
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
class ExternalLicensesViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: ExternalLicensesViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        val viewModelProvider = ViewModelProvider(composeRule.activity)
        viewModel = viewModelProvider[ExternalLicensesViewModel::class.java]
    }

    @Test
    fun callOnEventWithLicenseDialogStateUpdate_updatesIsLicenseDialogOpenCorrectly() {
        viewModel.onEvent(ExternalLicensesEvent.LicenseDialogStateUpdate(true))
        assertThat(viewModel.state.value.isLicenseDialogOpen).isTrue()
    }

    @Test
    fun callOnEventWithLicenseDialogInfoUpdate_updatesLicenseTextCorrectly() {
        val newText = "Example text"
        viewModel.onEvent(ExternalLicensesEvent.LicenseDialogInfoUpdate(newText))
        assertThat(viewModel.state.value.licenseText).contains(newText)
    }
}
