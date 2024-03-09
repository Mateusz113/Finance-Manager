package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.presentation.common.components.PhotoContainer
import com.mateusz113.financemanager.util.TestTags

@Composable
fun PaymentAdditionPhotoAddingBlock(
    modifier: Modifier = Modifier,
    uploadedPhotos: List<String>,
    onUploadedPhotoDelete: (String) -> Unit,
    newPhotos: List<Uri>,
    onNewPhotoDelete: (Uri) -> Unit,
    onPhotoAddClick: () -> Unit,
    onPhotoClick: (Any) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier
                .height(150.dp)
        ) {
            uploadedPhotos.forEach { photoUrl ->
                item {
                    PhotoContainer(
                        modifier = Modifier.testTag(TestTags.PHOTO),
                        photo = photoUrl,
                        isDeleteEnabled = true,
                        onDeleteClick = { photo ->
                            onUploadedPhotoDelete(photo)
                        },
                        onPhotoClick = { photo ->
                            onPhotoClick(photo)
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            newPhotos.forEach { photoUri ->
                item {
                    PhotoContainer(
                        modifier = Modifier.testTag(TestTags.PHOTO),
                        photo = photoUri,
                        isDeleteEnabled = true,
                        onDeleteClick = { photo ->
                            onNewPhotoDelete(photo)
                        },
                        onPhotoClick = { photo ->
                            onPhotoClick(photo)
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            if (uploadedPhotos.size + newPhotos.size < 5) {
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
