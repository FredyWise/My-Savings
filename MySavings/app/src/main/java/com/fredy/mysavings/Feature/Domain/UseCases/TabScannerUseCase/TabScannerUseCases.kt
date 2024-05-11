package com.fredy.mysavings.Feature.Domain.UseCases.TabScannerUseCase

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response.ResultResponse
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Repository.TabScannerRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.convertToRecords
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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
    ): List<Record>? {
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
) {
    operator fun invoke(
        state: AddRecordState,
    ): Flow<Resource<AddRecordState>> {
        return flow {
            Log.i("UpsertRecords: start")
            emit(Resource.Loading())
            val currentUserId = userRepository.getCurrentUser()!!.firebaseUserId

            if (state.records.isNullOrEmpty()) {
                throw Exception("Records is Empty")
            }
            val records = state.records.map {
                val recordId = state.recordId
                val walletIdFromFk = state.walletIdFromFk
                val walletIdToFk = walletIdFromFk
                val categoryExpenseIdToFk = state.categoryIdFk
                val categoryIncomeIdToFk = state.categoryIncomeIdFk
                var categoryIdFk = categoryExpenseIdToFk
                val bookIdFk = state.bookIdFk
                val recordDateTime = state.recordDate.atTime(
                    state.recordTime.withNano(
                        (state.recordTime.nano.div(1000000)).times(1000000)
                    )
                )
                var calculationResult = it.recordAmount
                val recordCurrency = state.recordCurrency
                val recordType = it.recordType

                if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || walletIdFromFk == null || walletIdToFk == null || categoryExpenseIdToFk == null || categoryIncomeIdToFk == null) {
                    Log.e(
                        "UpsertRecords.Error: Please fill all required information"
                    )
                    throw Exception("Please fill all required information")
                } else {
                    when (recordType) {
                        RecordType.Income -> {
                            Log.i("UpsertRecords: income: $it")
                            state.fromWallet.walletAmount += calculationResult
                            categoryIdFk = categoryIncomeIdToFk
                        }

                        RecordType.Expense -> {
                            Log.i("UpsertRecords: expense: $it")
                            if (state.fromWallet.walletAmount < calculationResult) {
                                Log.e(
                                    "UpsertRecords.Error: Account balance is not enough"
                                )
                                throw Exception("Account balance is not enough")
                            }
                            state.fromWallet.walletAmount -= calculationResult
                            calculationResult = -calculationResult
                            categoryIdFk = categoryExpenseIdToFk
                        }

                        RecordType.Transfer -> {}
                    }

                    it.copy(
                        recordId = recordId,
                        walletIdFromFk = walletIdFromFk,
                        walletIdToFk = walletIdToFk,
                        categoryIdFk = categoryIdFk!!,
                        bookIdFk = bookIdFk,
                        recordTimestamp = TimestampConverter.fromDateTime(recordDateTime),
                        recordAmount = calculationResult,
                        recordCurrency = recordCurrency,
                        recordType = recordType,
                        recordNotes = it.recordNotes,
                    )
                }
            }

            recordRepository.upsertAllRecordItems(records.map { it.copy(userIdFk = currentUserId) })
            Log.i("UpsertRecords: finish")
            emit(Resource.Success(state))
        }.catch { e ->
            Log.e(
                "UpsertRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}