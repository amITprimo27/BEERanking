package com.example.beeranking.model

import com.google.gson.annotations.SerializedName

data class BeerSearchResponse(
    val `object`: String,
    val url: String,
    val query: String,
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("next_cursor")
    val nextCursor: String?,
    val data: List<Beer>
)
