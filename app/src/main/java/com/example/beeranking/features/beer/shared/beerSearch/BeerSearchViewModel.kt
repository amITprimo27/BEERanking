package com.example.beeranking.features.beer.shared.beerSearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeranking.data.repository.beer.BeerRepository
import com.example.beeranking.model.Beer
import kotlinx.coroutines.launch

class BeerSearchViewModel : ViewModel() {

    private val beerRepository = BeerRepository.shared

    private val _searchResults = MutableLiveData<List<Beer>>()
    val searchResults: LiveData<List<Beer>> = _searchResults

    private val _selectedBeer = MutableLiveData<Beer?>()
    val selectedBeer: LiveData<Beer?> = _selectedBeer

    fun searchBeers(query: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = beerRepository.searchBeers(query)
            } catch (e: Exception) {
                Log.e("BeerSearchViewModel", "Error searching beers", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun selectBeer(beer: Beer) {
        _selectedBeer.value = beer
    }

    fun beerSelectedComplete() {
        _selectedBeer.value = null
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }
}
