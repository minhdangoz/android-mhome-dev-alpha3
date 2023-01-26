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

package com.jimmy.mhome.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.Utilities
import com.jimmy.api.UiMessage
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.AppAuthState
import com.jimmy.datasource.model.RegisterResponse
import com.jimmy.domain.Const
import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import com.jimmy.domain.helper.NetworkHelper
import com.jimmy.domain.interactors.RequestOTP
import com.jimmy.domain.interactors.RequestPassword
import com.jimmy.domain.interactors.RequestRegister
import com.jimmy.domain.interactors.RequestResendOTP
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
internal class RegisterViewModel @Inject constructor(
    private val submitPhoneRegister: RequestRegister,
    private val submitOTP: RequestOTP,
    private val resendOtp: RequestResendOTP,
    private val confirmPassword: RequestPassword,
    private val logger: Logger,
    private val networkHelper: NetworkHelper,
    private val dataStoreManager: DataStoreManager,
    private val dispatchers: AppCoroutineDispatchers,
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _userToken = MutableStateFlow("")
    private val userToken = _userToken.asStateFlow()


    private val _languageValue = MutableStateFlow(Const.LANGUAGE_ENGLISH)
    private val languageValue = _languageValue.asStateFlow()

    private val uiMessageManager = UiMessageManager()

    private val loadingState = ObservableLoadingCounter()

    val _acceptedTerms = MutableStateFlow(true)

    val _phoneNumber = MutableStateFlow("")
    private val phoneNumber = _phoneNumber.asStateFlow()

    private val _authState = MutableStateFlow(AppAuthState.LOGGED_OUT)

    val state: StateFlow<RegisterViewState> = combine(
        loadingState.observable,
        _acceptedTerms,
        phoneNumber,
        languageValue,
        _authState,
        uiMessageManager.message,
    ) { loading, accepted, phoneNumber, lang, authState, message ->
        RegisterViewState(
            loading = loading,
            acceptedTerms = _acceptedTerms,
            phoneNumber = phoneNumber,
            language = lang,
            authState = authState,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RegisterViewState.Empty,
    )

    init {
        viewModelScope.launch(dispatchers.io) {
            dataStoreManager.readStringFlow(
                PreferencesKeys.KEY_USER_LANGUAGE,
                Const.LANGUAGE_VIETNAM
            ).collect { lang ->
                withContext(Dispatchers.Main) {
                    _languageValue.value = lang
                }

            }
        }
    }

    fun clearAuthState() = viewModelScope.launch{
        _authState.emit(AppAuthState.LOGGED_OUT)
    }

    fun getOtpCode(
        phone: String,
    ) = viewModelScope.launch(dispatchers.io) {

        _phoneNumber.emit(phone)

        dataStoreManager.saveStringData(PreferencesKeys.KEY_USER_PHONE, phone)

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
        }
        else if(!_acceptedTerms.value){
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.register_not_check_terms)
            )
            return@launch
        } else {

            loadingState.addLoader()

            submitPhoneRegister.executeSync(
                RequestRegister.Params(
                    phone = phone
                        .replace(" ", "")
                        .replace("-","")
                        .replace(".",""),
                    language = languageValue.value,
                )
            ).collect { data ->

                if(data.isSuccessful){
                    val response: RegisterResponse = data.body()!!
                    when (response.statusCode) {
                        Const.STATUS_CODE_OK -> {
                            _userToken.emit(response.data.token)

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


    fun verifyOtp(inputOtp: String) = viewModelScope.launch(dispatchers.io) {

        if (!networkHelper.isNetworkConnected()) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.no_internet)
            )
            return@launch
        }

        loadingState.addLoader()

        val otp = inputOtp.trim()

        when(otp.length){
            5 -> {
                submitOTP.executeSync(
                    RequestOTP.Params(
                        otp = otp,
                        token = userToken.value,
                        language = languageValue.value,
                    )
                ).collect{ data ->

                    if(data.isSuccessful){

                        val body = data.body()

                        if(body != null){
                            when(body.statusCode){

                                Const.STATUS_CODE_OK -> {
                                    _authState.emit(AppAuthState.OTP_VERIFIED)
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
                }
            } else -> {
            uiMessageManager.emitMessage(
                UiMessage(
                    stringResource = R.string.otp_not_correct
                )
            )
        }
        }

        loadingState.removeLoader()
    }

    fun resendOtp() = viewModelScope.launch(dispatchers.io) {

        val phone = dataStoreManager.readStringData(PreferencesKeys.KEY_USER_PHONE)

        if (!networkHelper.isNetworkConnected()) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.no_internet)
            )
            return@launch
        }

        loadingState.addLoader()

        resendOtp.executeSync(
            RequestResendOTP.Params(
                phone = phone,
                language = languageValue.value,
            )
        ).collect { data ->
            if (data.isSuccessful) {

                val response: RegisterResponse = data.body()!!
                when (response.statusCode) {

                    Const.STATUS_CODE_OK -> {
                        _userToken.emit(response.data.token)

                        uiMessageManager.emitMessage(
                            UiMessage(
                                message = response.message
                            )
                        )
                    }

                    else -> {
                        uiMessageManager.emitMessage(
                            UiMessage(
                                message = response.message
                            )
                        )
                    }
                }
            } else {
                uiMessageManager.emitMessage(
                    UiMessage(
                        stringResource = R.string.server_error
                    )
                )
            }

            loadingState.removeLoader()

        }
    }

    fun onConfirmPassword(password: String) = viewModelScope.launch(dispatchers.io){

        if (!networkHelper.isNetworkConnected()) {
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.no_internet)
            )
            return@launch
        }

        loadingState.addLoader()


        confirmPassword.executeSync(
            RequestPassword.Params(
                token = userToken.value,
                password = password,
                language = languageValue.value,
            )
        ).collect{ data ->
            if(data.isSuccessful){

                val body = data.body()
                logger.i("[1] --> confirmPassword", "==body==$body")

                if(body != null){
                    when(body.statusCode){

                        Const.STATUS_CODE_OK -> {
                            Log.i("[2] --> confirmPassword", "== cfm password OK ==")

                            // in case user forgot their password
                            dataStoreManager.saveStringData(
                                PreferencesKeys.KEY_USER_PASS,
                                password
                            )
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
