package com.jimmy.datasource.model


import com.google.gson.annotations.SerializedName

data class CameraModel(
    @SerializedName("data")
    val `data`: List<CameraItem> = ArrayList<CameraItem>(),
    @SerializedName("hasNext")
    val hasNext: Boolean = false,
    @SerializedName("totalElements")
    val totalElements: Int = 0,
    @SerializedName("totalPages")
    val totalPages: Int = 0

)

data class CameraItem(
    @SerializedName("boxId")
    val boxId: String = "",
    @SerializedName("boxName")
    val boxName: String = "",
    @SerializedName("cameraName")
    val cameraName: String = "",
    @SerializedName("createdBy")
    val createdBy: String = "",
    @SerializedName("createdTime")
    val createdTime: Long? = null,
    @SerializedName("groupId")
    val groupId: String? = "",
    @SerializedName("groupName")
    val groupName: String? ="",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("ipv4")
    val ipv4: String = "",
    @SerializedName("positionId")
    val positionId: String = "",
    @SerializedName("positionName")
    val positionName: String = "",
    @SerializedName("recordSettingType")
    val recordSettingType: String? = "",
    @SerializedName("resolution")
    val resolution: String = "",
    @SerializedName("tbDeviceId")
    val tbDeviceId: String = "",
    @SerializedName("updatedBy")
    val updatedBy: String = "",
    @SerializedName("updatedTime")
    val updatedTime: Long? = null,
)