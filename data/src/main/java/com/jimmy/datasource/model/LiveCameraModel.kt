package com.jimmy.datasource.model


import com.google.gson.annotations.SerializedName

data class LiveCameraModel(
    @SerializedName("active")
    val active: Boolean = false,
    @SerializedName("bitrate")
    val bitrate: Int = 0,
    @SerializedName("boxId")
    val boxId: String = "",
    @SerializedName("boxName")
    val boxName: String = "",
    @SerializedName("cameraName")
    val cameraName: String = "",
    @SerializedName("channel")
    val channel: String = "",
    @SerializedName("channelSupport")
    val channelSupport: List<String> = listOf(),
    @SerializedName("createdTime")
    val createdTime: Long = 0,
    @SerializedName("fisheye")
    val fisheye: Boolean = false,
    @SerializedName("fisheyeSupport")
    val fisheyeSupport: Boolean = false,
    @SerializedName("fps")
    val fps: Int = 0,
    @SerializedName("groupId")
    val groupId: Any = Any(),
    @SerializedName("groupName")
    val groupName: Any = Any(),
    @SerializedName("id")
    val id: String = "",
    @SerializedName("ipv4")
    val ipv4: String = "",
    @SerializedName("liveStream")
    val liveStream: Boolean = false,
    @SerializedName("mainRtspUrl")
    val mainRtspUrl: String = "",
    @SerializedName("onvifPassword")
    val onvifPassword: String = "",
    @SerializedName("onvifPort")
    val onvifPort: Int = 0,
    @SerializedName("onvifUrl")
    val onvifUrl: Any = Any(),
    @SerializedName("onvifUsername")
    val onvifUsername: String = "",
    @SerializedName("optionSetting")
    val optionSetting: OptionSetting = OptionSetting(),
    @SerializedName("permitViewInTime")
    val permitViewInTime: Boolean = false,
    @SerializedName("resolution")
    val resolution: String = "",
    @SerializedName("resolutionSetting")
    val resolutionSetting: Any = Any(),
    @SerializedName("rtmpStreamId")
    val rtmpStreamId: String = "",
    @SerializedName("rtmpUrl")
    val rtmpUrl: Any = Any(),
    @SerializedName("rtspPassword")
    val rtspPassword: String = "",
    @SerializedName("rtspUsername")
    val rtspUsername: String = "",
    @SerializedName("streamHlsUrl")
    val streamHlsUrl: String = "",
    @SerializedName("streamViewUrl")
    val streamViewUrl: String = "",
    @SerializedName("subRtspUrl")
    val subRtspUrl: String = "",
    @SerializedName("tbDeviceId")
    val tbDeviceId: String = "",
    @SerializedName("tenantId")
    val tenantId: String = "",
    @SerializedName("token")
    val token: String = "",
    @SerializedName("webSocketUrl")
    val webSocketUrl: String = ""
) {
    data class OptionSetting(
        @SerializedName("mainStream")
        val mainStream: MainStream = MainStream(),
        @SerializedName("minorStream")
        val minorStream: MinorStream = MinorStream()
    ) {
        data class MainStream(
            @SerializedName("bitrateSupport")
            val bitrateSupport: List<Int> = listOf(),
            @SerializedName("fpsSupport")
            val fpsSupport: List<Int> = listOf(),
            @SerializedName("resolutionSupport")
            val resolutionSupport: List<ResolutionSupport> = listOf()
        ) {
            data class ResolutionSupport(
                @SerializedName("displayKey")
                val displayKey: String = "",
                @SerializedName("settingValue")
                val settingValue: SettingValue = SettingValue()
            ) {
                data class SettingValue(
                    @SerializedName("height")
                    val height: Int = 0,
                    @SerializedName("width")
                    val width: Int = 0
                )
            }
        }

        data class MinorStream(
            @SerializedName("bitrateSupport")
            val bitrateSupport: List<Int> = listOf(),
            @SerializedName("fpsSupport")
            val fpsSupport: List<Int> = listOf(),
            @SerializedName("resolutionSupport")
            val resolutionSupport: List<ResolutionSupport> = listOf()
        ) {
            data class ResolutionSupport(
                @SerializedName("displayKey")
                val displayKey: String = "",
                @SerializedName("settingValue")
                val settingValue: SettingValue = SettingValue()
            ) {
                data class SettingValue(
                    @SerializedName("height")
                    val height: Int = 0,
                    @SerializedName("width")
                    val width: Int = 0
                )
            }
        }
    }
}