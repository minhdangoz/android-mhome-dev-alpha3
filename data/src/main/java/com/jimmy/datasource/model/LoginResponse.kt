package com.jimmy.datasource.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @SerializedName("statusCode") var statusCode : Int    = -1,
    @SerializedName("message") var message    : String = "",
    @SerializedName("data") var data       : LoginData = LoginData()

)
data class LoginData (

    @SerializedName("token") var token      : String = "",
    @SerializedName("customerId") var customerId : Int    = 0

)