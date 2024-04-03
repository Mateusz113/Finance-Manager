package com.mateusz113.financemanager.presentation.payments.payment_listings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.enumeration.SortingMethod
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.presentation.common.components.PaymentListingsInfo
import com.mateusz113.financemanager.presentation.common.components.PaymentSearchBar
import com.mateusz113.financemanager.presentation.common.components.PullRefreshLazyColumn
import com.mateusz113.financemanager.presentation.common.dialog.ConfirmationDialog
import com.mateusz113.financemanager.presentation.common.dialog.PaymentFilterDialog
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonSelectionDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun PaymentListingsScreen(
    navigator: DestinationsNavigator,
    viewModel: PaymentListingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PaymentListingsScreenContent(
        state = state,
        onFABClick = {
            navigator.navigate(
                PaymentAdditionScreenDestination(
                    topBarLabel = R.string.new_payment
                )
            )
        },
        onRefresh = {
            viewModel.onEvent(PaymentListingsEvent.Refresh)
        },
        onOpenFilterDialog = {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateFilterDialogState(true)
            )
        },
        onSearchValueChange = { query ->
            viewModel.onEvent(
                PaymentListingsEvent.SearchPayment(query)
            )
        },
        onSortingButtonClick = {
            viewModel.onEvent(
                PaymentListingsEvent.UpdateSortingDialogState(
                    true
                )
            )
        },
        onPaymentClick = { paymentIndex ->
            navigator.navigate(PaymentDetailsScreenDestination(id = state.payments[paymentIndex].id))
        },
        onPaymentDeleteClick = { paymentIndex ->
            viewModel.onEvent(
                PaymentListingsEvent.UpdateDeleteDialogInfo(
                    id = state.payments[paymentIndex].id,
                    title = state.payments[paymentIndex].title
                )
            )
            viewModel.onEvent(PaymentListingsEvent.UpdateDeleteDialogState(true))
        },
        onFilterDialogDismiss = {
            viewModel.onEvent(PaymentListingsEvent.UpdateFilterDialogState(false))
        },
        onFilterSettingsUpdate = { filterSettings ->
            viewModel.onEvent(PaymentListingsEvent.UpdateFilterSettings(filterSettings))
            viewModel.onEvent(PaymentListingsEvent.UpdateFilterDialogState(false))
        },
        onConfirmationDialogDismiss = {
            viewModel.onEvent(PaymentListingsEvent.UpdateDeleteDialogState(false))
        },
        onConfirmationDialogConfirm = {
            viewModel.onEvent(PaymentListingsEvent.DeletePayment(state.deleteDialogPaymentId))
            viewModel.onEvent(PaymentListingsEvent.UpdateDeleteDialogState(false))
        },
        onSortingDialogDismiss = {
            viewModel.onEvent(PaymentListingsEvent.UpdateSortingDialogState(false))
        },
        onSortingDialogSelect = { sortingMethod ->
            viewModel.onEvent(PaymentListingsEvent.UpdateSortingMethod(sortingMethod))
            viewModel.onEvent(PaymentListingsEvent.UpdateSortingDialogState(false))
        }
    )
}

@Composable
fun PaymentListingsScreenContent(
    state: PaymentListingsState,
    onFABClick: () -> Unit,
    onRefresh: () -> Unit,
    onOpenFilterDialog: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onSortingButtonClick: () -> Unit,
    onPaymentClick: (Int) -> Unit,
    onPaymentDeleteClick: (Int) -> Unit,
    onFilterDialogDismiss: () -> Unit,
    onFilterSettingsUpdate: (FilterSettings) -> Unit,
    onConfirmationDialogDismiss: () -> Unit,
    onConfirmationDialogConfirm: () -> Unit,
    onSortingDialogDismiss: () -> Unit,
    onSortingDialogSelect: (SortingMethod) -> Unit
) {
    ScaffoldWrapper(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFABClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_new_payment),
                )
            }
        }
    ) { innerPadding ->
        val searchBarHeight = 80.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(searchBarHeight)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    //Set the background color and zIndex to hide the refresh indicator
                    .background(MaterialTheme.colorScheme.background)
                    .zIndex(1f),
            ) {
                PaymentSearchBar(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight(0.8f),
                    value = state.filterSettings.query,
                    onFilterDialogOpen = onOpenFilterDialog,
                    onSearchValueChange = onSearchValueChange
                )
                OutlinedButton(
                    onClick = onSortingButtonClick,
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(start = 10.dp)
                        .fillMaxHeight(0.8f),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        modifier = Modifier.size(20.dp),
                        contentDescription = stringResource(id = R.string.sorting_button)
                    )
                }
            }

            PullRefreshLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                isRefreshing = state.isLoading,
                onRefresh = onRefresh,
            ) {
                for (index in state.payments.indices) {
                    it.item {
                        PaymentListingsInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(85.dp)
                                .clickable {
                                    onPaymentClick(index)
                                },
                            paymentListing = state.payments[index],
                            currency = state.currency,
                            isCurrencyPrefix = state.isCurrencyPrefix,
                            isDeletable = true,
                            onPaymentDelete = { onPaymentDeleteClick(index) }
                        )
                        if (index < state.payments.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        PaymentFilterDialog(
            filterSettings = state.filterSettings,
            isDialogOpen = state.isFilterDialogOpen,
            onFilterSettingsUpdate = onFilterSettingsUpdate,
            onDismiss = onFilterDialogDismiss
        )

        ConfirmationDialog(
            dialogTitle = stringResource(id = R.string.deletion),
            dialogText = stringResource(
                id = R.string.deletion_text,
                state.deleteDialogPaymentTitle
            ),
            isDialogOpen = state.isDeleteDialogOpen,
            onDismiss = onConfirmationDialogDismiss,
            onConfirm = onConfirmationDialogConfirm
        )

        RadioButtonSelectionDialog(
            isDialogOpen = state.isSortingMethodDialogOpen,
            dialogInfo = state.sortingSettingsInfo,
            onDismiss = onSortingDialogDismiss,
            onOptionSelect = onSortingDialogSelect
        )
    }
}
