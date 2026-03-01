package com.example.beeranking.data.services

import com.example.beeranking.model.BeerResponse
import com.example.beeranking.model.BeerSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BeerApiService {
    @GET("beer")
    suspend fun getBeers(): BeerResponse

    @GET("beer/search")
    suspend fun searchBeers(@Query("q") query: String): BeerSearchResponse
}
