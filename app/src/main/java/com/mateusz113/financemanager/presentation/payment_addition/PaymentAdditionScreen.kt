package com.mateusz113.financemanager.presentation.payment_addition

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.common.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.common.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.payment_addition.components.PaymentAdditionCategoryPicker
import com.mateusz113.financemanager.presentation.payment_addition.components.PaymentAdditionDataInsertField
import com.mateusz113.financemanager.presentation.payment_addition.components.PaymentAdditionDatePicker
import com.mateusz113.financemanager.presentation.payment_addition.components.PaymentAdditionPhotoAddingBlock
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

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
        val snackbarHostState = remember { SnackbarHostState() }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val photoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    viewModel.onEvent(PaymentAdditionEvent.AddNewPhoto(uri))
                }
            }
        )
        val formattedDate by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(state.date)
            }
        }
        ScaffoldWrapper(
            topAppBar = {
                TopAppBarWithBack(
                    label = topBarLabel,
                    navController = navController
                )
            },
            snackbarContent = { snackbarData ->
                Snackbar(snackbarData = snackbarData)
            },
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.title,
                    isSingleLine = true,
                    value = state.title,
                    valueValidator = { title ->
                        title.isNotBlank() && title.length < 50
                    },
                    onValueChange = { title ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeTitle(title))
                    }
                )

                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.description,
                    value = state.description,
                    valueValidator = { description ->
                        description.isNotBlank() && description.length < 300
                    },
                    onValueChange = { description ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeDescription(description))
                    }
                )

                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.amount,
                    value = state.amount,
                    valueValidator = { amount ->
                        try {
                            amount.toFloat()
                            true
                        } catch (e: NumberFormatException) {
                            false
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    onValueChange = { amount ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeAmount(amount))
                    }
                )

                PaymentAdditionCategoryPicker(
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    category = state.category,
                    categoryChange = { category ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeCategory(category))
                    }
                )

                PaymentAdditionDatePicker(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    date = state.date,
                    dateText = formattedDate,
                    dateChange = { date ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeDate(date))
                    }
                )

                PaymentAdditionPhotoAddingBlock(
                    uploadedPhotos = state.uploadedPhotos,
                    onUploadedPhotoDelete = { photoUrl ->
                        viewModel.onEvent(PaymentAdditionEvent.RemoveUploadedPhoto(photoUrl))
                        coroutineScope.launch {
                            val result = snackbarHostState
                                .showSnackbar(
                                    message = context.getString(R.string.deleted_picture),
                                    actionLabel = context.getString(R.string.undo),
                                    duration = SnackbarDuration.Short
                                )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    viewModel.onEvent(
                                        PaymentAdditionEvent.RestoreDeletedPhoto(
                                            photoUrl
                                        )
                                    )
                                }

                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    },
                    newPhotos = state.newPhotos,
                    onNewPhotoDelete = { photoUri ->
                        viewModel.onEvent(PaymentAdditionEvent.RemovePhoto(photoUri))
                        coroutineScope.launch {
                            val result = snackbarHostState
                                .showSnackbar(
                                    message = context.getString(R.string.deleted_picture),
                                    actionLabel = context.getString(R.string.undo),
                                    duration = SnackbarDuration.Short
                                )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    viewModel.onEvent(
                                        PaymentAdditionEvent.RestoreDeletedPhoto(
                                            photoUri
                                        )
                                    )
                                }

                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    },
                    photoCount = state.newPhotos.size + state.uploadedPhotos.size,
                    onPhotoAddClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (isInputValid(state)) {
                                viewModel.onEvent(PaymentAdditionEvent.AdditionConfirm)
                                navController.navigate(PaymentListingsScreenDestination) {
                                    popUpTo(NavGraphs.root)
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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
}

private fun isInputValid(
    state: PaymentAdditionState
): Boolean {
    val isTitleValid = state.title.isNotBlank() && state.title.length < 50
    val isDescriptionValid = state.description.isNotBlank() && state.description.length < 300
    val isAmountValid = state.amount.isNotBlank() &&
            try {
                state.amount.toFloat()
                true
            } catch (e: NumberFormatException) {
                false
            }
    val arePhotosValid = state.newPhotos.size + state.uploadedPhotos.size < 6
    return isTitleValid && isDescriptionValid && isAmountValid && arePhotosValid
}