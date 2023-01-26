package com.jimmy.mhome.inject.auth

import com.jimmy.datasource.services.CameraApiServices
import com.jimmy.datasource.services.CameraApiServices.Factory.CUSTOM_HEADER
import com.jimmy.datasource.services.CameraApiServices.Factory.NO_AUTH
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val userLocalSource: AccessTokenWrapper,
    private val apiService: Provider<CameraApiServices>,
) : Interceptor {
    private val mutex = Mutex()


    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().also { Timber.i("[1] $it") }

        if (NO_AUTH in req.headers.values(CUSTOM_HEADER)) {
            return chain.proceedWithToken(req, null)
        }

        val token =
            runBlocking {
                userLocalSource.getAccessToken()
            }.also {
                Timber.i("[2] $req $it")
            }
        val res = chain.proceedWithToken(req, token)


        // in case request un-auth > refresh
        if (res.code != HTTP_UNAUTHORIZED) {
            return res
        }

        Timber.i("[3] $req")

        val newToken: String? = runBlocking {
            mutex.withLock {
                val user =
                    userLocalSource.getAccessToken().also { Timber.i("[4] $req $it") }
                val maybeUpdatedToken = user

                when {
                    user.isEmpty() || maybeUpdatedToken.isEmpty() -> null.also {
                        Timber.i("[5-1] $req")
                    } // already logged out!

                    maybeUpdatedToken != token -> maybeUpdatedToken.also {
                        Timber.i("[5-2] $req")
                    } // refreshed by another request

                    else -> {
                        Timber.i("[5-3] $req")

                        val refreshTokenRes =
                            apiService.get().refreshToken(userLocalSource.getRefreshToken()).execute()
                                .also {
                                    Timber.i("[6] $req $it")
                                }

                        val code = refreshTokenRes.code()
                        if (code == HTTP_OK) {
                            refreshTokenRes.body()?.token?.also {
                                Timber.i("[7-1] $req")

                                userLocalSource.saveAccessToken(it)
                            }

                            refreshTokenRes.body()?.refreshToken?.also {
                                Timber.i("[7-11] $req")

                                userLocalSource.saveRefreshToken(it)
                            }
                        } else if (code == HTTP_UNAUTHORIZED) {
                            Timber.i("[7-2] $req")
                            userLocalSource.saveAccessToken(null)
                            null
                        } else {
                            Timber.i("[7-3] $req")
                            null
                        }
                    }
                }
            }
        }

        return if (newToken !== null) chain.proceedWithToken(req, newToken) else res
    }

    private fun Interceptor.Chain.proceedWithToken(req: Request, token: String?): Response =
        req.newBuilder()
            .apply {
                if (token !== null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .removeHeader(CUSTOM_HEADER)
            .build()
            .let(::proceed)
}