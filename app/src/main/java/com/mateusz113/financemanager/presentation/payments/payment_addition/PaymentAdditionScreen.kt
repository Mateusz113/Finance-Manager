package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.compressor.PhotoCompressor
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.validator.PaymentInfoValidator
import com.mateusz113.financemanager.domain.validator.PaymentPhotoSizeValidator
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.common.components.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.common.dialog.PhotoDisplayDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.payments.payment_addition.components.PaymentAdditionBlock
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

@RootNavGraph
@Destination
@Composable
fun PaymentAdditionScreen(
    viewModel: PaymentAdditionViewModel = hiltViewModel(),
    navController: NavController,
    @StringRes topBarLabel: Int,
    paymentId: String? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.error == null) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val photoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    if (PaymentPhotoSizeValidator.validatePhotoSize(context, it)) {
                        coroutineScope.launch {
                            val outputFile = File(
                                context.cacheDir, "compressed_photo_${LocalDateTime.now()}.jpg"
                            )
                            val compressedImageUri = PhotoCompressor.compressImage(
                                context = context,
                                initialPhotoUri = it,
                                outputFile = outputFile
                            )
                            compressedImageUri?.let {
                                viewModel.onEvent(
                                    PaymentAdditionEvent.AddNewPhoto(
                                        compressedImageUri
                                    )
                                )
                            } ?: Toast.makeText(
                                context,
                                context.getString(R.string.photo_compression_failed),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.photo_too_big),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        PaymentAdditionScreenContent(
            state = state,
            topBarLabel = topBarLabel,
            snackbarHostState = snackbarHostState,
            navController = navController,
            onTitleChange = { viewModel.onEvent(PaymentAdditionEvent.ChangeTitle(it)) },
            onDescriptionChange = { viewModel.onEvent(PaymentAdditionEvent.ChangeDescription(it)) },
            onAmountChange = { viewModel.onEvent(PaymentAdditionEvent.ChangeAmount(it)) },
            onCategoryChange = { viewModel.onEvent(PaymentAdditionEvent.ChangeCategory(it)) },
            onDateChange = { viewModel.onEvent(PaymentAdditionEvent.ChangeDate(it)) },
            onUploadedPhotoDelete = { photoUrl ->
                handlePhotoDelete(
                    context = context,
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    newPhotosSize = state.newPhotos.size,
                    uploadedPhotosSize = state.uploadedPhotos.size,
                    deleteImplementation = {
                        viewModel.onEvent(PaymentAdditionEvent.RemoveUploadedPhoto(photoUrl))
                    },
                    retrieveImplementation = {
                        viewModel.onEvent(PaymentAdditionEvent.RestoreDeletedPhoto(photoUrl))
                    }
                )
            },
            onNewPhotoDelete = { photoUri ->
                handlePhotoDelete(
                    context = context,
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                    newPhotosSize = state.newPhotos.size,
                    uploadedPhotosSize = state.uploadedPhotos.size,
                    deleteImplementation = {
                        viewModel.onEvent(PaymentAdditionEvent.RemovePhoto(photoUri))
                    },
                    retrieveImplementation = {
                        viewModel.onEvent(PaymentAdditionEvent.RestoreDeletedPhoto(photoUri))
                    }
                )
            },
            onPhotoAddClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onPhotoClick = { photo ->
                viewModel.onEvent(PaymentAdditionEvent.UpdateDialogPhoto(photo))
                viewModel.onEvent(PaymentAdditionEvent.UpdateDialogState(true))
            },
            onConfirmClick = {
                handleConfirmationClick(
                    context = context,
                    title = state.title,
                    description = state.description,
                    amount = state.amount,
                    onCorrectValuesAction = {
                        viewModel.onEvent(PaymentAdditionEvent.AdditionConfirm)
                        navController.navigate(PaymentListingsScreenDestination) {
                            popUpTo(NavGraphs.root)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            },
            onPhotoDialogDismiss = {
                viewModel.onEvent(PaymentAdditionEvent.UpdateDialogState(false))
            }
        )
    }
}

@Composable
fun PaymentAdditionScreenContent(
    state: PaymentAdditionState,
    @StringRes topBarLabel: Int,
    snackbarHostState: SnackbarHostState,
    navController: NavController = NavController(LocalContext.current),
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onUploadedPhotoDelete: (String) -> Unit,
    onNewPhotoDelete: (Uri) -> Unit,
    onPhotoAddClick: () -> Unit,
    onPhotoClick: (Any) -> Unit,
    onConfirmClick: () -> Unit,
    onPhotoDialogDismiss: () -> Unit
) {
    ScaffoldWrapper(
        topAppBar = {
            TopAppBarWithBack(
                label = topBarLabel,
                navController = navController
            )
        },
        snackbarContent = { snackbarData ->
            Snackbar(
                modifier = Modifier
                    .offset(y = (-80).dp),
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                snackbarData = snackbarData
            )
        },
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        PaymentAdditionBlock(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            title = state.title,
            onTitleChange = onTitleChange,
            description = state.description,
            onDescriptionChange = onDescriptionChange,
            amount = state.amount,
            onAmountChange = onAmountChange,
            category = state.category,
            onCategoryChange = onCategoryChange,
            date = state.date,
            onDateChange = onDateChange,
            uploadedPhotos = state.uploadedPhotos,
            onUploadedPhotoDelete = onUploadedPhotoDelete,
            newPhotos = state.newPhotos,
            onNewPhotoDelete = onNewPhotoDelete,
            onPhotoAddClick = onPhotoAddClick,
            onPhotoClick = onPhotoClick,
            onConfirmClick = onConfirmClick
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        state.error?.let { errorMessage ->
            Text(
                text = errorMessage,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
    PhotoDisplayDialog(
        photo = state.dialogPhoto,
        isDialogOpen = state.isPhotoDialogOpen,
        onDismiss = onPhotoDialogDismiss
    )
}


private fun handleConfirmationClick(
    context: Context,
    title: String,
    description: String,
    amount: String,
    onCorrectValuesAction: () -> Unit
) {
    when (true) {
        !PaymentInfoValidator.titleValidator(title) -> {
            Toast.makeText(
                context,
                context.getString(R.string.payment_title_error),
                Toast.LENGTH_SHORT
            ).show()
        }

        !PaymentInfoValidator.descriptionValidator(description) -> {
            Toast.makeText(
                context,
                context.getString(R.string.payment_description_error),
                Toast.LENGTH_SHORT
            ).show()
        }

        !PaymentInfoValidator.amountValidator(amount) -> {
            Toast.makeText(
                context,
                context.getString(R.string.payment_amount_error),
                Toast.LENGTH_SHORT
            ).show()
        }

        true -> {
            onCorrectValuesAction()
        }

        else -> {
            Toast.makeText(
                context,
                context.getString(R.string.generic_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

private fun handlePhotoDelete(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    newPhotosSize: Int,
    uploadedPhotosSize: Int,
    deleteImplementation: () -> Unit,
    retrieveImplementation: () -> Unit
) {
    deleteImplementation()
    coroutineScope.launch {
        val result = snackbarHostState
            .showSnackbar(
                message = context.getString(R.string.deleted_picture),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                //The +1 check is taking care if the photo can be retrieved from deletion without breaking the max 5 photos limit
                if (PaymentInfoValidator.photoQuantityValidator(
                        newPhotosSize + 1,
                        uploadedPhotosSize
                    )
                ) {
                    retrieveImplementation()
                } else {
                    snackbarHostState
                        .showSnackbar(
                            message = context.getString(R.string.too_many_pictures),
                            duration = SnackbarDuration.Short
                        )
                }
            }

            SnackbarResult.Dismissed -> {}
        }
    }
}
