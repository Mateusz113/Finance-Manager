package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.ui.piechart.models.PieChartData
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.presentation.common.components.PaymentSearchBar
import com.mateusz113.financemanager.presentation.common.dialog.PaymentFilterDialog
import com.mateusz113.financemanager.presentation.common.dialog.PaymentListingsCollectionDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.mateusz113.financemanager.presentation.spendings.spending_details.components.PaymentsChart
import com.mateusz113.financemanager.util.TestTags
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDate

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

@OptIn(ExperimentalMaterialApi::class)
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
    ScaffoldWrapper { paddingValues ->
        val searchBarHeight = 80.dp
        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.isLoading,
            onRefresh = onRefresh,
            refreshThreshold = 60.dp
        )
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier
                    .testTag(TestTags.SWIPE_REFRESH_INDICATOR)
                    .align(Alignment.TopCenter)
                    .offset(y = searchBarHeight)
                    .zIndex(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .testTag(TestTags.SCROLLABLE_COLUMN)
            ) {
                PaymentSearchBar(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = state.filterSettings.query,
                    onFilterDialogOpen = onFilterDialogOpen,
                    onSearchValueChange = onSearchValueChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.spendings_chart),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize
                    )
                )

                PaymentsChart(
                    listingsMap = state.listingsMap,
                    currency = state.currency,
                    onKeyClick = onKeyClick
                )
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
