package com.example.beeranking.data.repository.beer

import com.example.beeranking.data.networking.NetworkClient
import com.example.beeranking.model.Beer
import com.example.beeranking.model.BeerSummary

class BeerRepository private constructor() {
    private val beerApiService = NetworkClient.beerApiService

    suspend fun getBeers(): List<BeerSummary> {
        return beerApiService.getBeers().data
    }

    suspend fun searchBeers(query: String): List<Beer> {
        return beerApiService.searchBeers(query).data
    }

    suspend fun getBeerById(beerId: String): Beer {
        return beerApiService.getBeerById(beerId)
    }

    companion object {
        val shared = BeerRepository()
    }
}
