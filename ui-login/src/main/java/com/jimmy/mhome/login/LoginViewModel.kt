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

package com.jimmy.mhome.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.jimmy.Utilities
import com.jimmy.api.UiMessage
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.AppAuthState
import com.jimmy.domain.Const
import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import com.jimmy.domain.helper.NetworkHelper
import com.jimmy.domain.interactors.RequestLogin
import com.jimmy.domain.interactors.UpdateAccessToken
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import com.jimmy.util.ObservableLoadingCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val requestLogin: RequestLogin,
    private val updateAccessToken: UpdateAccessToken,
    private val logger: Logger,
    private val networkHelper: NetworkHelper,
    private val dataStoreManager: DataStoreManager,
    private val dispatchers: AppCoroutineDispatchers,
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _firebaseToken = MutableStateFlow("")
    val firebaseToken = _firebaseToken.asStateFlow()

    private val _firebaseId = MutableStateFlow("")
    val firebaseId = _firebaseId.asStateFlow()

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    private val _languageValue = MutableStateFlow(Const.LANGUAGE_ENGLISH)
    val languageValue = _languageValue.asStateFlow()

    private val uiMessageManager = UiMessageManager()

    private val loadingState = ObservableLoadingCounter()

    private val _authState = MutableStateFlow(AppAuthState.LOGGED_OUT)
    private val authState = _authState.asStateFlow()

    val state: StateFlow<LoginViewState> = combine(
        loadingState.observable,
        authState,
        languageValue,
        uiMessageManager.message,
    ) { loading, authState, lang, message ->
        LoginViewState(
            authState = authState,
            loading = loading,
            language = lang,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginViewState.Empty,
    )

    init {



        //TODO: for testing
        requestCameraAccessToken()




        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModelScope.launch(Dispatchers.IO) {
                    _firebaseId.emit(task.result)
                    // store in datastore
                    dataStoreManager.saveStringData(PreferencesKeys.KEY_FIREBASE_FID, task.result)
                }
            } else {
                val uniqueID = UUID.randomUUID().toString()
                viewModelScope.launch(Dispatchers.IO) {
                    _firebaseId.emit(uniqueID)
                    // store in datastore
                    dataStoreManager.saveStringData(PreferencesKeys.KEY_FIREBASE_FID, uniqueID)
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            viewModelScope.launch(Dispatchers.IO) {
                _firebaseToken.emit(token)
                // store in datastore
                dataStoreManager.saveStringData(PreferencesKeys.KEY_NOTI_TOKEN, token)
            }
        })

        viewModelScope.launch(Dispatchers.IO) {
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

    fun updateLanguage(language: String) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreManager.saveStringData(
            PreferencesKeys.KEY_USER_LANGUAGE,
            language
        )

        withContext(Dispatchers.Main) {
            _languageValue.value = language
        }
    }

    fun requestLogin(
        phone: String,
        password: String
    ) = viewModelScope.launch(Dispatchers.IO) {

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
        } else if(password.isEmpty() || password.length !in 6..20){
            uiMessageManager.emitMessage(
                UiMessage(stringResource = R.string.password_invalid)
            )
            return@launch
        } else {

            loadingState.addLoader()

            requestLogin.executeSync(
                RequestLogin.Params(
                    phone = phone
                        .replace(" ", "")
                        .replace("-","")
                        .replace(".","").trim(),
                    password = password,
                    language = languageValue.value,
                    firebaseId = firebaseId.value,
                    firebaseToken = _firebaseToken.value
                )
            ).collect { data ->
                val response = data.body()!!
                logger.i("==login response ==", response.message)
                when (response.statusCode) {
                    Const.STATUS_CODE_OK -> {
                        _userToken.emit(response.data.token)

                        dataStoreManager.saveStringData(
                            PreferencesKeys.KEY_USER_TOKEN,
                            response.data.token
                        )

                        dataStoreManager.saveStringData(
                            PreferencesKeys.KEY_USER_PHONE,
                            phone
                        )

                        logger.setUserId(phone)

                        // emit auth state
                        _authState.emit(AppAuthState.LOGGED_IN)
                    }

                    else -> {
                        uiMessageManager.emitMessage(
                            UiMessage(
                                message = response.message
                            )
                        )
                    }
                }

                loadingState.removeLoader()

            }
        }

    }


    private fun requestCameraAccessToken(
        username: String = "vanbinhdoan97@gmail.com",
        password: String = "123qwe"
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (!networkHelper.isNetworkConnected()) {
            return@launch
        }

        Log.i("-->", "--> requestCameraAccessToken")
        updateAccessToken.executeSync(
            UpdateAccessToken.Params(
                username = username,
                password = password,
            )
        ).collect { data ->
            val response = data.awaitResponse().body()!!

            saveAccessToken(response.token)
            saveRefreshToken(response.refreshToken)

        }
    }

    private fun saveRefreshToken(refreshToken: String?) {
        if (refreshToken != null) {
            if(refreshToken.isNotEmpty()){
                viewModelScope.launch(dispatchers.io) {
                    dataStoreManager.saveStringData(
                        PreferencesKeys.KEY_CAMERA_REFRESH_TOKEN,
                        refreshToken
                    )
                }
            }
        }
    }

    private fun saveAccessToken(accessToken: String?) {
        if (accessToken != null) {
            if(accessToken.isNotEmpty()){
                viewModelScope.launch(dispatchers.io) {
                    dataStoreManager.saveStringData(
                        PreferencesKeys.KEY_CAMERA_ACCESS_TOKEN,
                        accessToken
                    )
                }
            }
        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
