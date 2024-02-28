package com.mateusz113.financemanager.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.components.ClickableItemRow
import com.mateusz113.financemanager.presentation.common.components.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonSelectionDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.ExternalLicensesScreenDestination
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate

@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        state = state,
        navController = navController,
        onCurrencyClick = {
            viewModel.onEvent(
                SettingsEvent.UpdateDialogInfo(
                    label = R.string.currency,
                    currentOption = state.currentCurrency,
                    optionsLabelsMap = Currency.labelMap,
                    optionsList = Currency.values().toList()
                )
            )
            viewModel.onEvent(SettingsEvent.UpdateDialogState(true))
        },
        onSymbolPlacementClick = {
            viewModel.onEvent(
                SettingsEvent.UpdateDialogInfo(
                    label = R.string.symbol_placement,
                    currentOption = state.currentSymbolPlacement,
                    optionsLabelsMap = SymbolPlacement.labelMap,
                    optionsList = SymbolPlacement.values().toList()
                )
            )
            viewModel.onEvent(SettingsEvent.UpdateDialogState(true))
        },
        onDialogDismiss = {
            viewModel.onEvent(SettingsEvent.UpdateDialogState(false))
        },
        onOptionSelect = { option ->
            viewModel.onEvent(SettingsEvent.UpdateDialogState(false))
            viewModel.onEvent(SettingsEvent.UpdateSelectedOption(option))
        }
    )
}

@Composable
fun SettingsScreenContent(
    state: SettingsState,
    navController: NavController = NavController(LocalContext.current),
    onCurrencyClick: () -> Unit,
    onSymbolPlacementClick: () -> Unit,
    onDialogDismiss: () -> Unit,
    onOptionSelect: (Any?) -> Unit
) {
    ScaffoldWrapper(
        topAppBar = {
            TopAppBarWithBack(
                label = R.string.settings,
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClickableItemRow(
                label = stringResource(id = R.string.currency_option),
                bottomText = stringResource(
                    id = Currency.labelMap[state.currentCurrency] ?: R.string.unknown
                ),
                onClick = onCurrencyClick
            )

            ClickableItemRow(
                label = stringResource(id = R.string.symbol_placement_option),
                bottomText = stringResource(
                    id = SymbolPlacement.labelMap[state.currentSymbolPlacement] ?: R.string.unknown
                ),
                onClick = onSymbolPlacementClick
            )

            ClickableItemRow(
                label = "ExternalLicenses",
                onClick = {
                    navController.navigate(ExternalLicensesScreenDestination)
                }
            )
        }

        RadioButtonSelectionDialog(
            isDialogOpen = state.isDialogOpen,
            dialogInfo = state.dialogInfo,
            onDismiss = onDialogDismiss,
            onOptionSelect = onOptionSelect
        )
    }
}
