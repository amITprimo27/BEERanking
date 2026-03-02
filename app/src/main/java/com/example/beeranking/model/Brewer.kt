package com.example.beeranking.model

import com.google.gson.annotations.SerializedName

data class Brewer(
    val id: String,
    val `object`: String?,
    val name: String,
    val description: String?,
    @SerializedName("short_description")
    val shortDescription: String?,
    val url: String?,
    @SerializedName("cb_verified")
    val cbVerified: Boolean?,
    @SerializedName("brewer_verified")
    val brewerVerified: Boolean?,
    @SerializedName("last_modified")
    val lastModified: Long?
)
