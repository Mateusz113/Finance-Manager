package com.mateusz113.financemanager.presentation.profile

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mateusz113.financemanager.di.AppModule
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.enumeration.AuthMethod
import com.mateusz113.financemanager.presentation.auth.TestAuthUiClient
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class, AppModule::class)
class ProfileViewModelTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val userId = "ID"
    private val userDisplayName = "Display name"
    private val userEmail = "email@email.com"
    private val userPhotoUrl = Uri.parse("https://w.photo.c/image.png")
    private val userJoinDate = "01/01/2020"
    private val userPaymentNumber = 10

    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelCoroutineContext: CoroutineContext

    private lateinit var firebaseMock: FirebaseAuth
    private lateinit var userMock: FirebaseUser
    private lateinit var taskMock: Task<Void>

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        hiltRule.inject()

        mockkStatic(FirebaseAuth::class)
        firebaseMock = mockk<FirebaseAuth>()
        userMock = mockk<FirebaseUser>()
        taskMock = mockk<Task<Void>>()
        every { FirebaseAuth.getInstance() } returns firebaseMock
        every { firebaseMock.currentUser } returns userMock
        every { userMock.uid } returns userId
        every { userMock.delete() } returns taskMock
        every { taskMock.isSuccessful } returns true

        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit().apply {
            this.putString("${userId}JoinDate", userJoinDate)
            this.putInt("${userId}PaymentsNum", userPaymentNumber)
            this.putString("${userId}AuthMethod", AuthMethod.FACEBOOK.name)
        }.apply()

        viewModel = ProfileViewModel(sharedPreferences)
        viewModelCoroutineContext = viewModel.viewModelScope.coroutineContext
    }

    @Test
    fun sendUpdateConfirmationDialogStateEvent_updatesCorrectIsOpenFlagInState() {
        assertThat(viewModel.state.value.isDeletionConfirmOpen).isFalse()
        viewModel.onEvent(ProfileEvent.UpdateConfirmationDialogState(true))
        assertThat(viewModel.state.value.isDeletionConfirmOpen).isTrue()
    }

    @Test
    fun sendUpdateSignOutDialogStateEvent_updatesCorrectIsOpenFlagInState() {
        assertThat(viewModel.state.value.isSignOutDialogOpen).isFalse()
        viewModel.onEvent(ProfileEvent.UpdateSignOutDialogState(true))
        assertThat(viewModel.state.value.isSignOutDialogOpen).isTrue()
    }

    @Test
    fun sendUpdateDeleteConditionEvent_updatesDeleteConditionInState() {
        assertThat(viewModel.state.value.shouldDelete).isFalse()
        viewModel.onEvent(ProfileEvent.UpdateDeleteCondition(true))
        assertThat(viewModel.state.value.shouldDelete).isTrue()

    }

    @Test
    fun sendUpdateSignOutConditionEvent_updatesSignOutConditionInState() {
        assertThat(viewModel.state.value.shouldSignOut).isFalse()
        viewModel.onEvent(ProfileEvent.UpdateSignOutCondition(true))
        assertThat(viewModel.state.value.shouldSignOut).isTrue()
    }

    @Test
    fun updateAuthUiClient_correctlyUpdatesState() {
        every { userMock.displayName } returns userDisplayName
        every { userMock.email } returns userEmail
        every { userMock.photoUrl } returns userPhotoUrl

        val testAuthUiClient = TestAuthUiClient()
        viewModel.updateAuthClient(authUiClient = testAuthUiClient)
        assertThat(viewModel.state.value.authUiClient).isEqualTo(testAuthUiClient)
        assertThat(viewModel.state.value.userId).isEqualTo(userId)
        assertThat(viewModel.state.value.username).isEqualTo(userDisplayName)
        assertThat(viewModel.state.value.email).isEqualTo(userEmail)
        assertThat(viewModel.state.value.profilePictureUrl).isEqualTo(userPhotoUrl.toString())
        assertThat(viewModel.state.value.paymentsNumber).isEqualTo(userPaymentNumber)
        assertThat(viewModel.state.value.joinDate).isEqualTo(userJoinDate)
    }

    @Test
    fun updateSharedPreferences_listenerUpdatesState() = runBlocking {
        sharedPreferences.edit().apply {
            this.putInt("${userId}PaymentsNum", userPaymentNumber + 1)
        }.apply()
        //Wait for listener to register change
        delay(200)
        assertThat(viewModel.state.value.paymentsNumber).isEqualTo(userPaymentNumber + 1)
    }

    @Test
    fun deleteAccountWithSuccess_updatesSharedPreferencesAndReturnsTrue() = runTest {
        val onCompleteListenerMock = slot<OnCompleteListener<Void>>()
        val onFailureListenerMock = slot<OnFailureListener>()
        every { taskMock.addOnCompleteListener(capture(onCompleteListenerMock)) } answers {
            onCompleteListenerMock.captured.onComplete(taskMock)
            taskMock
        }
        every { taskMock.addOnFailureListener(capture(onFailureListenerMock)) } returns taskMock

        val wasDeleteSuccessful =
            async(viewModelCoroutineContext) {
                viewModel.deleteAccount()
            }.await()

        assertThat(
            sharedPreferences.getString(
                "${FirebaseAuth.getInstance().currentUser?.uid}JoinDate",
                null
            )
        ).isNull()
        assertThat(
            sharedPreferences.getString(
                "${FirebaseAuth.getInstance().currentUser?.uid}PaymentsNum",
                null
            )
        ).isNull()
        assertThat(wasDeleteSuccessful).isTrue()
    }

    @Test
    fun deleteAccountWithFailure_updatesErrorMessageAndReturnsFalse() = runTest {
        val onCompleteListenerMock = slot<OnCompleteListener<Void>>()
        val onFailureListenerMock = slot<OnFailureListener>()
        val errorMessage = "Error message"
        every { taskMock.addOnCompleteListener(capture(onCompleteListenerMock)) } returns taskMock
        every { taskMock.addOnFailureListener(capture(onFailureListenerMock)) } answers {
            onFailureListenerMock.captured.onFailure(Exception(errorMessage))
            taskMock
        }

        val wasDeleteSuccessful =
            async(viewModelCoroutineContext) {
                viewModel.deleteAccount()
            }.await()

        assertThat(wasDeleteSuccessful).isFalse()
        assertThat(viewModel.state.value.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun signOut_updatesSharedPreferences() = runTest {
        launch(viewModelCoroutineContext) { viewModel.signOut() }.join()
        assertThat(sharedPreferences.getString("${userId}AuthMethod", null)).isNull()
    }
}
