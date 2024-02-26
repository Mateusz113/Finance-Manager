package com.mateusz113.financemanager.presentation.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mateusz113.financemanager.presentation.register.component.RegisterBlock
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        RegisterBlock(
            modifier = Modifier.fillMaxWidth(0.8f),
            onRegisterClick = onRegisterClick
        )
    }
}
