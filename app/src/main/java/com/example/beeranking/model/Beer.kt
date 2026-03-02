package com.example.beeranking.model

import com.google.gson.annotations.SerializedName

data class Beer(
    val id: String,
    val `object`: String,
    val name: String,
    val style: String,
    val description: String?,
    val abv: Float?,
    val ibu: Int?,
    @SerializedName("cb_verified")
    val cbVerified: Boolean,
    @SerializedName("brewer_verified")
    val brewerVerified: Boolean,
    @SerializedName("last_modified")
    val lastModified: Long,
    val brewer: Brewer?
)
