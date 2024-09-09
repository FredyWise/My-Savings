package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.data.api.tabScannerModel.tabScannerDTO.ProcessResponse
import com.fredy.data.api.tabScannerModel.tabScannerDTO.ResultResponse
import okhttp3.MultipartBody

interface TabScannerRepository {
    suspend fun processReceipt(image: MultipartBody.Part): ProcessResponse?
    suspend fun getProcessResult(token: String): ResultResponse?
    suspend fun getCredit(): Int?
}