package com.example.beeranking.model

import com.google.gson.annotations.SerializedName

data class BeerSummary(
    val id: String,
    val name: String,
    @SerializedName("last_modified")
    val lastModified: Long
)
