package com.jimmy.datasource.model


import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("refreshToken")
    val refreshToken: String = "",
    @SerializedName("token")
    val token: String = ""
)