package com.mateusz113.financemanager.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.destinations.RegisterScreenDestination
import com.mateusz113.financemanager.presentation.sign_in.component.ExternalSignInBlock
import com.mateusz113.financemanager.presentation.sign_in.component.LoginBlock
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    onGoogleSignInClick: () -> Unit,
    onFacebookSignInClick: () -> Unit,
    onGitHubSignInClick: () -> Unit,
    onFirebaseSignInClick: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginBlock(
                modifier = Modifier.fillMaxWidth(0.8f),
                onLoginClick = onFirebaseSignInClick,
                onRegisterClick = {
                    navigator.navigate(RegisterScreenDestination)
                }
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(id = R.string.or_alternatively),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.displaySmall.fontSize
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            ExternalSignInBlock(
                modifier = Modifier.fillMaxWidth(0.8f),
                googleOnClick = onGoogleSignInClick,
                facebookOnClick = onFacebookSignInClick,
                gitHubOnClick = onGitHubSignInClick
            )
        }
    }
}