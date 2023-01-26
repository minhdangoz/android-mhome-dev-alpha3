/*
 * Copyright 2020 Google LLC
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.bodyWidth
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import com.jimmy.datasource.AppAuthState
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordConfirm(
    openLogin: () -> Unit,
) {
    ForgotPasswordConfirm (
        viewModel = hiltViewModel(),
        openLogin = openLogin,
    )
}

@Composable
private fun ForgotPasswordConfirm(
    viewModel: ForgotPasswordViewModel,
    openLogin: () -> Unit,
) {

    val viewState by rememberStateWithLifecycle(viewModel.state)

    ForgotPasswordConfirm(
        state = viewState,
        onMessageShown = { viewModel.clearMessage(it) },
        onChangedLanguage = {lang ->  viewModel.updateLanguage(lang)},
        onSubmitPassword = {otp, password -> viewModel.onConfirmPassword(otp, password)},
        openLogin = openLogin,
        clearAuthState = {viewModel.clearAuthState()}
    )
}

@Composable
private fun ForgotPasswordConfirm(
    state: ForgotPasswordViewState,
    onMessageShown: (id: Long) -> Unit,
    onChangedLanguage: (lang: String) -> Unit,
    onSubmitPassword: (otp: String, password: String) -> Unit,
    openLogin: () -> Unit,
    clearAuthState: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    if(state.authState == AppAuthState.PASSWORD_CONFIRMED){
        LaunchedEffect(key1 = state.authState){
            openLogin()
            clearAuthState()
        }
    }

    state.message?.let { message ->
        val stringMessage : String = if(message.stringResource > 0){
            stringResource(id = message.stringResource)
        } else{
            message.message
        }

        Log.i("==@==", "==stringMessage is $stringMessage ==")

        // `LaunchedEffect` will cancel and re-launch if
        // `message` changes
        LaunchedEffect(state.message) {
            // Show snackbar using a coroutine, when the coroutine is cancelled the
            // snackbar will automatically dismiss. This coroutine will cancel whenever
            // `state.message` is null, and only start when `state.message` is not null
            // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
            scaffoldState.snackbarHostState.showSnackbar(
                message = stringMessage,
                actionLabel = "OK",
            )

            // Notify the view model that the message has been dismissed
            onMessageShown(message.id)
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackBarHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(Layout.bodyMargin)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        // content
        RegisterOtpPage(
            state = state,
            paddingValues = paddingValues,
            onChangedLanguage = onChangedLanguage,
            onSubmitPassword = onSubmitPassword,
        )
    }
}

@Composable
private fun RegisterOtpPage(
    state: ForgotPasswordViewState,
    paddingValues: PaddingValues,
    onChangedLanguage: (lang: String) -> Unit,
    onSubmitPassword: (otp: String, password: String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .bodyWidth()
                .padding(paddingValues)

        ) {

            IndicatorSection(state)

            Spacer(modifier = Modifier.weight(1f))

            SubmitOTPForm(
                state = state,
                onSubmitPassword = onSubmitPassword,
            )

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}



@Composable
private fun IndicatorSection(state: ForgotPasswordViewState){
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = state.loading,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }


}

@Composable
private fun LogoSection() {

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,

        ) {
        Image(
            painter = painterResource(id = R.drawable.logo_mhome),
            contentDescription = "Logo",
            alignment = Alignment.Center,
            modifier = Modifier.size(86.dp)

        )
    }

    Spacer(modifier = Modifier.height(24.dp))


    Text(
        text = stringResource(id = R.string.otp_verify),
        fontSize = 32.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun SubmitOTPForm(
    state: ForgotPasswordViewState,
    onSubmitPassword: (otp: String, password: String) -> Unit,
) {

    val otpValue = remember { mutableStateOf("") }

    val passwordValue = remember { mutableStateOf("") }

    val keyboardManager = LocalSoftwareKeyboardController.current

    val passwordText by remember { mutableStateOf(R.string.password) }

    val passwordHint by remember { mutableStateOf(R.string.password) }

    val otpHint by remember { mutableStateOf(R.string.title_forgot_password) }

    val passwordVisibility = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = Layout.bodyMargin)
            .verticalScroll(
                state = rememberScrollState(),
                reverseScrolling = true
            ),
    ) {

        LogoSection()

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = otpValue.value,
            onValueChange = { otpValue.value = it },
            label = { Text(text = "OTP") },
            placeholder = { Text(text = stringResource(id = otpHint)) },
            singleLine = true,
//            modifier = Modifier.fillMaxWidth(0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }

            )
        )


        OutlinedTextField(
            value = passwordValue.value,
            onValueChange = { passwordValue.value = it },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_eye_outlined_24),
                        contentDescription = null,
                        tint = if (passwordVisibility.value){
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                    )
                }
            },
            label = { Text(stringResource(id = passwordText)) },
            placeholder = { Text(text = stringResource(id = passwordHint)) },
            singleLine = true,
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardManager?.hide()

                    onSubmitPassword(
                        otpValue.value,
                        passwordValue.value,
                    )
                }
            )

        )


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            shape = RoundedCornerShape(4.dp),
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onSubmitPassword(
                    otpValue.value,
                    passwordValue.value,
                )
            }
        ) {
            Text(text = stringResource(id = R.string.verify).uppercase())
        }


    }
}



@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true, device = Devices.PIXEL_3)
@Composable
fun PreviewRegisterOtp() {
    RegisterOtpPage(
        state = ForgotPasswordViewState(loading = true),
        paddingValues = PaddingValues(8.dp),
        onChangedLanguage = {},
        onSubmitPassword = {
                otp, password ->
        },
    )
}
