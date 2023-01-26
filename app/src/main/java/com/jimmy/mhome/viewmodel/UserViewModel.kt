package com.jimmy.mhome.viewmodel

import androidx.lifecycle.ViewModel
import com.jimmy.domain.Const
import com.jimmy.domain.DataStoreManager
import com.jimmy.domain.PreferencesKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {

    fun isAuthenticated(): Boolean = dataStoreManager.readStringData(
        PreferencesKeys.KEY_USER_TOKEN
    ).isNotEmpty()


    fun getLanguage(): String = dataStoreManager.readStringData(
        PreferencesKeys.KEY_USER_LANGUAGE,
        Const.LANGUAGE_VIETNAM
    )

//    fun initToken() = viewModelScope.launch(Dispatchers.IO) {
//
//            requestLogin.executeSync(
//                GetAuthResponse.Params(
//                    username = "vanbinhdoan97@gmail.com",
//                    password = "123qwe"
//                )
//            ).collect { response ->
//                if (response.token.isNotEmpty()) {
//
//                    Log.i("==@==", "==>save token<== ${response.token}")
//                    dataStoreManager.saveStringData(
//                        PreferencesKeys.KEY_CAMERA_AUTH_TOKEN,
//                        response.token.trim()
//                    )
//                }
//
//            }
//
//        }
}
