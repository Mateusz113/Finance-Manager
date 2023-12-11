package com.mateusz113.financemanager.presentation.spending_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun SpendingDetailsScreen() {
    Text(
        text = "Spending chart",
        modifier = Modifier.fillMaxSize()
    )
}