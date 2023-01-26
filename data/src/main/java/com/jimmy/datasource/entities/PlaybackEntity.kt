package com.jimmy.datasource.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "playback",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class PlaybackEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "aid") override val aid: Long = 0L,
    @ColumnInfo(name ="boxId")
    val boxId: String = "",
    @ColumnInfo(name ="cameraId")
    val cameraId: String = "",
    @ColumnInfo(name ="createdTime")
    val createdTime: OffsetDateTime? = null,
    @ColumnInfo(name ="endVideoTime")
    val endVideoTime: OffsetDateTime? = null,
    @ColumnInfo(name ="id")
    val id: String = "",
    @ColumnInfo(name ="recordType")
    val recordType: String = "",
    @ColumnInfo(name ="size")
    val size: Int = 0,
    @ColumnInfo(name ="startVideoTime")
    val startVideoTime: OffsetDateTime? = null,
    @ColumnInfo(name ="videoUrl")
    val videoUrl: String = "",
) : AppEntity {

    companion object {
        val EMPTY_PLAYBACK = PlaybackEntity()
    }
}
