/*
 * Copyright 2019 Google LLC
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

package com.jimmy.domain.interactors

import com.jimmy.datasource.model.AuthResponse
import com.jimmy.datasource.services.CameraApiServices
import com.jimmy.domain.ResultInteractor
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Provider

class UpdateAccessToken @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val apiCameraService: Provider<CameraApiServices>,
) : ResultInteractor<UpdateAccessToken.Params, Flow<Call<AuthResponse>>>() {

    override suspend fun doWork(params: Params): Flow<Call<AuthResponse>> {

        val hashMap = HashMap<String, String>()
        hashMap["username"] = params.username
        hashMap["password"] = params.password

        logger.i("--> update access token params: $hashMap")

        return flow {
            emit(
                apiCameraService.get().login(
                    body = hashMap
                )
            )

        }.flowOn(dispatchers.io)
    }

    data class Params(
        val username: String,
        val password: String,
    )

}

