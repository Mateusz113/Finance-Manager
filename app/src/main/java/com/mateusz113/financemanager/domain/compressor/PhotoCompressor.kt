package com.mateusz113.financemanager.domain.compressor

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.quality
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PhotoCompressor {
    companion object {
        /**
         * Compresses the image, outputs it to the file and returns Uri of that file
         * @param context Application context
         * @param initialPhotoUri Uri of the photo that should be compressed
         * @param outputFile File to which the photo should be outputted
         */
        suspend fun compressImage(
            context: Context,
            initialPhotoUri: Uri,
            outputFile: File
        ): Uri? {
            return try {
                val byteArray = getByteArrayFromUri(context, initialPhotoUri)
                writeBiteArrayToFile(byteArray, outputFile)
                Compressor.compress(context, outputFile) {
                    destination(outputFile)
                    quality(50)
                }
                getFileUri(outputFile)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun getByteArrayFromUri(
            context: Context,
            uri: Uri
        ): ByteArray {
            val inputStream = context.contentResolver.openInputStream(uri)
            val buffer = ByteArrayOutputStream()
            inputStream?.use { input ->
                val bufferSize = 4096
                val data = ByteArray(bufferSize)
                var bytesRead: Int
                while (input.read(data, 0, bufferSize).also { bytesRead = it } != -1) {
                    buffer.write(data, 0, bytesRead)
                }
            }
            return buffer.toByteArray()
        }

        private fun writeBiteArrayToFile(
            byteArray: ByteArray,
            file: File
        ) {
            val outputStream = FileOutputStream(file)
            outputStream.write(byteArray)
            outputStream.close()
        }

        private fun getFileUri(
            file: File
        ): Uri {
            return Uri.fromFile(file)
        }
    }
}
