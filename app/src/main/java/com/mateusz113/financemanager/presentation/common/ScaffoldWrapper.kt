package com.mateusz113.financemanager.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScaffoldWrapper(
    floatingActionButton: @Composable () -> Unit = {},
    snackbarContent: @Composable (SnackbarData) -> Unit = {},
    topAppBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    pageContent: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = {
            snackbarHostState?.let { state ->
                SnackbarHost(
                    hostState = state
                ) { data ->
                    snackbarContent(data)
                }
            }
        }
    ) { innerPadding ->
        pageContent(innerPadding)
    }
}