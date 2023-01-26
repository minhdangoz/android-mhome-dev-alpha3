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

import com.jimmy.datasource.model.ForgetPassResponse
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

class UpdateForgotPassword @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
    private val showService: Provider<ApiServices>
) : ResultInteractor<UpdateForgotPassword.Params, Flow<Response<ForgetPassResponse>>>() {

    override suspend fun doWork(params: Params): Flow<Response<ForgetPassResponse>> {



        val hashMap = HashMap<String, String>()
        hashMap["otp"] = params.otp
        hashMap["password"] = params.password
        hashMap["lang"] = params.language

        println("--> update forgotpass word $hashMap, token = ${params.token}")
        return flow {
            emit(
                showService.get().getUpdateForgotPassword(
                    token = params.token,
                    hashMap = hashMap,
                )
            )

        }.flowOn(dispatchers.io)
    }


    data class Params(
        val otp: String,
        val password: String,
        val token: String,
        val language: String,
    )


}
