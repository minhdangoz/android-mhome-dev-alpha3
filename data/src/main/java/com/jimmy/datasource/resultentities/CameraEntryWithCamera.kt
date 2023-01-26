/*
 * Copyright 2018 Google LLC
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

package com.jimmy.datasource.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import com.jimmy.datasource.entities.CameraEntry
import com.jimmy.datasource.entities.CameraEntity
import java.util.Objects

class CameraEntryWithCamera : EntryWithCamera<CameraEntry> {
    @Embedded
    override lateinit var entry: CameraEntry

    @Relation(parentColumn = "show_id", entityColumn = "aid")
    override lateinit var relations: List<CameraEntity>


    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is CameraEntryWithCamera -> {
            entry == other.entry && relations == other.relations
        }
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(entry, relations)
}
