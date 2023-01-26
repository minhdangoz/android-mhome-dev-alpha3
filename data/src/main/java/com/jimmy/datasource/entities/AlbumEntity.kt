package com.jimmy.datasource.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "album",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "aid") override val aid: Long = 0L,

    @ColumnInfo(name ="id")
    val id: String = "",

    @ColumnInfo(name ="boxId")
    val boxId: String = "",

    @ColumnInfo(name ="cameraId")
    val cameraId: String = "",

    @ColumnInfo(name ="imgUrl")
    val imgUrl: String = "",

    @ColumnInfo(name ="size")
    val size: Int = 0,

    @ColumnInfo(name ="endVideoTime")
    val recordTime: OffsetDateTime? = null,

    @ColumnInfo(name ="createdTime")
    val createdTime: OffsetDateTime? = null,


    ) : AppEntity {

    companion object {
        val EMPTY_PLAYBACK = AlbumEntity()
    }
}
