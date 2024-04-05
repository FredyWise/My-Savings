package com.fredy.mysavings.Data.APIs.TextCorrectionModels

import com.fredy.mysavings.Feature.Data.APIs.ApiCredentials
import com.fredy.mysavings.Feature.Data.APIs.TextCorrectionModels.Request.TextCorrectionRequest
import com.fredy.mysavings.Feature.Data.APIs.TextCorrectionModels.Response.TextCorrectionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface TypeWiseApi {
    @POST(ApiCredentials.TextCorrectionModels.POST_LATEST_CORRECTION)
    fun sendPostRequest(@Body postData: TextCorrectionRequest): Call<TextCorrectionResponse>
}
