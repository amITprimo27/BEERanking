package com.example.beeranking.data.networking


import com.example.beeranking.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic ${BuildConfig.BEER_API_KEY}")
            .addHeader("accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}

object NetworkClient {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.catalog.beer/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val beerApiService: com.example.beeranking.data.services.BeerApiService by lazy {
        retrofit.create(com.example.beeranking.data.services.BeerApiService::class.java)
    }
}