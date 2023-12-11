package com.mateusz113.financemanager.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.ProfileScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SpendingDetailsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    @StringRes val label: Int
) {
    Payments(
        direction = PaymentListingsScreenDestination,
        filledIcon = Icons.Filled.List,
        outlinedIcon = Icons.Outlined.List,
        label = R.string.payments
    ),
    Spendings(
        direction = SpendingDetailsScreenDestination,
        filledIcon = Icons.Filled.Analytics,
        outlinedIcon = Icons.Outlined.Analytics,
        label = R.string.spendings
    ),
    Profile(
        direction = ProfileScreenDestination,
        filledIcon = Icons.Filled.AccountBox,
        outlinedIcon = Icons.Outlined.AccountBox,
        label = R.string.profile
    )
}