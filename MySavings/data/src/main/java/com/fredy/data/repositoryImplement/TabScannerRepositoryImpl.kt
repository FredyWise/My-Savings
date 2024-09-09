package com.fredy.data.repositoryImplement

import com.fredy.data.api.tabScannerModel.tabScannerDTO.ProcessResponse
import com.fredy.data.api.tabScannerModel.tabScannerDTO.ResultResponse
import com.fredy.data.api.tabScannerModel.TabScannerAPI
import com.fredy.domain.repository.TabScannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class TabScannerRepositoryImpl @Inject constructor(
    private val tabScannerAPI: TabScannerAPI,
) : TabScannerRepository {
    override suspend fun processReceipt(
        image: MultipartBody.Part
    ): ProcessResponse? {
        return withContext(Dispatchers.IO) {
            tabScannerAPI.processReceipt(
                image
            ).execute().body()
        }
    }

    override suspend fun getProcessResult(token: String): ResultResponse? {
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