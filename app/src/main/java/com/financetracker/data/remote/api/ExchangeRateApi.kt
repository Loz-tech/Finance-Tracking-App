package com.financetracker.data.remote.api

import com.financetracker.data.remote.dto.ExchangeRateApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String): Response<ExchangeRateApiResponse>
}
