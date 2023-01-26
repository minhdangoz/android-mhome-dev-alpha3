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

package com.jimmy.mhome.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.Utilities
import com.jimmy.api.UiMessage
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.AppAuthState
import com.jimmy.datasource.model.ForgetPassResponse
import com.jimmy.domain.Const
import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import com.jimmy.domain.helper.NetworkHelper
import com.jimmy.domain.interactors.RequestForgotPhone
import com.jimmy.domain.interactors.UpdateForgotPassword
import com.jimmy.extensions.combine
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import com.jimmy.util.ObservableLoadingCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class ForgotPasswordViewModel @Inject constructor(
    private val updateForgotPassword: UpdateForgotPassword,
    private val requestForgotPhone: RequestForgotPhone,
    private val logger: Logger,
    private val dispatchers: AppCoroutineDispatchers,
    private val networkHelper: NetworkHelper,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _userToken = MutableStateFlow("")
    private val userToken = _userToken.asStateFlow()

    private val _languageValue = MutableStateFlow(Const.LANGUAGE_ENGLISH)
    private val languageValue = _languageValue.asStateFlow()

    private val uiMessageManager = UiMessageManager()

    private val loadingState = ObservableLoadingCounter()

    private val _authState = MutableStateFlow(AppAuthState.LOGGED_OUT)
    private val authState = _authState.asStateFlow()

    val state: StateFlow<ForgotPasswordViewState> = combine(
        loadingState.observable,
        languageValue,
        authState,
        uiMessageManager.message,
    ) { loading, lang, authState, message ->
        ForgotPasswordViewState(
            loading = loading,
            language = lang,
            authState = authState,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ForgotPasswordViewState.Empty,
    )

    init {
        viewModelScope.launch(dispatchers.io) {
            dataStoreManager.readStringFlow(
                PreferencesKeys.KEY_USER_LANGUAGE,
                Const.LANGUAGE_VIETNAM
            ).collect { lang ->
                withContext(dispatchers.main) {
                    _languageValue.value = lang
                }

            }
        }
    }

    fun clearAuthState() = viewModelScope.launch {
        _authState.emit(AppAuthState.LOGGED_OUT)
    }


    // user enter phone number > get otp > confirm new password
    fun requestForgotPhone(
        phone: String,
    ) = viewModelScope.launch(dispatchers.io) {

        if (!networkHelper.isNetworkConnected()) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.no_internet)
            )
            return@launch
        } else if (!Utilities.isValidPhoneNumber(phone)) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.phone_number_invalid)
            )
            return@launch
        } else {

            loadingState.addLoader()

            requestForgotPhone.executeSync(
                RequestForgotPhone.Params(
                    phone = phone
                        .replace(" ", "")
                        .replace("-","")
                        .replace(".",""),
                    language = languageValue.value,
                )
            ).collect { data ->

                if(data.isSuccessful){
                    val response: ForgetPassResponse = data.body()!!

                    when (response.statusCode) {
                        Const.STATUS_CODE_OK -> {
                            _userToken.emit(response.data.token)

                            Log.i("== store user token == ", response.data.token)

                            dataStoreManager.saveStringData(
                                PreferencesKeys.KEY_REGISTER_TOKEN,
                                response.data.token
                            )

                            _authState.emit(AppAuthState.REGISTERED)
                        }

                        else -> {
                            uiMessageManager.emitMessage(
                                UiMessage(
                                    message = response.message
                                )
                            )
                        }
                    }
                } else{
                    uiMessageManager.emitMessage(
                        UiMessage(
                            message = "Server error!"
                        )
                    )
                }



                loadingState.removeLoader()
            }
        }

    }

    fun onConfirmPassword(
        otp: String,
        password: String
    ) = viewModelScope.launch(dispatchers.io){

        val token = dataStoreManager.readStringData(
            PreferencesKeys.KEY_REGISTER_TOKEN
        )

        if (!networkHelper.isNetworkConnected()) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.no_internet)
            )
            return@launch
        }

        loadingState.addLoader()

        logger.i("--> password: $password")

        updateForgotPassword.executeSync(
            UpdateForgotPassword.Params(
                otp = otp,
                token = token,
                password = password,
                language = languageValue.value,
            )
        ).collect { data ->
            if(data.isSuccessful){

                val body = data.body()

                if(body != null){
                    when(body.statusCode){

                        Const.STATUS_CODE_OK -> {
                            logger.i("--> cfm password OK go to login")

                            uiMessageManager.emitMessage(
                                UiMessage(
                                    message = body.message
                                )
                            )

                            _authState.emit(AppAuthState.PASSWORD_CONFIRMED)
                        }

                        else -> {
                            uiMessageManager.emitMessage(
                                UiMessage(
                                    message = body.message
                                )
                            )
                        }
                    }
                } else{
                    uiMessageManager.emitMessage(
                        UiMessage(
                            stringResource = R.string.server_error
                        )
                    )
                }
            } else{
                uiMessageManager.emitMessage(
                    UiMessage(
                        stringResource = R.string.server_error
                    )
                )
            }


            loadingState.removeLoader()
        }

    }


    fun updateLanguage(language: String) = viewModelScope.launch(dispatchers.io) {
        dataStoreManager.saveStringData(
            PreferencesKeys.KEY_USER_LANGUAGE,
            language
        )

        withContext(Dispatchers.Main) {
            _languageValue.value = language
        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
