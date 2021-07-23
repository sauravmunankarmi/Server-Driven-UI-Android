package com.edheba.serverdrivenuitest

import com.google.gson.annotations.SerializedName

/**
 * Created by Saurav
 * on 7/23/2021
 */
data class UIResponse(
    @SerializedName("layout")
    val layout: Object,

    @SerializedName("layoutData")
    val data: Object
)