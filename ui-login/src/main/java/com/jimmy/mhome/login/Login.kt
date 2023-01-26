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

package com.jimmy.mhome.login

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.bodyWidth
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import com.jimmy.datasource.AppAuthState
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Login(
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    openMainScreen: () -> Unit,
) {
    Login(
        viewModel = hiltViewModel(),
        openRegister = openRegister,
        openForgotPassword = openForgotPassword,
        openMainScreen = openMainScreen,
    )
}

@Composable
private fun Login(
    viewModel: LoginViewModel,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    openMainScreen: () -> Unit,
) {

    val viewState by rememberStateWithLifecycle(viewModel.state)

//    val activity = LocalContext.current as Activity

    Login(
        state = viewState,
        openRegister = openRegister,
        openForgotPassword = openForgotPassword,
        openHomeScreen = {
            openMainScreen()
        },
        onMessageShown = { viewModel.clearMessage(it) },
        onLoginButtonClicked = { phone, password ->
            viewModel.requestLogin(phone, password)
        },
        onChangedLanguage = {
                lang ->  viewModel.updateLanguage(lang)
        },
        clearAuthState = {viewModel.clearAuthState()},
    )
}

@Composable
private fun Login(
    state: LoginViewState,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    openHomeScreen: () -> Unit,
    clearAuthState: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
    onLoginButtonClicked: (phone: String, password: String) -> Unit,
    onChangedLanguage: (lang: String) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

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


    if(state.authState == AppAuthState.LOGGED_IN){
        LaunchedEffect(key1 = state.authState){
            openHomeScreen()
            clearAuthState()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackBarHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(horizontal = Layout.bodyMargin)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        // content
        LoginPage(
            state = state,
            paddingValues = paddingValues,
            openRegister = openRegister,
            openForgotPassword = openForgotPassword,
            onLoginButtonClicked = onLoginButtonClicked,
            onChangedLanguage = onChangedLanguage,
        )
    }
}

@Composable
private fun LoginPage(
    state: LoginViewState,
    paddingValues: PaddingValues,
    openRegister: () -> Unit,
    openForgotPassword: () -> Unit,
    onLoginButtonClicked: (phone: String, password: String) -> Unit,
    onChangedLanguage: (lang: String) -> Unit,
) {


    SetLanguage(state.language)

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background_register_noise),
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = stringResource(id = R.string.cd_poster_image),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .bodyWidth()
                .fillMaxHeight()
                .padding(paddingValues)
                .navigationBarsPadding()

            ) {

            IndicatorSection(state)

            LanguageSection(state, onChangedLanguage = onChangedLanguage)

            Spacer(modifier = Modifier.weight(1f))

            LoginForm(
                state = state,
                onForgotButtonClicked = openForgotPassword,
                onLoginButtonClicked = { phone, password ->
                    onLoginButtonClicked(phone, password)
                },
//                onChangedLanguage = onChangedLanguage,
            )

            Spacer(modifier = Modifier.weight(1f))

            RegisterSection(
                onSignUpButtonClicked = openRegister
            )

        }
    }
}

@Composable
fun SetLanguage(locale: String) {
    val locale = Locale(locale)
    val configuration = LocalConfiguration.current
    configuration.setLocale(locale)
    val resources = LocalContext.current.resources
    resources.updateConfiguration(configuration, resources.displayMetrics)
    LocalContext.current.createConfigurationContext(configuration)
}


@Composable
private fun LanguageSection(
    state: LoginViewState,
    onChangedLanguage: (lang: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentLang = state.language
        val languageName =
            if(currentLang == "en"){
                "English"
            } else{
                "Tiếng Việt"
            }

        var expanded by remember { mutableStateOf(false) }

        TextButton(
            modifier = Modifier.wrapContentWidth(),
            onClick = { expanded = true },

            ) {
            Text(text = languageName, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.onPrimary
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onChangedLanguage("en")

                },
                    text = {
                        Text("English")
                    }
                )

                DropdownMenuItem(onClick = {
                    expanded = false
                    onChangedLanguage("vi")
                },
                    text = {
                        Text("Tiếng Việt")
                    }
                )
            }
        }

    }
}


@Composable
private fun IndicatorSection(state: LoginViewState){
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun LoginForm(
    state: LoginViewState,
    onLoginButtonClicked: (phone: String, password: String) -> Unit,
    onForgotButtonClicked: () -> Unit,
) {

    val phoneValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    val phoneStringRes by remember { mutableStateOf(R.string.phone_number) }
    val passStringRes by remember { mutableStateOf(R.string.password) }
    val forgotStringRes by remember { mutableStateOf(R.string.forgot_password_ask) }
    val loginStringRes by remember { mutableStateOf(R.string.log_in) }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = Layout.bodyMargin)
            .verticalScroll(
                state = rememberScrollState(),
                reverseScrolling = true
            ),
        ) {

//        LanguageSection(state, onChangedLanguage)

        Column{
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
                color = MaterialTheme.colorScheme.onPrimary,
                text = stringResource(id = loginStringRes),
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                placeholderColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
            value = phoneValue.value,
            onValueChange = { phoneValue.value = it },
            label = { Text(text = stringResource(id = phoneStringRes),
                color = MaterialTheme.colorScheme.inverseOnSurface) },
            placeholder = {
                Text(
                    text = stringResource(id = phoneStringRes),
                    color = Color(0xFFBCBCBC)
                )
            },
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
                },
            keyboardOptions = KeyboardOptions(
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                placeholderColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
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
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }

                    )
                }
            },
            label = { Text(stringResource(id = passStringRes),
                color = MaterialTheme.colorScheme.inverseOnSurface) },
            placeholder = { Text(text = stringResource(id = passStringRes),
                color = Color(0xFFBCBCBC)) },
            singleLine = true,
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .fillMaxWidth()
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

                    onLoginButtonClicked(
                        phoneValue.value,
                        passwordValue.value,
                    )
                }
            )

        )


        Spacer(modifier = Modifier.height(12.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = { onForgotButtonClicked() },
            ) {
                Text(stringResource(
                    id = forgotStringRes),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            onClick = {
                onLoginButtonClicked(
                    phoneValue.value,
                    passwordValue.value,
                )
            }
        ) {

            Text(text = stringResource(id = loginStringRes).uppercase(),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }


    }
}

@Composable
private fun RegisterSection(onSignUpButtonClicked: () -> Unit) {
    val signUpRes by remember { mutableStateOf(R.string.sign_up)}

    Column{
        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.do_not_have_an_account),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            TextButton(
                onClick = { onSignUpButtonClicked() }
            ) {
                Text(
                    text = stringResource(id = signUpRes),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }

}


@Preview(device = Devices.PIXEL_C,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewLoginPageTablet() {
    LoginPage(
        state = LoginViewState(loading = true),
        paddingValues = PaddingValues(8.dp),
        openRegister = {},
        openForgotPassword = {},
        onLoginButtonClicked = {
                _, _ ->
        },
        onChangedLanguage = {}
    )
}

@Preview(device = Devices.PIXEL_4,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewLoginPagePhone() {
    LoginPage(
        state = LoginViewState(loading = true),
        paddingValues = PaddingValues(8.dp),
        openRegister = {},
        openForgotPassword = {},
        onLoginButtonClicked = {
                _, _ ->
        },
        onChangedLanguage = {}
    )
}