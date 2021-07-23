package com.edheba.serverdrivenuitest

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Saurav
 * on 7/23/2021
 */
interface ApiInterface {

    @GET("ui")
    fun getUI(): Call<UIResponse>

}