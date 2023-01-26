/*
 * Copyright 2020 Google LLC
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

import android.app.Application
import com.jimmy.actions.CameraTasks
import com.jimmy.appinitializers.AppInitializer
import dagger.Lazy
import javax.inject.Inject

class CameraTasksInitializer @Inject constructor(
    private val showTasks: Lazy<CameraTasks>
) : AppInitializer {
    override fun init(application: Application) {
        showTasks.get().setupNightSyncs()
    }
}
