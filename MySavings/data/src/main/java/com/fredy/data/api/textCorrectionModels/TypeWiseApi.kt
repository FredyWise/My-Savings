package com.fredy.data.api.textCorrectionModels

import com.fredy.core.credentials.ApiCredentials
import com.fredy.data.api.textCorrectionModels.textCorrectionDTO.TextCorrectionRequest
import com.fredy.data.api.textCorrectionModels.textCorrectionDTO.TextCorrectionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface TypeWiseApi {
    @POST(ApiCredentials.TextCorrectionModels.POST_LATEST_CORRECTION)
    fun sendPostRequest(@Body postData: TextCorrectionRequest): Call<TextCorrectionResponse>
}
