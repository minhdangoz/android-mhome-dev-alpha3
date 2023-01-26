package com.jimmy.datasource.model

import com.google.gson.annotations.SerializedName

data class ForgetPassResponse (

    @SerializedName("statusCode") var statusCode : Int    = -1,
    @SerializedName("message") var message    : String = "",
    @SerializedName("data") var data       : ForgetPassData = ForgetPassData("")
)

data class ForgetPassData (
    @SerializedName("token") val token : String
)