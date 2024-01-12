package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.NavGraphs
import com.mateusz113.financemanager.presentation.common.dialog.PhotoDisplayDialog
import com.mateusz113.financemanager.presentation.common.wrapper.ScaffoldWrapper
import com.mateusz113.financemanager.presentation.common.components.TopAppBarWithBack
import com.mateusz113.financemanager.presentation.destinations.PaymentListingsScreenDestination
import com.mateusz113.financemanager.presentation.payments.payment_addition.components.PaymentAdditionCategoryPicker
import com.mateusz113.financemanager.presentation.payments.payment_addition.components.PaymentAdditionDataInsertField
import com.mateusz113.financemanager.presentation.payments.payment_addition.components.PaymentAdditionDatePicker
import com.mateusz113.financemanager.presentation.payments.payment_addition.components.PaymentAdditionPhotoAddingBlock
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch
import java.math.BigDecimal
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
                Snackbar(
                    modifier = Modifier
                        .offset(y = (-60).dp),
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    snackbarData = snackbarData
                )
            },
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.title,
                    isSingleLine = true,
                    value = state.title,
                    valueValidator = { title ->
                        isTitleValid(title)
                    },
                    onValueChange = { title ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeTitle(title))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.description,
                    value = state.description,
                    valueValidator = { description ->
                        isDescriptionValid(description)
                    },
                    onValueChange = { description ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeDescription(description))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentAdditionDataInsertField(
                    modifier = Modifier,
                    label = R.string.amount,
                    value = state.amount,
                    valueValidator = { amount ->
                        isAmountValid(amount)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    onValueChange = { amount ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeAmount(amount))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentAdditionCategoryPicker(
                    modifier = Modifier,
                    category = state.category,
                    categoryChange = { category ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeCategory(category))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentAdditionDatePicker(
                    modifier = Modifier,
                    date = state.date,
                    dateText = formattedDate,
                    dateChange = { date ->
                        viewModel.onEvent(PaymentAdditionEvent.ChangeDate(date))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentAdditionPhotoAddingBlock(
                    modifier = Modifier,
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
                                    if (isPhotoQuantityValid(
                                            state.newPhotos.size,
                                            state.uploadedPhotos.size + 1
                                        )
                                    ) {
                                        viewModel.onEvent(
                                            PaymentAdditionEvent.RestoreDeletedPhoto(
                                                photoUrl
                                            )
                                        )
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    message = context.getString(R.string.too_many_pictures),
                                                    duration = SnackbarDuration.Short
                                                )
                                        }
                                    }
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
                                    if (isPhotoQuantityValid(
                                            state.newPhotos.size + 1,
                                            state.uploadedPhotos.size
                                        )
                                    ) {
                                        viewModel.onEvent(
                                            PaymentAdditionEvent.RestoreDeletedPhoto(
                                                photoUri
                                            )
                                        )
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    message = context.getString(R.string.too_many_pictures),
                                                    duration = SnackbarDuration.Short
                                                )
                                        }
                                    }
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
                    },
                    onPhotoClick = { photo ->
                        viewModel.onEvent(PaymentAdditionEvent.UpdateDialogPhoto(photo))
                        viewModel.onEvent(PaymentAdditionEvent.UpdateDialogState(true))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (isTitleValid(state.title) &&
                                isDescriptionValid(state.description) &&
                                isAmountValid(state.amount) &&
                                isPhotoQuantityValid(
                                    state.newPhotos.size,
                                    state.uploadedPhotos.size
                                )
                            ) {
                                viewModel.onEvent(PaymentAdditionEvent.AdditionConfirm)
                                navController.navigate(PaymentListingsScreenDestination) {
                                    popUpTo(NavGraphs.root)
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                val errorMessage = constructErrorMessage(
                                    context = context,
                                    title = state.title,
                                    description = state.description,
                                    amount = state.amount
                                )
                                coroutineScope.launch {
                                    snackbarHostState
                                        .showSnackbar(
                                            message = errorMessage,
                                            duration = SnackbarDuration.Short
                                        )
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
    PhotoDisplayDialog(
        photo = state.dialogPhoto,
        isDialogOpen = state.isPhotoDialogOpen,
        dialogOpen = { isOpen ->
            viewModel.onEvent(PaymentAdditionEvent.UpdateDialogState(isOpen))
        })
}

private fun isTitleValid(
    title: String
): Boolean {
    return title.isNotBlank() && title.length < 50
}

private fun isDescriptionValid(
    description: String
): Boolean {
    return description.isNotBlank() && description.length < 500
}

private fun isAmountValid(
    amount: String
): Boolean {
    return amount.isNotBlank() &&
            amount.split(".").first().length < 8 &&
            try {
                amount.toFloat()
                true
            } catch (e: NumberFormatException) {
                false
            }
}

private fun isPhotoQuantityValid(
    newPhotosAmount: Int,
    uploadedPhotosAmount: Int
): Boolean {
    return newPhotosAmount + uploadedPhotosAmount <= 5
}

private fun constructErrorMessage(
    context: Context,
    title: String,
    description: String,
    amount: String
): String {
    val incorrectValues = mutableListOf<String>()
    if (!isTitleValid(title)) {
        incorrectValues.add("title")
    }
    if (!isDescriptionValid(description)) {
        incorrectValues.add("description")
    }
    if (!isAmountValid(amount)) {
        incorrectValues.add("amount")
    }
    if (incorrectValues.size > 1) {
        return buildString {
            incorrectValues.forEachIndexed { index, value ->
                if (index == 0) {
                    append(value.replaceFirstChar { it.uppercase() })
                } else {
                    append(value)
                }
                if (index != incorrectValues.lastIndex) {
                    append(", ")
                } else {
                    append(" ")
                }
            }
            append(context.getString(R.string.are_incorrect))
        }
    } else {
        return buildString {
            incorrectValues.forEach { value ->
                append("${value.replaceFirstChar { it.uppercase() }} ")
            }
            append(context.getString(R.string.is_incorrect))
        }
    }
}