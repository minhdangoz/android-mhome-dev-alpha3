package com.jimmy.datasource.services

import com.jimmy.datasource.model.AlbumModel
import com.jimmy.datasource.model.AuthResponse
import com.jimmy.datasource.model.CameraModel
import com.jimmy.datasource.model.LiveCameraModel
import com.jimmy.datasource.model.PlaybackModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CameraApiServices {

    companion object Factory {
        const val CUSTOM_HEADER = "@"
        const val NO_AUTH = "NoAuth"
    }

    /**
     * username
     * password
     * */

    @POST("auth/login")
    fun login(@Body body: HashMap<String, String>): Call<AuthResponse>

    /**
     * @param: refreshToken:
     * @return: token,refreshToken
     * */

    @POST("auth/token")
    fun refreshToken(@Body refreshToken: String): Call<AuthResponse>

    /**
     * Returns the most popular shows. Popularity is calculated using the rating percentage and the number of ratings.
     *
     * @param page Number of page of results to be returned. If `null` defaults to 1.
     * @param pageSize Number of results to return per page. If `null` defaults to 10.
     */
    @GET("camera")
    fun getAllCamera(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): Call<CameraModel>


    @GET("camera-live")
    fun getAllLiveCamera(): Call<LiveCameraModel>

    @POST("camera-live/{id}")
    fun getCameraDetails(
        @Path("id") id: String,
    ): Call<LiveCameraModel>



    /**
     * @params:
     * camId
     * startTs
     * endTs
     * recordType: NORMAL_RECORD || MOTION_RECORD
     * --optional--
     * sortProperty: ‘startVideoTime’ or ‘endVideoTime’ or ‘createdTime’
     * sortOrder:	‘ASC’ or ‘DESC’
     * */
    @GET("videoHistory/listVideos-v2")
    fun getAllPlaybackVideos(@QueryMap params: Map<String, String> ): Call<PlaybackModel>


    /**
     * @params:
     * page: start with 0
     * pageSize: total items of 1 page
     * camId
     * startTs
     * endTs
     * recordType: NORMAL_RECORD || MOTION_RECORD
     * --optional--
     * sortProperty: ‘startVideoTime’ or ‘endVideoTime’ or ‘createdTime’
     * sortOrder:	‘ASC’ or ‘DESC’
     * */
    @GET("videoHistory/paging")
    fun getPagingPlaybackVideos(@QueryMap params: Map<String, String> ): Call<PlaybackModel>


    /**
     * @params:
     * page: start with 0
     * pageSize: total items of 1 page
     * camId
     * startTs
     * endTs
     * --optional--
     * sortProperty: ‘startVideoTime’ or ‘endVideoTime’ or ‘createdTime’
     * sortOrder:	‘ASC’ or ‘DESC’
     * */
    @GET("imgHistory/paging")
    fun getPagingAlbums(@QueryMap params: Map<String, String> ): Call<AlbumModel>

}