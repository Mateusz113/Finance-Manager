package com.mateusz113.financemanager.presentation.sign_in.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.enumeration.AuthMethod

@Composable
fun ExternalSignInButton(
    authMethod: AuthMethod,
    modifier: Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (authMethod) {
                AuthMethod.FACEBOOK -> {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_logo),
                        modifier = Modifier.size(40.dp),
                        contentDescription = stringResource(
                            id = R.string.facebook_sign_in
                        )
                    )

                    Text(text = stringResource(id = R.string.facebook_sign_in))
                }

                AuthMethod.GOOGLE -> {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        modifier = Modifier.size(40.dp),
                        contentDescription = stringResource(
                            id = R.string.google_sign_in
                        )
                    )

                    Text(text = stringResource(id = R.string.google_sign_in))
                }

                AuthMethod.GITHUB -> {
                    Image(
                        painter = painterResource(id = R.drawable.github_logo),
                        modifier = Modifier.size(40.dp),
                        contentDescription = stringResource(
                            id = R.string.github_sign_in
                        )
                    )

                    Text(text = stringResource(id = R.string.github_sign_in))
                }

                else -> {
                    Log.d("Login_UI", stringResource(id = R.string.login_ui_provider_error))
                }
            }
        }
    }
}
