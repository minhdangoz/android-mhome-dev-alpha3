package com.jimmy.datasource.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse (

    @SerializedName("statusCode") var statusCode : Int    = -1,
    @SerializedName("message") var message    : String = "",
    @SerializedName("data") var data       : Data = Data(0,"")
)

data class Data (
    @SerializedName("id") val id : Int,
    @SerializedName("token") val token : String
)