package com.jimmy.mhome.inject.auth

import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import javax.inject.Inject

class AccessTokenWrapper @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {


    fun getAccessToken(): String {
        return dataStoreManager.readStringData(PreferencesKeys.KEY_CAMERA_ACCESS_TOKEN, "")
    }

    fun saveAccessToken(accessToken: String?) {
        if (accessToken != null) {
            if(accessToken.isNotEmpty()){
                dataStoreManager.saveSyncStringData(PreferencesKeys.KEY_CAMERA_ACCESS_TOKEN, accessToken)
            }
        }
    }

    fun getRefreshToken(): String {
        return dataStoreManager.readStringData(
            PreferencesKeys.KEY_CAMERA_REFRESH_TOKEN, "")
    }

    fun saveRefreshToken(refreshToken: String?) {
        if (refreshToken != null) {
            if(refreshToken.isNotEmpty()){
                dataStoreManager.saveSyncStringData(PreferencesKeys.KEY_CAMERA_REFRESH_TOKEN, refreshToken)
            }
        }
    }
}