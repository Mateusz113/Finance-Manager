package com.mateusz113.financemanager.presentation.payment_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@Destination
@Composable
fun PaymentDetailsScreen(
    id: String,
    viewModel: PaymentDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state.error == null) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            state.paymentDetails?.let { details ->
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