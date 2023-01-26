package com.jimmy.datasource.entities


import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jimmy.datasource.entities.AppEntity
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "live_cameras",
    indices = [
        Index(value = ["aid"], unique = true),
        Index(value = ["id"], unique = true),
        Index(value = ["tbDeviceId"])
    ],
)
data class LiveCameraEntity (

    @PrimaryKey(autoGenerate = true) override var aid: Long,

    @ColumnInfo(name ="id")
    val id: String? = null,

    @ColumnInfo(name ="tenantId")
    val tenantId: String? = null,

    @ColumnInfo(name ="tbDeviceId")
    val tbDeviceId: String? = null,

    @ColumnInfo(name ="cameraName")
    val cameraName: String? = null,

    @ColumnInfo(name ="boxId")
    val boxId: String? = null,

    @ColumnInfo(name ="boxName")
    val boxName: String? = null,

    @ColumnInfo(name ="active")
    val active: Boolean = false,

    @ColumnInfo(name ="liveStream")
    val liveStream: Boolean = false,

    @ColumnInfo(name ="permitViewInTime")
    val permitViewInTime: Boolean? = null,

    @ColumnInfo(name ="ipv4")
    val ipv4: String? = null,

    @ColumnInfo(name ="rtspUsername")
    val rtspUsername: String? = null,

    @ColumnInfo(name ="rtspPassword")
    val rtspPassword: String? = null,

    @ColumnInfo(name ="onvifUsername")
    val onvifUsername: String? = null,

    @ColumnInfo(name ="onvifPassword")
    val onvifPassword: String? = null,

    @ColumnInfo(name ="onvifPort")
    val onvifPort: String? = null,

    @ColumnInfo(name ="onvifUrl")
    val onvifURL: String? = null,

    @ColumnInfo(name ="channel")
    val channel: String? = null,

    @ColumnInfo(name ="resolution")
    val resolution: String? = null,

    @ColumnInfo(name ="resolutionSetting")
    val resolutionSetting: String? = null,

    @ColumnInfo(name ="fps")
    val fps: String? = null,

    @ColumnInfo(name ="bitrate")
    val bitrate: String? = null,

    @ColumnInfo(name ="fisheye")
    val fisheye: Boolean? = null,

    @ColumnInfo(name ="fisheyeSupport")
    val fisheyeSupport: Boolean? = null,

//    @ColumnInfo(name ="channelSupport")
//    @Embedded val channelSupport: List<String>? = null,


//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id"
//    )
//    @ColumnInfo(name ="optionSetting")
//    @Embedded val optionSetting: OptionSetting? = null,

    @ColumnInfo(name ="mainRtspUrl")
    val mainRtspUrl: String? = null,

    @ColumnInfo(name ="subRtspUrl")
    val subRtspUrl: String? = null,

    @ColumnInfo(name ="rtmpUrl")
    val rtmpUrl: String? = null,

    @ColumnInfo(name ="rtmpStreamId")
    val rtmpStreamId: String? = null,

    @ColumnInfo(name ="streamViewUrl")
    val streamViewUrl: String? = null,

    @ColumnInfo(name ="streamHlsUrl")
    val streamHlsUrl: String? = null,

    @ColumnInfo(name ="webSocketUrl")
    val webSocketUrl: String? = null,

    @ColumnInfo(name ="token")
    val token: String? = null,

    @ColumnInfo(name ="groupName")
    val groupName: String? = null,

    @ColumnInfo(name ="groupId")
    val groupId: String? = null,

    @ColumnInfo(name ="createdTime")
    val createdTime: OffsetDateTime? = null

) : AppEntity
//
//@Entity
//data class OptionSetting (
//    @PrimaryKey val optionId: Long,
//    val id: String? = "",
//
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id"
//    )
//    @Embedded val mainStream: Stream? = null,
//
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id"
//    )
//    @Embedded val minorStream: Stream? = null
//)
//
//@Entity
//data class Stream (
//    @PrimaryKey val streamId: Long? = 0L,
//
//    @Relation(
//        parentColumn = "streamId",
//        entityColumn = "res"
//    )
//    @Embedded val resolutionSupport: List<ResolutionSupport>? = null,
//    @Embedded val fpsSupport: List<Long>? = null,
//    @Embedded val bitrateSupport: List<Long>? = null
//)
//
//@Entity
//data class ResolutionSupport (
//    @PrimaryKey val res: Long? = 0L,
//    val displayKey: String? = null,
//
//    @Relation(
//        parentColumn = "res",
//        entityColumn = "settingId"
//    )
//    @Embedded val settingValue: SettingValue? = null
//)
//
//@Entity
//data class SettingValue (
//    @PrimaryKey val settingId: Long? = 0L,
//    val width: Long? = null,
//    val height: Long? = null
//)