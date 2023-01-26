package com.jimmy.datasource.services

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.jimmy.datasource.model.ForgetPassResponse
import com.jimmy.datasource.model.LoginResponse
import com.jimmy.datasource.model.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

const val CONTENT_TYPE = "Content-Type: application/json"
const val AUTH = "Authorization: Basic bW9iaWZvbmU6bW9iaWZvbmVAMTIz="

interface ApiServices {

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/register-new")
    suspend fun register(@Body regis: HashMap<String, String>): Response<RegisterResponse>

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/login")
    suspend fun requestLogin(@Body login: HashMap<String, String>): Response<LoginResponse>

    /**
     * update forgot password
     * @param
     * hashMap.put("otp",otp);
     * hashMap.put("password",pass);
     * hashMap.put("lang",mLanguageCode);
     * **/

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/new-password")
    suspend fun getUpdateForgotPassword(
        @Header("token") token: String,
        @Body hashMap: HashMap<String, String>
    ): Response<ForgetPassResponse>

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/resend-opt-register")
    suspend fun resendOTP(@Body hashMap: HashMap<String, String>): Response<RegisterResponse>

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/verify-otp-register")
    suspend fun submitOTP(
        @Header("token") token: String,
        @Body hashMap: HashMap<String, String>
    ): Response<RegisterResponse>

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/update-password")
    suspend fun submitPassword(
        @Header("token") token: String,
        @Body hashMap: HashMap<String, String>
    ): Response<RegisterResponse>

    @Headers(CONTENT_TYPE, AUTH)
    @POST("auth/forget-password")
    suspend fun getForgotPassword(
        @Body hashMap: HashMap<String, String>
    ): Response<ForgetPassResponse>



    /**
     * used for changing user password
     * @param:
     * hashMap.put("old_password",mOldPass);
     * hashMap.put("new_password",mNewPass);
     * hashMap.put("confirm_password",mConfirmPass);
     * hashMap.put("lang",mLanguageCode);
     * */
    @Headers(CONTENT_TYPE, AUTH)
    @POST("customers/change-password")
    suspend fun changePassword(
        @Header("token") token: String,
        @Body hashMap: HashMap<String, String>
    ): Response<ForgetPassResponse>



    /**
     * Customer information
     * @param: token
     * */
    @Headers(CONTENT_TYPE, AUTH)
    @GET("customers/info")
    suspend fun customerInfo(
        @Header("token") token: String,
    ): Response<ResponseBody>



}