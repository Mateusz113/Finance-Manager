package com.mateusz113.financemanager.presentation.payments.payment_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.mateusz113.financemanager.presentation.payments.payment_listings.components.PaymentListingsFilterDialog
import com.mateusz113.financemanager.presentation.payments.payment_listings.components.PaymentListingsItem
import com.mateusz113.financemanager.presentation.payments.payment_listings.components.PaymentListingsSearchBar
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
        isRefreshing = state.isRefreshing
    )
    ScaffoldWrapper(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigator.navigate(PaymentAdditionScreenDestination(
                        topBarLabel = R.string.new_payment
                    ))
                },
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
                onRefresh = {
                    viewModel.onEvent(PaymentListingsEvent.Refresh)
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        PaymentListingsSearchBar(
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxSize()
                                .padding(16.dp),
                            value = state.filterSettings.query,
                            openFilterDialog = {
                                viewModel.onEvent(
                                    PaymentListingsEvent.UpdateFilterDialogState(true)
                                )
                            },
                            searchValueChange = { query ->
                                viewModel.onEvent(
                                    PaymentListingsEvent.SearchPayment(query)
                                )
                            }
                        )
                    }
                    items(state.payments.size) { i ->
                        PaymentListingsItem(
                            paymentListing = state.payments[i],
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(85.dp)
                                .clickable {
                                    navigator.navigate(PaymentDetailsScreenDestination(id = state.payments[i].id))
                                },
                            deletePayment = {
                                viewModel.onEvent(
                                    PaymentListingsEvent.DeletePayment(state.payments[i].id)
                                )
                            }
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

        PaymentListingsFilterDialog(
            currentFilterSettings = state.filterSettings,
            isDialogOpen = state.isFilterDialogOpen,
            dialogOpen = { isOpen ->
                viewModel.onEvent(PaymentListingsEvent.UpdateFilterDialogState(isOpen))
            },
            updateFilterSettings = { filterSettings ->
                viewModel.onEvent(PaymentListingsEvent.UpdateFilterSettings(filterSettings))
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}

