package com.mateusz113.financemanager.presentation.settings

import android.content.SharedPreferences
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class, AppModule::class)
class SettingsViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: SettingsViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()
        sharedPreferences.edit().clear().apply()
        viewModel = SettingsViewModel(sharedPreferences)
    }

    @Test
    fun initViewModel_correctlyInitializesState() = runTest {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val testCurrency = Currency.EUR
        val testSymbolPlacement = SymbolPlacement.Prefix
        sharedPreferences.edit().apply {
            this.putString("${userId}SymbolPlacement", testSymbolPlacement.name)
            this.putString("${userId}Currency", testCurrency.name)
        }.apply()
        launch { viewModel = SettingsViewModel(sharedPreferences) }.join()

        assertThat(viewModel.state.value.currentCurrency).isEqualTo(testCurrency)
        assertThat(viewModel.state.value.currentSymbolPlacement).isEqualTo(testSymbolPlacement)
    }

    @Test
    fun sendUpdateDialogStateEvent_updatesStateIsDialogOpenFlag() {
        viewModel.onEvent(SettingsEvent.UpdateDialogState(true))
        assertThat(viewModel.state.value.isDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateDialogInfoEvent_updatesDialogInfoInState() {
        val testLabel = R.string.currency
        val testCurrentOption = Currency.EUR
        val testOptionsLabelsMap = Currency.labelMap
        val testOptionsList = Currency.values().toList()
        viewModel.onEvent(
            SettingsEvent.UpdateDialogInfo(
                label = testLabel,
                currentOption = testCurrentOption,
                optionsLabelsMap = testOptionsLabelsMap,
                optionsList = testOptionsList
            )
        )
        assertThat(viewModel.state.value.dialogInfo.label).isEqualTo(testLabel)
        assertThat(viewModel.state.value.dialogInfo.currentOption).isEqualTo(testCurrentOption)
        assertThat(viewModel.state.value.dialogInfo.optionsLabelsMap).isEqualTo(testOptionsLabelsMap)
        assertThat(viewModel.state.value.dialogInfo.optionsList).isEqualTo(testOptionsList)
    }

    @Test
    fun sendUpdateSelectedOptionEventWithCurrency_updatesSelectionInStateAndSharedPreferences() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val testCurrency = Currency.EUR
        viewModel.onEvent(SettingsEvent.UpdateSelectedOption(testCurrency))
        assertThat(viewModel.state.value.currentCurrency).isEqualTo(testCurrency)
        assertThat(
            sharedPreferences.getString(
                "${userId}Currency",
                null
            )
        ).isEqualTo(testCurrency.name)
    }

    @Test
    fun sendUpdateSelectedOptionEventWithSymbolPlacement_updatesSelectionInStateAndSharedPreferences() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val testSymbolPlacement = SymbolPlacement.Prefix
        viewModel.onEvent(SettingsEvent.UpdateSelectedOption(testSymbolPlacement))
        assertThat(viewModel.state.value.currentSymbolPlacement).isEqualTo(testSymbolPlacement)
        assertThat(sharedPreferences.getString("${userId}SymbolPlacement", null)).isEqualTo(
            testSymbolPlacement.name
        )
    }
}
