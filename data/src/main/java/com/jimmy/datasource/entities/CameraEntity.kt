/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jimmy.datasource.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "cameras",
    indices = [
        Index(value = ["aid"], unique = true),
        Index(value = ["id"], unique = true),
        Index(value = ["boxId"])
    ],
)
data class CameraEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "aid") override val aid: Long = 0L,
    @ColumnInfo(name ="id")val id : String? = null,
    @ColumnInfo(name ="createdTime") val createdTime : OffsetDateTime? = null,
    @ColumnInfo(name ="createdBy") val createdBy : String? = null,
    @ColumnInfo(name ="updatedTime") val updatedTime : OffsetDateTime? = null,
    @ColumnInfo(name ="updatedBy") val updatedBy : String? = null,
    @ColumnInfo(name ="tbDeviceId") val tbDeviceId : String? = null,
    @ColumnInfo(name ="cameraName") val cameraName : String? = null,
    @ColumnInfo(name ="boxId") val boxId : String? = null,
    @ColumnInfo(name ="boxName") val boxName : String? = null,
    @ColumnInfo(name ="ipv4") val ipv4 : String? = null,
    @ColumnInfo(name ="resolution") val resolution : String? = null,
    @ColumnInfo(name ="groupId") val groupId : String? = null,
    @ColumnInfo(name ="groupName") val groupName : String? = null,
    @ColumnInfo(name ="positionId") val positionId : String? = null,
    @ColumnInfo(name ="positionName") val positionName : String? = null,
    @ColumnInfo(name ="recordSettingType") val recordSettingType : String? = null,
) : AppEntity {

    companion object {
        val EMPTY_SHOW = CameraEntity()
    }
}
