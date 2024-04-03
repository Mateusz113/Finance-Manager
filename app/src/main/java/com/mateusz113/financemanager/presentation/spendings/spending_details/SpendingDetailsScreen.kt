package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.presentation.common.components.PaymentSearchBar
import com.mateusz113.financemanager.presentation.common.components.PullRefreshLazyColumn
import com.mateusz113.financemanager.presentation.common.dialog.PaymentFilterDialog
import com.mateusz113.financemanager.presentation.common.dialog.PaymentListingsCollectionDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
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

    SpendingDetailsScreenContent(
        state = state,
        onRefresh = {
            viewModel.onEvent(SpendingDetailsEvent.Refresh)
        },
        onFilterDialogOpen = {
            viewModel.onEvent(
                SpendingDetailsEvent.UpdateFilterDialogState(true)
            )
        },
        onSearchValueChange = { query ->
            viewModel.onEvent(
                SpendingDetailsEvent.SearchForPayment(query)
            )
        },
        onKeyClick = { slice ->
            viewModel.onEvent(SpendingDetailsEvent.UpdateCurrentSlice(slice))
            viewModel.onEvent(SpendingDetailsEvent.UpdateSliceDialogState(true))
        },
        onPaymentFilterDialogDismiss = {
            viewModel.onEvent(SpendingDetailsEvent.UpdateFilterDialogState(false))
        },
        onFilterSettingsUpdate = { filterSettings ->
            viewModel.onEvent(SpendingDetailsEvent.UpdateFilterSettings(filterSettings))
            viewModel.onEvent(SpendingDetailsEvent.UpdateFilterDialogState(false))
        },
        onPaymentListingsDialogDismiss = {
            viewModel.onEvent(SpendingDetailsEvent.UpdateSliceDialogState(false))
        },
        onPaymentClick = { listing ->
            viewModel.onEvent(SpendingDetailsEvent.UpdateSliceDialogState(false))
            navigator.navigate(
                direction = PaymentDetailsScreenDestination(
                    id = listing.id
                )
            )
        }
    )
}

@Composable
fun SpendingDetailsScreenContent(
    state: SpendingDetailsState,
    onRefresh: () -> Unit,
    onFilterDialogOpen: () -> Unit,
    onSearchValueChange: (String) -> Unit,
    onKeyClick: (PieChartData.Slice) -> Unit,
    onPaymentFilterDialogDismiss: () -> Unit,
    onFilterSettingsUpdate: (FilterSettings) -> Unit,
    onPaymentListingsDialogDismiss: () -> Unit,
    onPaymentClick: (PaymentListing) -> Unit
) {
    ScaffoldWrapper { innerPadding ->
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
                        .fillMaxHeight(0.8f),
                    value = state.filterSettings.query,
                    onFilterDialogOpen = onFilterDialogOpen,
                    onSearchValueChange = onSearchValueChange
                )
            }

            PullRefreshLazyColumn(
                isRefreshing = state.isLoading,
                onRefresh = onRefresh
            ) {
                it.item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                it.item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.spendings_chart),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize
                        )
                    )
                }

                it.item {
                    PaymentsChart(
                        listingsMap = state.listingsMap,
                        currency = state.currency,
                        onKeyClick = onKeyClick
                    )
                }
            }
        }

        PaymentFilterDialog(
            filterSettings = state.filterSettings,
            isDialogOpen = state.isFilterDialogOpen,
            onFilterSettingsUpdate = onFilterSettingsUpdate,
            onDismiss = onPaymentFilterDialogDismiss
        )
        PaymentListingsCollectionDialog(
            paymentListings = state.listingsMap[Category.valueOf(state.currentSlice.label)]
                ?: emptyList(),
            currency = state.currency,
            isCurrencyPrefix = state.isCurrencyPrefix,
            isDialogOpen = state.isKeyDialogOpen,
            onPaymentClick = onPaymentClick,
            onDismiss = onPaymentListingsDialogDismiss
        )
    }
}
