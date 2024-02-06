package com.mateusz113.financemanager.presentation.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.mateusz113.financemanager.R

@Composable
fun ProfileInfo(
    modifier: Modifier = Modifier,
    profilePictureUrl: String?,
    username: String?,
    email: String?,
    joinDate: String,
    paymentsNumber: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        profilePictureUrl?.let {
            SubcomposeAsyncImage(
                model = it,
                contentDescription = "Profile picture",
                loading = {
                    CircularProgressIndicator()
                },
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        username?.let {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                lineHeight = MaterialTheme.typography.displaySmall.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (!email.isNullOrEmpty()) {
            Text(
                text = stringResource(id = R.string.email, email),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MaterialTheme.typography.displaySmall.fontSize,
                maxLines = 2,
                fontWeight = FontWeight.Normal
            )
        }

        Text(
            text = stringResource(id = R.string.join_date, joinDate),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            lineHeight = MaterialTheme.typography.displaySmall.fontSize,
            fontWeight = FontWeight.Normal
        )

        Text(
            text = stringResource(id = R.string.payments_number, paymentsNumber),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            lineHeight = MaterialTheme.typography.displaySmall.fontSize,
            fontWeight = FontWeight.Normal
        )
    }
}