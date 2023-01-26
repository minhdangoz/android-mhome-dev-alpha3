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

import com.jimmy.datasource.model.RegisterResponse
import com.jimmy.datasource.services.ApiServices
import com.jimmy.domain.ResultInteractor
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Provider

class RequestPassword @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val showService: Provider<ApiServices>
) : ResultInteractor<RequestPassword.Params, Flow<Response<RegisterResponse>>>() {

    override suspend fun doWork(params: Params): Flow<Response<RegisterResponse>> {

        val hashMap = HashMap<String, String>()
        hashMap["password"] = params.password
        hashMap["lang"] = params.language

        return flow {
            emit(
                showService.get().submitPassword(
                    token = params.token,
                    hashMap = hashMap
                )
            )

        }.flowOn(Dispatchers.IO)

    }

    data class Params(
        val token: String,
        val password: String,
        val language: String
    )

}
