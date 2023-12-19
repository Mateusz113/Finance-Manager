package com.mateusz113.financemanager.presentation.payments.payment_details

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.stringResource
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
import com.mateusz113.financemanager.presentation.common.PhotoDisplayDialog
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.common.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.payments.payment_details.components.PaymentDetailsInfoBlock
import com.mateusz113.financemanager.presentation.payments.payment_details.components.PaymentDetailsPhotosRow
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
                onRefresh = {
                    viewModel.onEvent(PaymentDetailsEvent.Refresh)
                }
            ) {
                state.paymentDetails?.let { details ->
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        PaymentDetailsInfoBlock(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            paymentDetails = details
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (details.photoUrls.isNotEmpty()) {
                            PaymentDetailsPhotosRow(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                photos = details.photoUrls,
                                onPhotoClick = { photo ->
                                    viewModel.onEvent(PaymentDetailsEvent.UpdateDialogPhoto(photo))
                                    viewModel.onEvent(PaymentDetailsEvent.UpdateDialogState(true))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(0.5f),
                                onClick = {
                                    navigator.navigate(
                                        PaymentAdditionScreenDestination(
                                            topBarLabel = R.string.edit_payment,
                                            paymentId = id
                                        )
                                    )
                                }
                            ) {
                                Text(text = stringResource(id = R.string.edit_payment))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        PhotoDisplayDialog(
            photo = state.dialogPhoto,
            isDialogOpen = state.isPhotoDialogOpen,
            dialogOpen = { isOpen ->
                viewModel.onEvent(PaymentDetailsEvent.UpdateDialogState(isOpen))
            }
        )
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