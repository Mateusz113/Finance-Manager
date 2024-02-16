package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.presentation.common.components.InputTextField
import com.mateusz113.financemanager.util.PaymentInfoValidator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PaymentAdditionBlock(
    modifier: Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    category: Category,
    onCategoryChange: (Category) -> Unit,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    uploadedPhotos: List<String>,
    onUploadedPhotoDelete: (String) -> Unit,
    newPhotos: List<Uri>,
    onNewPhotoDelete: (Uri) -> Unit,
    onPhotoAddClick: () -> Unit,
    onPhotoClick: (Any) -> Unit,
    onConfirmClick: () -> Unit
) {
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(date)
        }
    }

    val headlinesTextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        color = MaterialTheme.colorScheme.onBackground
    )

    val textFieldModifier = Modifier
        .fillMaxWidth()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        InputTextField(
            modifier = textFieldModifier,
            value = title,
            onValueChange = onTitleChange,
            label = {
                Text(text = stringResource(id = R.string.title))
            },
            isError = !PaymentInfoValidator.titleValidator(title) && title.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
        )
        Spacer(modifier = Modifier.height(16.dp))

        InputTextField(
            modifier = textFieldModifier,
            value = description,
            onValueChange = onDescriptionChange,
            isSingleLine = false,
            label = {
                Text(text = stringResource(id = R.string.description))
            },
            isError = !PaymentInfoValidator.descriptionValidator(description) && description.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))

        InputTextField(
            modifier = textFieldModifier,
            value = amount,
            onValueChange = onAmountChange,
            label = {
                Text(text = stringResource(id = R.string.amount))
            },
            isError = !PaymentInfoValidator.amountValidator(amount) && amount.isNotEmpty(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.category),
            style = headlinesTextStyle
        )
        Spacer(modifier = Modifier.height(8.dp))

        PaymentAdditionCategoryPicker(
            category = category,
            categoryChange = onCategoryChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.date),
            style = headlinesTextStyle
        )
        Spacer(modifier = Modifier.height(8.dp))

        PaymentAdditionDatePicker(
            date = date,
            dateText = formattedDate,
            dateChange = onDateChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.photos),
            style = headlinesTextStyle
        )
        Spacer(modifier = Modifier.height(8.dp))

        PaymentAdditionPhotoAddingBlock(
            modifier = Modifier.fillMaxWidth(),
            uploadedPhotos = uploadedPhotos,
            onUploadedPhotoDelete = onUploadedPhotoDelete,
            newPhotos = newPhotos,
            onNewPhotoDelete = onNewPhotoDelete,
            onPhotoAddClick = onPhotoAddClick,
            onPhotoClick = onPhotoClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = onConfirmClick
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
