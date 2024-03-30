package com.mateusz113.financemanager.presentation.external_licenses

import androidx.annotation.StringRes
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
import com.mateusz113.financemanager.presentation.common.dialog.TextInfoDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@Destination
@RootNavGraph
@Composable
fun ExternalLicensesScreen(
    viewModel: ExternalLicensesViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ExternalLicensesScreenContent(
        state = state,
        topBarLabel = R.string.external_licenses_notice,
        navController = navController,
        licensesList = ExternalLicense.values().toList(),
        onItemClick = { licenseTextId ->
            val licenseText = context.getString(licenseTextId)
            viewModel.onEvent(ExternalLicensesEvent.LicenseDialogStateUpdate(true))
            viewModel.onEvent(ExternalLicensesEvent.LicenseDialogInfoUpdate(licenseText))
        },
        onDialogDismiss = {
            viewModel.onEvent(ExternalLicensesEvent.LicenseDialogStateUpdate(false))
        }
    )
}

@Composable
fun ExternalLicensesScreenContent(
    state: ExternalLicensesState,
    @StringRes topBarLabel: Int,
    navController: NavController = NavController(LocalContext.current),
    licensesList: List<ExternalLicense>,
    onItemClick: (Int) -> Unit,
    onDialogDismiss: () -> Unit
) {
    ScaffoldWrapper(
        topAppBar = {
            TopAppBarWithBack(
                label = topBarLabel,
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
            licensesList.forEach { externalLicense ->
                ClickableItemRow(
                    label = stringResource(id = externalLicense.label),
                    onClick = {
                        onItemClick(externalLicense.licenseText)
                    }
                )
            }
        }

        TextInfoDialog(
            dialogText = state.licenseText,
            isDialogOpen = state.isLicenseDialogOpen,
            onDismiss = onDialogDismiss
        )
    }
}
