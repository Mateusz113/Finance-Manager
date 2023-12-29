package com.mateusz113.financemanager.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient
import com.mateusz113.financemanager.presentation.common.dialog.ConfirmationDialog
import com.mateusz113.financemanager.presentation.destinations.SettingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SignInScreenDestination
import com.mateusz113.financemanager.presentation.profile.components.ProfileInfo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph
@Destination
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    googleAuthUiClient: GoogleAuthUiClient,
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.updateAuthClient(googleAuthUiClient)
    }

    LaunchedEffect(key1 = state.shouldDelete) {
        if (state.shouldDelete) {
            viewModel.onEvent(ProfileEvent.UpdateDeleteCondition(false))
            coroutineScope.launch {
                val success = viewModel.deleteAccount()
                if (success) {
                    Toast.makeText(
                        context,
                        getString(context, R.string.delete_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigator.navigate(SignInScreenDestination)
                } else {
                    Toast.makeText(
                        context,
                        getString(context, R.string.delete_unsuccessful),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.shouldSignOut) {
        if (state.shouldSignOut) {
            viewModel.onEvent(ProfileEvent.UpdateSignOutCondition(false))
            coroutineScope.launch {
                viewModel.signOut()
                navigator.navigate(SignInScreenDestination)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileInfo(
            modifier = Modifier.fillMaxWidth(),
            profilePictureUrl = state.profilePictureUrl,
            username = state.username,
            email = state.email,
            joinDate = state.joinDate,
            paymentsNumber = state.paymentsNumber
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f),
            onClick = {
                navigator.navigate(SettingsScreenDestination)
            }
        ) {
            Text(text = stringResource(id = R.string.settings))
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f),
            onClick = {
                viewModel.onEvent(ProfileEvent.UpdateSignOutDialogState(true))
            }
        ) {
            Text(text = stringResource(id = R.string.sign_out))
        }
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f),
            onClick = {
                viewModel.onEvent(ProfileEvent.UpdateConfirmationDialogState(true))
            }
        ) {
            Text(text = stringResource(id = R.string.delete_account))
        }
    }
    ConfirmationDialog(
        dialogTitle = stringResource(id = R.string.account_deletion),
        dialogText = stringResource(id = R.string.acc_deletion_confirmation_text),
        isDialogOpen = state.isDeletionConfirmOpen,
        onDismiss = {
            viewModel.onEvent(ProfileEvent.UpdateConfirmationDialogState(false))
        },
        onConfirm = {
            viewModel.onEvent(ProfileEvent.UpdateConfirmationDialogState(false))
            viewModel.onEvent(ProfileEvent.UpdateDeleteCondition(true))
        }
    )
    ConfirmationDialog(
        dialogTitle = stringResource(id = R.string.sign_out),
        dialogText = stringResource(id = R.string.sign_out_text),
        isDialogOpen = state.isSignOutDialogOpen,
        onDismiss = {
            viewModel.onEvent(ProfileEvent.UpdateSignOutDialogState(false))
        },
        onConfirm = {
            viewModel.onEvent(ProfileEvent.UpdateSignOutDialogState(false))
            viewModel.onEvent(ProfileEvent.UpdateSignOutCondition(true))
        }
    )
}