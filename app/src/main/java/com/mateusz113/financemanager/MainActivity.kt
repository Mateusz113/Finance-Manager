package com.mateusz113.financemanager

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.data.converter.DateConverter
import com.mateusz113.financemanager.di.DaggerSharedPrefsSetupComponent
import com.mateusz113.financemanager.domain.enumeration.AuthMethod
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.appCurrentDestinationAsState
import com.mateusz113.financemanager.presentation.auth.FacebookAuthUiClient
import com.mateusz113.financemanager.presentation.auth.FirebaseAuthUiClient
import com.mateusz113.financemanager.presentation.auth.GitHubAuthUiClient
import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient
import com.mateusz113.financemanager.presentation.common.bottom_nav.BottomNavigationBar
import com.mateusz113.financemanager.presentation.destinations.Destination
import com.mateusz113.financemanager.presentation.destinations.ExternalLicensesScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentAdditionScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentDetailsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.ProfileScreenDestination
import com.mateusz113.financemanager.presentation.destinations.RegisterScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SettingsScreenDestination
import com.mateusz113.financemanager.presentation.destinations.SignInScreenDestination
import com.mateusz113.financemanager.presentation.profile.ProfileScreen
import com.mateusz113.financemanager.presentation.register.RegisterScreen
import com.mateusz113.financemanager.presentation.sign_in.SignInScreen
import com.mateusz113.financemanager.ui.theme.FinanceManagerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //Google auth
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    //Facebook auth
    private val facebookAuthUiClient by lazy {
        FacebookAuthUiClient(
            context = applicationContext,
            activity = this@MainActivity,
            onSignIn = { result ->
                isUserLoggedIn = result.wasSignInSuccessful
            }
        )
    }

    //GitHub auth
    private val gitHubAuthUiClient by lazy {
        GitHubAuthUiClient(
            activity = this@MainActivity
        )
    }

    //Firebase auth
    private val firebaseAuthUiClient by lazy {
        FirebaseAuthUiClient(
            context = applicationContext
        )
    }

    //State to keep track if user is logged in
    private var isUserLoggedIn by mutableStateOf(false)

    //Variable to keep track of what authentication method was used
    private lateinit var authMethod: AuthMethod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences =
            applicationContext.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        authMethod = FirebaseAuth.getInstance().currentUser?.let { user ->
            AuthMethod.valueOf(
                sharedPreferences.getString(
                    "${user.uid}AuthMethod",
                    "FIREBASE"
                )!!
            )
        } ?: AuthMethod.FIREBASE
        setContent {
            FinanceManagerTheme {
                val screensWithoutBottomNav = remember {
                    mutableListOf<Destination>(
                        PaymentDetailsScreenDestination,
                        PaymentAdditionScreenDestination,
                        SignInScreenDestination,
                        SettingsScreenDestination,
                        RegisterScreenDestination,
                        ExternalLicensesScreenDestination
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
                        if (FirebaseAuth.getInstance().currentUser == null) SignInScreenDestination else NavGraphs.root.startRoute

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
                                    //Intent launcher
                                    val googleLauncher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
                                                            intent = result.data ?: return@launch
                                                        )
                                                    isUserLoggedIn =
                                                        signInResult.wasSignInSuccessful
                                                }
                                            }
                                        }
                                    )

                                    //Navigates user to signed it page when the sign in is successful
                                    LaunchedEffect(
                                        key1 = isUserLoggedIn,
                                    ) {
                                        if (isUserLoggedIn) {
                                            setupSharedPreferences(sharedPreferences)
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign in successful",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            destinationsNavigator.navigate(
                                                PaymentListingsScreenDestination
                                            )
                                            isUserLoggedIn = false
                                        }
                                    }

                                    SignInScreen(
                                        navigator = destinationsNavigator,
                                        onGoogleSignInClick = {
                                            lifecycleScope.launch {
                                                val signInIntentSender =
                                                    googleAuthUiClient.getIntentSender()
                                                googleLauncher.launch(
                                                    IntentSenderRequest.Builder(
                                                        signInIntentSender ?: return@launch
                                                    ).build()
                                                )
                                                authMethod = AuthMethod.GOOGLE
                                            }
                                        },
                                        onFacebookSignInClick = {
                                            lifecycleScope.launch {
                                                facebookAuthUiClient.openLoginPage()
                                                authMethod = AuthMethod.FACEBOOK
                                            }
                                        },
                                        onGitHubSignInClick = {
                                            gitHubAuthUiClient.signIn(
                                                onSignInComplete = { signInResult ->
                                                    isUserLoggedIn =
                                                        signInResult.wasSignInSuccessful
                                                    authMethod = AuthMethod.GITHUB
                                                }
                                            )
                                        },
                                        onFirebaseSignInClick = { email, password ->
                                            lifecycleScope.launch {
                                                val signInResult =
                                                    firebaseAuthUiClient.signIn(email, password)
                                                isUserLoggedIn = signInResult.wasSignInSuccessful
                                            }
                                        }
                                    )
                                }
                                composable(ProfileScreenDestination) {
                                    ProfileScreen(
                                        navigator = destinationsNavigator,
                                        authUiClient = when (authMethod) {
                                            AuthMethod.FACEBOOK -> {
                                                facebookAuthUiClient
                                            }

                                            AuthMethod.GOOGLE -> {
                                                googleAuthUiClient
                                            }

                                            AuthMethod.GITHUB -> {
                                                gitHubAuthUiClient
                                            }

                                            AuthMethod.FIREBASE -> {
                                                firebaseAuthUiClient
                                            }
                                        }
                                    )
                                }
                                composable(RegisterScreenDestination) {
                                    LaunchedEffect(
                                        key1 = isUserLoggedIn,
                                    ) {
                                        if (isUserLoggedIn) {
                                            setupSharedPreferences(sharedPreferences)
                                            Toast.makeText(
                                                applicationContext,
                                                "Register successful",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            destinationsNavigator.navigate(
                                                PaymentListingsScreenDestination
                                            )
                                            isUserLoggedIn = false
                                        }
                                    }
                                    RegisterScreen(
                                        onRegisterClick = { displayName, email, password ->
                                            lifecycleScope.launch {
                                                val result = firebaseAuthUiClient.register(
                                                    displayName,
                                                    email,
                                                    password
                                                )
                                                isUserLoggedIn = result.wasSignInSuccessful
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupSharedPreferences(
        sharedPreferences: SharedPreferences
    ) {
        val component = DaggerSharedPrefsSetupComponent.create()
        val sharedPreferencesSetup =
            component.getSharedPreferencesSetup()
        val userJoinDate =
            FirebaseAuth.getInstance().currentUser?.metadata?.creationTimestamp?.let { timestamp ->
                DateConverter.convertTimestampIntoLocalDate(timestamp)
            } ?: LocalDate.now()
        sharedPreferencesSetup.setupSharedPreferences(
            sharedPreferences = sharedPreferences,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            userJoinDate = userJoinDate,
            authMethod = authMethod
        )
    }

    private fun clearCache() {
        try {
            val cacheDir = applicationContext.cacheDir
            if (cacheDir.exists()) {
                cacheDir.listFiles()?.forEach { file ->
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        facebookAuthUiClient.unregisterCallback()
        clearCache()
        super.onDestroy()
    }
}
