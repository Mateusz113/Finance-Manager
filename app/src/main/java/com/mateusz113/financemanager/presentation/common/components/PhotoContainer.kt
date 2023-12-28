package com.mateusz113.financemanager.presentation.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.SubcomposeAsyncImage
import com.mateusz113.financemanager.R

@Composable
fun <T> PhotoContainer(
    modifier: Modifier = Modifier,
    photo: T,
    deleteEnabled: Boolean = false,
    onDeleteClick: (T) -> Unit = {},
    onPhotoClick: (T) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (image, deleteButton) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 5.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(5.dp)
                )
                .constrainAs(image) {
                    centerTo(parent)
                },
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = photo,
                contentDescription = stringResource(id = R.string.payment_picture),
                loading = {
                    Box(
                        modifier = Modifier.width(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        onPhotoClick(photo)
                    }
            )
        }
        if (deleteEnabled) {
            IconButton(
                modifier = Modifier
                    .constrainAs(deleteButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .offset(y = (-10).dp, x = 5.dp),
                onClick = {
                    onDeleteClick(photo)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.add_new_photo),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}