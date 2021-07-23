package com.edheba.serverdrivenuitest

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import com.edheba.serverdrivenuitest.BuildConfig
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Saurav
 * on 7/23/2021
 */
object RetrofitClient {

    val BASE_URL = "http://192.168.1.4:8080/api/v1/"
    var retrofit: Retrofit? = null

    fun getUIRetrofit(): Retrofit? {

        if (retrofit == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .client(
                    OkHttpClient().newBuilder()
                        .connectTimeout(300, TimeUnit.SECONDS)
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level =
                                if (BuildConfig.DEBUG) {
                                    HttpLoggingInterceptor.Level.BODY
                                } else {
                                    HttpLoggingInterceptor.Level.NONE
                                }
                        })
                        .build()
                )
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}