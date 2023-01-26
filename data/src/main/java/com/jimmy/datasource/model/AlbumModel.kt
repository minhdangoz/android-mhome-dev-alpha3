package com.jimmy.datasource.model

import com.google.gson.annotations.SerializedName

data class AlbumModel (

    @SerializedName("data"          ) var data          : ArrayList<AlbumItem> = arrayListOf(),
    @SerializedName("totalPages"    ) var totalPages    : Int?            = null,
    @SerializedName("totalElements" ) var totalElements : Int?            = null,
    @SerializedName("hasNext"       ) var hasNext       : Boolean?        = null

)


data class AlbumItem (

    @SerializedName("id"          ) var id          : String? = null,
    @SerializedName("boxId"       ) var boxId       : String? = null,
    @SerializedName("cameraId"    ) var cameraId    : String? = null,
    @SerializedName("imgUrl"      ) var imgUrl      : String? = null,
    @SerializedName("size"        ) var size        : Int?    = null,
    @SerializedName("recordTime"  ) var recordTime  : Long?    = null,
    @SerializedName("createdTime" ) var createdTime : Long?    = null

)