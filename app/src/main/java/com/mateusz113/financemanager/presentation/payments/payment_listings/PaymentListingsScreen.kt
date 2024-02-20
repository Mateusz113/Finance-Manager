package com.mateusz113.financemanager.presentation.payments.payment_listings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.presentation.common.components.PaymentListingsInfo
import com.mateusz113.financemanager.presentation.common.components.PaymentSearchBar
import com.mateusz113.financemanager.presentation.common.dialog.ConfirmationDialog
import com.mateusz113.financemanager.presentation.common.dialog.PaymentFilterDialog
import com.mateusz113.financemanager.presentation.common.dialog.RadioButtonSelectionDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.mateusz113.financemanager.util.SortingMethod
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
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isLoading
    )

    PaymentListingsScreenContent(
        state = state,
        swipeRefreshState = swipeRefreshState,
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
        filterDialogOpen = { isOpen ->
            viewModel.onEvent(PaymentListingsEvent.UpdateFilterDialogState(isOpen))
        },
        onFilterSettingsUpdate = { filterSettings ->
            viewModel.onEvent(PaymentListingsEvent.UpdateFilterSettings(filterSettings))
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
    swipeRefreshState: SwipeRefreshState = SwipeRefreshState(false),
    onFABClick: () -> Unit,
    onRefresh: () -> Unit,
    onOpenFilterDialog: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onSortingButtonClick: () -> Unit,
    onPaymentClick: (Int) -> Unit,
    onPaymentDeleteClick: (Int) -> Unit,
    filterDialogOpen: (Boolean) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = onRefresh
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(16.dp),
                        ) {
                            PaymentSearchBar(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .fillMaxHeight(),
                                value = state.filterSettings.query,
                                openFilterDialog = onOpenFilterDialog,
                                searchValueChange = onSearchValueChange
                            )

                            OutlinedButton(
                                onClick = onSortingButtonClick,
                                modifier = Modifier
                                    .weight(0.15f)
                                    .padding(start = 10.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(5.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Sort,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(id = R.string.sorting_button)
                                )
                            }
                        }
                    }
                    items(state.payments.size) { i ->
                        PaymentListingsInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(85.dp)
                                .clickable {
                                    onPaymentClick(i)
                                },
                            paymentListing = state.payments[i],
                            currency = state.currency,
                            isCurrencyPrefix = state.isCurrencyPrefix,
                            isDeletable = true,
                            onPaymentDelete = { onPaymentDeleteClick(i) }
                        )
                        if (i < state.payments.size - 1) {
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        PaymentFilterDialog(
            currentFilterSettings = state.filterSettings,
            isDialogOpen = state.isFilterDialogOpen,
            dialogOpen = filterDialogOpen,
            updateFilterSettings = onFilterSettingsUpdate
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
