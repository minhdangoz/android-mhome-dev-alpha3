package com.jimmy.datasource.model


import com.google.gson.annotations.SerializedName

data class PlaybackModel (

    @SerializedName("data"          ) var data          : List<PlaybackItem> = ArrayList(),
    @SerializedName("totalPages"    ) var totalPages    : Int?            = null,
    @SerializedName("totalElements" ) var totalElements : Int?            = null,
    @SerializedName("hasNext"       ) var hasNext       : Boolean?        = null

)


data class PlaybackItem (

    @SerializedName("id"             ) var id             : String? = null,
    @SerializedName("boxId"          ) var boxId          : String? = null,
    @SerializedName("cameraId"       ) var cameraId       : String? = null,
    @SerializedName("videoUrl"       ) var videoUrl       : String? = null,
    @SerializedName("size"           ) var size           : Int?    = null,
    @SerializedName("startVideoTime" ) var startVideoTime : Long?    = null,
    @SerializedName("endVideoTime"   ) var endVideoTime   : Long?    = null,
    @SerializedName("recordType"     ) var recordType     : String? = null,
    @SerializedName("createdTime"    ) var createdTime    : Long?    = null

)