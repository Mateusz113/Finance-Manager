package com.mateusz113.financemanager.presentation.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.mateusz113.financemanager.R

@Composable
fun <T> PhotoDisplayDialog(
    photo: T,
    isDialogOpen: Boolean,
    dialogOpen: (Boolean) -> Unit,
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = {
                dialogOpen(false)
            },
        ) {
            Card(
                modifier = Modifier.sizeIn(
                    maxWidth = 560.dp
                ),
                shape = RoundedCornerShape(size = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                SubcomposeAsyncImage(
                    model = photo,
                    contentDescription = stringResource(id = R.string.photo),
                    loading = {
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                )
            }
        }
    }
}