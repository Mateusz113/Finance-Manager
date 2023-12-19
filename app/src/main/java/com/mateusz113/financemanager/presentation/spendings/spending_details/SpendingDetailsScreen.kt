package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.presentation.common.PaymentFilterDialog
import com.mateusz113.financemanager.presentation.common.PaymentSearchBar
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.spendings.spending_details.components.PaymentsChart
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SpendingDetailsScreen(
    viewModel: SpendingDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isLoading
    )
    ScaffoldWrapper { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(SpendingDetailsEvent.Refresh)
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                PaymentSearchBar(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = state.filterSettings.query,
                    openFilterDialog = {
                        viewModel.onEvent(
                            SpendingDetailsEvent.UpdateDialogState(true)
                        )
                    },
                    searchValueChange = { query ->
                        viewModel.onEvent(
                            SpendingDetailsEvent.SearchForPayment(query)
                        )
                    }
                )
                PaymentsChart(
                    paymentListings = state.paymentListings
                )
            }
        }
        PaymentFilterDialog(
            currentFilterSettings = state.filterSettings,
            isDialogOpen = state.isDialogOpen,
            dialogOpen = { isOpen ->
                viewModel.onEvent(SpendingDetailsEvent.UpdateDialogState(isOpen))
            },
            updateFilterSettings = { filterSettings ->
                viewModel.onEvent(SpendingDetailsEvent.UpdateFilterSettings(filterSettings))
            }
        )
    }
}