package com.mateusz113.financemanager.presentation.payment_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.PhotoContainer

@Composable
fun PaymentDetailsPhotosRow(
    modifier: Modifier = Modifier,
    photos: List<String>,
    onPhotoClick: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.photos),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .height(150.dp)
        ) {
            photos.forEach { photo ->
                item {
                    PhotoContainer(
                        photo = photo,
                        onPhotoClick = { clickedPhoto ->
                            onPhotoClick(clickedPhoto)
                        }
                    )
                }
            }
        }
    }
}