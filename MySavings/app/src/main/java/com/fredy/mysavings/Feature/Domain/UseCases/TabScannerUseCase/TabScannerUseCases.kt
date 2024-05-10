package com.fredy.mysavings.Feature.Domain.UseCases.TabScannerUseCase

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response.ResultResponse
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.TabScannerRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.convertToRecords
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class TabScannerUseCases(
    val processImage: ProcessImage,
    val upsertRecords: UpsertRecords,
)

class ProcessImage(
    private val context: Context,
    private val tabScannerRepository: TabScannerRepository,
) {
    suspend operator fun invoke(
        imageUri: Uri
    ):List<Record>? {
        return withContext(Dispatchers.IO) {
            Log.i(
                "processImage: start",
            )
            val imagePart = createImagePart(
                context, imageUri
            )
            Log.i(
                "processImage: start2",
            )
            val processResponse = tabScannerRepository.processReceipt(
                imagePart
            )
            processResponse?.let {
                val token = processResponse.token
                delay(5000)
                Log.i(
                    "processImage: finish",
                )
                token?.let {
                    getResult(it)?.convertToRecords()
                }
            }
        }

    }

    private suspend fun getResult(token: String): ResultResponse? {
        return tabScannerRepository.getProcessResult(
            token
        )
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


class UpsertRecords(
    val userRepository: UserRepository,
    val recordRepository: RecordRepository,
){
    suspend operator fun invoke(
        records: List<Record>
    ) {
        val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId
        recordRepository.upsertAllRecordItems(records.map { it.copy(userIdFk = currentUserId) })
    }

}