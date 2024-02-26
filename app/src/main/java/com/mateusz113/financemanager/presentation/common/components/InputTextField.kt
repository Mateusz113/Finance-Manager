package com.mateusz113.financemanager.presentation.common.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit = {},
    isError: Boolean = false,
    isPassword: Boolean = false,
    isSingleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    //Determine the transformation
    val transformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        visualTransformation = transformation,
        singleLine = isSingleLine,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(5.dp)
    )
}
