package com.mateusz113.financemanager.presentation.payment_listings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RootNavGraph
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Button(onClick = {
                viewModel.onEvent(PaymentListingsEvent.AddPayment)
                println("Add button pressed")
            }) {
                Text(text = "Add new payment")
            }
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(PaymentListingsEvent.Refresh)
            }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.payments.size) { i ->
                    PaymentListingsItem(
                        paymentListing = state.payments[i],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                navigator.navigate(PaymentDetailsScreenDestination(id = state.payments[i].id))
                            },
                        deletePayment = {
                            viewModel.onEvent(
                                PaymentListingsEvent.DeletePayment,
                                state.payments[i].id
                            )
                        }
                    )
                    if (i < state.payments.size) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
