package com.fredy.addrecord.data


import com.fredy.addrecord.data.mappers.convertToDataRecords
import com.fredy.addrecord.data.tabScannerModel.TabScannerAPI
import com.fredy.addrecord.data.tabScannerModel.tabScannerDTO.ProcessResponse
import com.fredy.addrecord.data.tabScannerModel.tabScannerDTO.ResultResponse
import com.fredy.addrecord.domain.TabScannerRepository
import com.fredy.data.mappers.toDomainRecords
import com.fredy.domain.model.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

class TabScannerRepositoryImpl @Inject constructor(
    private val tabScannerAPI: TabScannerAPI,
) : TabScannerRepository {
    override suspend fun processReceipt(
        image: MultipartBody.Part, delay: Long
    ): List<Record>? {
        return withContext(Dispatchers.IO) {
            sendReceipt(image)?.let { processResponse ->
                val token = processResponse.token
                delay(delay)
                Timber.i(
                    "ProcessImage: finish",
                )
                token?.let {
                    getProcessResult(it)?.convertToDataRecords()?.toDomainRecords()

                }
            }
        }
    }

    private suspend fun sendReceipt(image: MultipartBody.Part): ProcessResponse? {
        return withContext(Dispatchers.IO) {
            tabScannerAPI.processReceipt(
                image
            ).execute().body()
        }

    }

    private suspend fun getProcessResult(token: String): ResultResponse? {
        return withContext(Dispatchers.IO) {
            tabScannerAPI.getResult(
                token
            ).execute().body()
        }

    }

    override suspend fun getCredit(): Int? {
        return withContext(Dispatchers.IO) {
            tabScannerAPI.getCredit().execute().body()
        }
    }
}