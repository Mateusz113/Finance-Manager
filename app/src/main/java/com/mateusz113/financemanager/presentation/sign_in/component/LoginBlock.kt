package com.mateusz113.financemanager.presentation.sign_in.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.presentation.common.components.InputTextField

@Composable
fun LoginBlock(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            isError = !isEmailValid(email) && email.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(10.dp))

        InputTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            isPassword = true,
            isError = !isPasswordValid(password) && password.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            when (true) {
                !isEmailValid(email) -> {
                    Toast.makeText(
                        context,
                        "Email is blank or incorrectly formatted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !isPasswordValid(
                    password
                ) -> {
                    Toast.makeText(
                        context,
                        "Password is too short. It must be at least 6 characters long.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                true -> {
                    onLoginClick(email, password)
                }

                else -> {
                    Toast.makeText(
                        context,
                        "Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }) {
            Text(text = "Login")
        }
    }
}

private fun isEmailValid(email: String): Boolean {
    if (email.isEmpty()){
        return false
    }
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return email.matches(emailRegex)
}

private fun isPasswordValid(password: String): Boolean {
    return password.length >= 6
}
