package com.fredy.addrecord.domain.usecases.bulkAdd

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.fredy.domain.model.Record
import com.fredy.addrecord.domain.TabScannerRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ProcessImage(
    private val context: Context,
    private val tabScannerRepository: TabScannerRepository,
) {
    suspend operator fun invoke(
        imageUri: Uri, delay: Long = 3000
    ): List<Record>? {
        return withContext(Dispatchers.IO) {
            Timber.i(
                "ProcessImage: startCreatingImagePart",
            )
            val imagePart = createImagePart(
                context, imageUri
            )
            Timber.i(
                "ProcessImage: startProcessingReceipt",
            )
            tabScannerRepository.processReceipt(
                imagePart,delay
            )
        }

    }

    private suspend fun createImagePart(
        context: Context, imageUri: Uri
    ): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                imageUri, "r"
            )
            val file = File(
                context.cacheDir,
                context.contentResolver.getFileName(
                    imageUri
                )
            )
            parcelFileDescriptor?.let {
                val inputStream = FileInputStream(
                    it.fileDescriptor
                )
                val outputStream = FileOutputStream(
                    file
                )
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(
                        buffer, 0, length
                    )
                }
                inputStream.close()
                outputStream.close()
            }


            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            MultipartBody.Part.createFormData(
                "file", file.name, requestFile
            )
        }
    }


    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val returnCursor = this.query(
            uri, null, null, null, null
        )
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(
                OpenableColumns.DISPLAY_NAME
            )
            returnCursor.moveToFirst()
            name = returnCursor.getString(
                nameIndex
            )
            returnCursor.close()
        }
        return name
    }

}