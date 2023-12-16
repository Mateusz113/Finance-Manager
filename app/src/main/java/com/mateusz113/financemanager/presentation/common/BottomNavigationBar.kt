package com.mateusz113.financemanager.presentation.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.mateusz113.financemanager.presentation.NavGraphs
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Route
import com.ramcosta.composedestinations.utils.isRouteOnBackStack

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            val isCurrentDestOnBackStack = navController.isRouteOnBackStack(destination.direction)
            NavigationBarItem(
                selected = isCurrentDestOnBackStack,
                onClick = {
                    if (isCurrentDestOnBackStack) {
                        navController.popBackStack(destination.direction, false)
                        return@NavigationBarItem
                    }
                    navController.navigate(destination.direction) {
                        popUpTo(NavGraphs.root){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                },
                icon = {
                    if (isCurrentDestOnBackStack) {
                        Icon(
                            imageVector = destination.filledIcon,
                            contentDescription = stringResource(
                                id = destination.label
                            )
                        )
                    } else {
                        Icon(
                            imageVector = destination.outlinedIcon,
                            contentDescription = stringResource(
                                id = destination.label
                            )
                        )
                    }
                },
                label = {
                    Text(text = stringResource(id = destination.label))
                }
            )
        }
    }
}