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

package com.jimmy.mhome.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.AppAuthState
import com.jimmy.datasource.resultentities.CameraEntryWithCamera
import com.jimmy.domain.Const
import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import com.jimmy.domain.helper.NetworkHelper
import com.jimmy.domain.interactors.UpdateCameras
import com.jimmy.domain.interactors.UpdateLiveCameras
import com.jimmy.domain.observers.ObservePagedCameras
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import com.jimmy.util.ObservableLoadingCounter
import com.jimmy.util.collectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logger: Logger,
    private val networkHelper: NetworkHelper,
    private val dataStoreManager: DataStoreManager,
    pagingCameraInteractor: ObservePagedCameras,
    private val updateCameras: UpdateCameras,
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
    private val languageValue = _languageValue.asStateFlow()

    private val _authState = MutableStateFlow(AppAuthState.LOGGED_OUT)
    private val authState = _authState.asStateFlow()


    private val uiMessageManager = UiMessageManager()

    private val loadingState = ObservableLoadingCounter()

    val pagedList: Flow<PagingData<CameraEntryWithCamera>> =
        pagingCameraInteractor.flow.cachedIn(viewModelScope)

    init {
        pagingCameraInteractor(ObservePagedCameras.Params(PAGING_CONFIG))
    }

    companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20,
        )
    }


    val state: StateFlow<HomeViewState> = combine(
        loadingState.observable,
        authState,
        languageValue,
        uiMessageManager.message,
    ) { loading, authState, lang, message ->
        HomeViewState(
            authState = authState,
            loading = loading,
            language = lang,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeViewState.Empty,
    )

    init {

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logger.i("--> FID: " + task.result)
                viewModelScope.launch(dispatchers.io) {
                    _firebaseId.emit(task.result)
                    dataStoreManager.saveStringData(PreferencesKeys.KEY_FIREBASE_FID, task.result)
                }
            } else {
                val uniqueID = UUID.randomUUID().toString()
                viewModelScope.launch(dispatchers.io) {
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
            viewModelScope.launch(dispatchers.io) {
                _firebaseToken.emit(token)
                // store in datastore
                dataStoreManager.saveStringData(PreferencesKeys.KEY_NOTI_TOKEN, token)
            }
        })

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

    fun refresh(fromUser: Boolean = true) {
        viewModelScope.launch {
            updateCameras(
                UpdateCameras.Params(0, fromUser)
            ).collectStatus(loadingState, logger, uiMessageManager)
        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
