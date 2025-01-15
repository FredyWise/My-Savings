package com.fredy.addrecord.domain

import com.fredy.domain.model.Record

import okhttp3.MultipartBody

interface TabScannerRepository {
    suspend fun processReceipt(image: MultipartBody.Part, delay: Long): List<Record>?

    suspend fun getCredit(): Int?
}