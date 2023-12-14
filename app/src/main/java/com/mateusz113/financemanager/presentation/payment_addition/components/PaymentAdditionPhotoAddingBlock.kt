package com.mateusz113.financemanager.presentation.payment_addition.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R

@Composable
fun PaymentAdditionPhotoAddingBlock(
    modifier: Modifier = Modifier,
    uploadedPhotos: List<String>,
    onUploadedPhotoDelete: (String) -> Unit,
    newPhotos: List<Uri>,
    onNewPhotoDelete: (Uri) -> Unit,
    photoCount: Int,
    onPhotoAddClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 5.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 5.dp),
            text = stringResource(id = R.string.photos),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyHorizontalGrid(
            modifier = modifier
                .height(180.dp),
            rows = GridCells.Fixed(1),
        ) {
            uploadedPhotos.forEach { photoUrl ->
                item {
                    PaymentAdditionPhotoContainer(
                        photo = photoUrl,
                        deleteClicked = { photo ->
                            onUploadedPhotoDelete(photo)
                        }
                    )
                }
            }
            newPhotos.forEach { photoUri ->
                item {
                    PaymentAdditionPhotoContainer(
                        photo = photoUri,
                        deleteClicked = { photo ->
                            onNewPhotoDelete(photo)
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
        Spacer(modifier = Modifier.height(20.dp))
    }
}