package com.mateusz113.financemanager.presentation.payment_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.common.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun PaymentDetailsScreen(
    id: String,
    viewModel: PaymentDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing
    )
    if (state.error == null) {
        ScaffoldWrapper(
            topAppBar = {
                TopAppBarWithBack(
                    label = R.string.payment_details,
                    navController = navController
                )
            }
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.refresh() }
            ) {
                state.paymentDetails?.let { details ->
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = details.title,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = id,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = details.description,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = details.amount.toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = details.date.toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = details.category.name,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )

                        Button(onClick = {
                            navigator.navigate(
                                PaymentAdditionScreenDestination(
                                    topBarLabel = R.string.edit_payment,
                                    paymentId = id
                                )
                            )
                        }) {
                            Text(text = "Change payment")
                        }
                        details.photoUrls.forEach {
                            SubcomposeAsyncImage(
                                model = it,
                                loading = {
                                    CircularProgressIndicator()
                                },
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}