package com.mateusz113.financemanager.presentation.external_licenses

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.enumeration.LicenseType
import com.mateusz113.financemanager.presentation.common.components.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.common.dialog.TextInfoDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.external_licenses.components.externalLicensesList
import com.mateusz113.financemanager.util.TestTags
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
        onItemClick = { licenseTextIds ->
            var licenseText = ""
            licenseTextIds.forEach { id ->
                licenseText += context.getString(id)
            }
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
    onItemClick: (List<Int>) -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag(TestTags.LAZY_COLUMN),
            contentPadding = PaddingValues(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            externalLicensesList(
                license = LicenseType.APACHE2,
                externalLicensesMap = state.externalLicensesMap,
                onLicenseClick = onItemClick
            )
            externalLicensesList(
                license = LicenseType.META_LICENSE,
                externalLicensesMap = state.externalLicensesMap,
                onLicenseClick = onItemClick
            )
            externalLicensesList(
                license = LicenseType.ECLIPSE_PUBLIC_LICENSE,
                externalLicensesMap = state.externalLicensesMap,
                onLicenseClick = onItemClick
            )
        }

        TextInfoDialog(
            dialogText = state.licenseText,
            isDialogOpen = state.isLicenseDialogOpen,
            onDismiss = onDialogDismiss
        )
    }
}
