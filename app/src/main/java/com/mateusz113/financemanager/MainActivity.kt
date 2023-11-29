package com.mateusz113.financemanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.destinations.ProfileScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SignInScreenDestination
import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.sign_in.SignInScreen
import com.mateusz113.financemanager.presentation.sign_in.SignInViewModel
import com.mateusz113.financemanager.presentation.profile.ProfileScreen
import com.mateusz113.financemanager.ui.theme.FinanceManagerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root) {
                        composable(SignInScreenDestination) {
                            val viewModel = hiltViewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            //Navigates to signed in screen if sign in was successful
                            LaunchedEffect(key1 = Unit){
                                if (googleAuthUiClient.getSignedInUser() != null){
                                    destinationsNavigator.navigate(PaymentListingsScreenDestination)
                                }
                            }

                            //Intent launcher
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            //Navigates user to signed it page when the sign in is successful
                            LaunchedEffect(
                                key1 = state.isSignInSuccessful,
                            ) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    destinationsNavigator.navigate(PaymentListingsScreenDestination)
                                    viewModel.resetState()

                                }
                            }

                            SignInScreen(
                                navigator = destinationsNavigator,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.getIntentSender()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(ProfileScreenDestination) {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOutClick = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        destinationsNavigator.popBackStack()
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FinanceManagerTheme {
        Greeting("Android")
    }
}