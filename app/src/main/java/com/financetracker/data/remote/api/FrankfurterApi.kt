package com.financetracker.data.remote.api

import com.financetracker.data.remote.dto.FrankfurterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApi {
    @GET("v1/latest")
    suspend fun getLatestRates(@Query("from") base: String, @Query("to") targets: String): Response<FrankfurterResponse>
}
