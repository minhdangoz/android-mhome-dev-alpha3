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

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jimmy.actions.CameraTasks
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CameraTasksImpl @Inject constructor(
    private val workManager: WorkManager
) : CameraTasks {


    override fun syncCameras() {
        val request = OneTimeWorkRequestBuilder<SyncAllCameras>()
            .addTag(SyncAllCameras.TAG)
            .build()
        workManager.enqueue(request)
    }

    override fun syncCamerasWhenIdle() {
        val request = OneTimeWorkRequestBuilder<SyncAllCameras>()
            .addTag(SyncAllCameras.TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresDeviceIdle(true)
                    .build()
            )
            .build()
        workManager.enqueue(request)
    }

    override fun setupNightSyncs() {
        val request = PeriodicWorkRequestBuilder<SyncAllCameras>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            SyncAllCameras.NIGHTLY_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
