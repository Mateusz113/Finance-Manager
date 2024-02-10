package com.mateusz113.financemanager.presentation.sign_in.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.util.AuthMethod

@Composable
fun ExternalSignInBlock(
    modifier: Modifier,
    googleOnClick: () -> Unit,
    facebookOnClick: () -> Unit,
    gitHubOnClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExternalSignInButton(
            authMethod = AuthMethod.GOOGLE,
            modifier = Modifier.fillMaxWidth(),
            onClick = googleOnClick
        )
        Spacer(modifier = Modifier.height(20.dp))

        ExternalSignInButton(
            authMethod = AuthMethod.FACEBOOK,
            modifier = Modifier.fillMaxWidth(),
            onClick = facebookOnClick
        )
        Spacer(modifier = Modifier.height(20.dp))

        ExternalSignInButton(
            authMethod = AuthMethod.GITHUB,
            modifier = Modifier.fillMaxWidth(),
            onClick = gitHubOnClick
        )
    }
}