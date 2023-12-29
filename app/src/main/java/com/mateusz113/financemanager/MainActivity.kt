package com.mateusz113.financemanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.appCurrentDestinationAsState
import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient
import com.mateusz113.financemanager.presentation.common.bottom_nav.BottomNavigationBar
import com.mateusz113.financemanager.presentation.destinations.Destination
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.ProfileScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SettingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SignInScreenDestination
import com.mateusz113.financemanager.presentation.profile.ProfileScreen
import com.mateusz113.financemanager.presentation.sign_in.SignInScreen
import com.mateusz113.financemanager.presentation.sign_in.SignInViewModel
import com.mateusz113.financemanager.ui.theme.FinanceManagerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.popUpTo
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
                val screensWithoutBottomNav = remember {
                    mutableListOf<Destination>(
                        PaymentDetailsScreenDestination,
                        PaymentAdditionScreenDestination,
                        SignInScreenDestination,
                        SettingsScreenDestination
                    )
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentDestination = navController.appCurrentDestinationAsState().value
                    val startRoute =
                        if (googleAuthUiClient.getSignedInUser() == null) SignInScreenDestination else NavGraphs.root.startRoute

                    Scaffold(
                        bottomBar = {
                            if (!screensWithoutBottomNav.contains(currentDestination)) {
                                BottomNavigationBar(
                                    navController = navController
                                )
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(it)) {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                navController = navController,
                                startRoute = startRoute
                            ) {
                                composable(SignInScreenDestination) {
                                    val viewModel = hiltViewModel<SignInViewModel>()
                                    val state by viewModel.state.collectAsStateWithLifecycle()

                                    //Navigates to signed in screen if sign in was successful
                                    LaunchedEffect(key1 = Unit) {
                                        if (googleAuthUiClient.getSignedInUser() != null) {
                                            destinationsNavigator.navigate(
                                                PaymentListingsScreenDestination
                                            ) {
                                                popUpTo(NavGraphs.root.startRoute) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }

                                    //Intent launcher
                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
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
                                            destinationsNavigator.navigate(
                                                PaymentListingsScreenDestination
                                            )
                                            viewModel.resetState()

                                        }
                                    }

                                    SignInScreen(
                                        navigator = destinationsNavigator,
                                        onSignInClick = {
                                            lifecycleScope.launch {
                                                val signInIntentSender =
                                                    googleAuthUiClient.getIntentSender()
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
                                        navigator = destinationsNavigator,
                                        googleAuthUiClient = googleAuthUiClient
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
