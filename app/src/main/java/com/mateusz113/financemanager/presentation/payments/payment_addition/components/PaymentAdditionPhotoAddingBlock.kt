package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.mateusz113.financemanager.presentation.common.components.PhotoContainer

@Composable
fun PaymentAdditionPhotoAddingBlock(
    modifier: Modifier = Modifier,
    uploadedPhotos: List<String>,
    onUploadedPhotoDelete: (String) -> Unit,
    newPhotos: List<Uri>,
    onNewPhotoDelete: (Uri) -> Unit,
    photoCount: Int,
    onPhotoAddClick: () -> Unit,
    onPhotoClick: (Any) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.photos),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .height(150.dp)
        ) {
            uploadedPhotos.forEach { photoUrl ->
                item {
                    PhotoContainer(
                        photo = photoUrl,
                        deleteEnabled = true,
                        onDeleteClick = { photo ->
                            onUploadedPhotoDelete(photo)
                        },
                        onPhotoClick = { photo ->
                            onPhotoClick(photo)
                        }
                    )
                }
            }
            newPhotos.forEach { photoUri ->
                item {
                    PhotoContainer(
                        photo = photoUri,
                        deleteEnabled = true,
                        onDeleteClick = { photo ->
                            onNewPhotoDelete(photo)
                        },
                        onPhotoClick = { photo ->
                            onPhotoClick(photo)
                        }
                    )
                }
            }
            if (photoCount < 5) {
                item {
                    PaymentAdditionAddPhotoContainer(
                        onClick = {
                            onPhotoAddClick()
                        }
                    )
                }
            }
        }
    }
}