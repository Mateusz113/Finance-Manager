package com.mateusz113.financemanager.presentation.register.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.components.InputTextField
import com.mateusz113.financemanager.domain.validator.AccountInfoValidator

@Composable
fun RegisterBlock(
    modifier: Modifier,
    onRegisterClick: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val buttonTextStyle = TextStyle(
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        fontWeight = FontWeight.Medium
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputTextField(
            modifier = Modifier.fillMaxWidth(),
            value = displayName,
            onValueChange = {
                displayName = it
            },
            label = {
                Text(text = stringResource(id = R.string.username))
            },
            isError = !AccountInfoValidator.displayNameValidator(displayName) && displayName.isNotEmpty(),
            isPassword = false,
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = stringResource(id = R.string.email))
            },
            isError = !AccountInfoValidator.emailValidator(email) && email.isNotEmpty(),
            isPassword = false
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            isError = !AccountInfoValidator.passwordValidator(password) && password.isNotEmpty(),
            isPassword = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                when (true) {
                    !AccountInfoValidator.displayNameValidator(displayName) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.display_name_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !AccountInfoValidator.emailValidator(email) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.email_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !AccountInfoValidator.passwordValidator(password) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.password_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true -> {
                        onRegisterClick(displayName, email, password)
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
        ) {
            Text(
                text = stringResource(id = R.string.register),
                style = buttonTextStyle
            )
        }
    }
}
