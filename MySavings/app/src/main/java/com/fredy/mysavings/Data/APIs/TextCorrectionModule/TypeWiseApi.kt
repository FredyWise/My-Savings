package com.fredy.mytest.APIs.TextCorrectionModule

import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mytest.APIs.TextCorrectionModule.Request.TextCorrectionRequest
import com.fredy.mytest.APIs.TextCorrectionModule.Response.TextCorrectionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface TypeWiseApi {
    @POST(ApiCredentials.TextCorrectionModule.POST_LATEST_CORRECTION)
    fun sendPostRequest(@Body postData: TextCorrectionRequest): Call<TextCorrectionResponse>
}
