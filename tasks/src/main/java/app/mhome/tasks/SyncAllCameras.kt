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

package app.mhome.tasks

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jimmy.datasource.entities.RefreshType
import com.jimmy.domain.interactors.UpdateCameras
import com.jimmy.util.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncAllCameras @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updateCameras: UpdateCameras,
    private val logger: Logger
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "sync-all-cameras"
        const val NIGHTLY_SYNC_TAG = "night-sync-all-cameras"
    }

    override suspend fun doWork(): Result {
        logger.d("$tags worker running")
        updateCameras.executeSync(UpdateCameras.Params(0, true))
        return Result.success()
    }
}
