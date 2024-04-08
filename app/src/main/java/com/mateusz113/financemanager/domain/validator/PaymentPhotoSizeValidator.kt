package com.mateusz113.financemanager.domain.validator

import android.content.Context
import android.net.Uri
import java.io.File

class PaymentPhotoSizeValidator {
    companion object {
        //Maximum upload size in 4 MB
        private const val MAXIMUM_FILE_SIZE_IN_BYTES: Long = 4_194_304
        fun validatePhotoSize(
            context: Context,
            uri: Uri
        ): Boolean {
            return try {
                val fileSize = getPhotoSize(context, uri)
                fileSize != null && fileSize < MAXIMUM_FILE_SIZE_IN_BYTES
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        private fun getPhotoSize(
            context: Context,
            uri: Uri
        ): Long? {
            return try {
                uri.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.length()
                    } else {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val fileSize = inputStream?.available()?.toLong()
                        inputStream?.close()
                        fileSize
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
