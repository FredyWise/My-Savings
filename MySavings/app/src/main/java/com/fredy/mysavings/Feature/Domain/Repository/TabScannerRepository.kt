package com.fredy.mysavings.Feature.Domain.Repository

import com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response.ProcessResponse
import com.fredy.mysavings.Feature.Data.APIs.TabScannerModel.Response.ResultResponse
import okhttp3.MultipartBody
import retrofit2.Response

interface TabScannerRepository {
    suspend fun processReceipt(image: MultipartBody.Part): ProcessResponse?
    suspend fun getProcessResult(token: String): ResultResponse?
    suspend fun getCredit(): Int?
}