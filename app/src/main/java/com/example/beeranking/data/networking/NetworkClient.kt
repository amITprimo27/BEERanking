package com.example.beeranking.data.networking


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//TODO: check auth of api and add interceptor if needed
//class AuthInterceptor : Interceptor {
//
//    companion object {
//        private const val YOUR_API_KEY = "ACCESS_TOKEN"
//    }
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $YOUR_API_KEY")
//            .addHeader("accept", "application/json")
//            .build()
//
//        return chain.proceed(request)
//    }
//}

object NetworkClient {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor())
            .build()
    }

    //TODO: add api clients here
}