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

package com.jimmy.mhome.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
fun Register(
    openLogin: () -> Unit,
    onTermsClicked: () -> Unit,
    openOtp: () -> Unit,
) {
    Register(
        viewModel = hiltViewModel(),
        openLogin = openLogin,
        onTermsClicked = onTermsClicked,
        openOtp = openOtp,
    )
}

@Composable
private fun Register(
    viewModel: RegisterViewModel,
    openLogin: () -> Unit,
    onTermsClicked: () -> Unit,
    openOtp: () -> Unit,

    ) {

    val viewState by rememberStateWithLifecycle(viewModel.state)

    Register(
        state = viewState,
        openLogin = openLogin,
        onMessageShown = { viewModel.clearMessage(it) },
        onRegisterClicked = { phone ->
            viewModel.getOtpCode(phone)
        },
        onChangedLanguage = {lang ->  viewModel.updateLanguage(lang)},
        onTermsClicked = onTermsClicked,
        openOtp = openOtp,
        clearAuthState = {viewModel.clearAuthState()},
    )
}

@Composable
private fun Register(
    state: RegisterViewState,
    openLogin: () -> Unit,
    onTermsClicked: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
    onRegisterClicked: (phone: String) -> Unit,
    onChangedLanguage: (lang: String) -> Unit,
    openOtp: () -> Unit,
    clearAuthState: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    if(state.authState == AppAuthState.REGISTERED){
        LaunchedEffect(key1 = state.authState){
            openOtp()
            clearAuthState()
        }
    }

    state.message?.let { message ->
        val stringMessage : String = if(message.stringResource > 0){
            stringResource(id = message.stringResource)
        } else{
            message.message
        }

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

    SetLanguage(state.language)

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
        RegisterPage(
            state = state,
            paddingValues = paddingValues,
            openLogin = openLogin,
            onChangedLanguage = onChangedLanguage,
            onRegisterClicked = { phone ->
                onRegisterClicked(phone)
            },
            onTermsClicked = onTermsClicked,
        )
    }
}

@Composable
private fun RegisterPage(
    state: RegisterViewState,
    paddingValues: PaddingValues,
    openLogin: () -> Unit,
    onRegisterClicked: (phone: String) -> Unit,
    onChangedLanguage: (lang: String) -> Unit,
    onTermsClicked: () -> Unit,
) {
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
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {

            IndicatorSection(state)

            LanguageSection(state = state,
                onChangedLanguage = onChangedLanguage)

            Spacer(modifier = Modifier.weight(1f))

            RegisterForm(
                state = state,
                onRegisterButtonClicked = { phone ->
                    onRegisterClicked(phone)
                },
                onTermsClicked = onTermsClicked,
            )

            Spacer(modifier = Modifier.weight(1f))

            SignInSection(
                onLoginClicked = openLogin
            )
        }
    }
}


@Composable
private fun LanguageSection(
    state: RegisterViewState,
    onChangedLanguage: (lang: String) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth().statusBarsPadding(),
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
            Text(
                text = languageName,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.onPrimary,
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    onChangedLanguage("en")

                },
                    text = {Text("English")})

                DropdownMenuItem(onClick = {
                    expanded = false
                    onChangedLanguage("vi")
                },
                    text = { Text("Tiếng Việt")})
            }
        }

    }
}

@Composable
private fun SignInSection(onLoginClicked: () -> Unit) {
    val signUpRes by remember { mutableStateOf(R.string.log_in)}

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
            Text(stringResource(id = R.string.already_have_an_account),
                color = MaterialTheme.colorScheme.onPrimary)
            TextButton(onClick = { onLoginClicked() }
            ) {
                Text(
                    text = stringResource(id = signUpRes),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun IndicatorSection(state: RegisterViewState){
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
private fun RegisterForm(
    state: RegisterViewState,
    onRegisterButtonClicked: (phone: String) -> Unit,
    onTermsClicked: () -> Unit,
    scrollableState: ScrollState = rememberScrollState(),
) {

    val phoneValue = remember { mutableStateOf("") }
    val signUpString by remember { mutableStateOf(R.string.sign_up)}

    val phoneStringRes by remember { mutableStateOf(R.string.phone_number) }
    val getOtpString by remember { mutableStateOf(R.string.get_otp) }
    val keyboardManager = LocalSoftwareKeyboardController.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = Layout.bodyMargin)
            .verticalScroll(
                state = scrollableState,
                reverseScrolling = true
            ),

    ) {

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
                text = stringResource(id = signUpString),
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimary,
                placeholderColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
            value = phoneValue.value,
            onValueChange = {
                phoneValue.value = it
            },
            label = {
                Text(
                    text = stringResource(id = phoneStringRes),
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            },
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
                }.focusTarget(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Phone
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onRegisterButtonClicked(phoneValue.value)

                    keyboardManager?.hide()
                }
            )
        )


        Spacer(modifier = Modifier.height(24.dp))

        AcceptTerms(state, onTermsClicked)

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            shape = RoundedCornerShape(4.dp),
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onRegisterButtonClicked(
                    phoneValue.value,
                )

                keyboardManager?.hide()

            }
        ) {
            Text(text = stringResource(id = getOtpString))
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcceptTerms(
    state: RegisterViewState,
    onTermsClicked: () -> Unit
){
    val isChecked = state.acceptedTerms
    val accepted = isChecked.collectAsState().value
    val acceptTerms by remember { mutableStateOf(R.string.register_term) }
    val termsOfService by remember { mutableStateOf(R.string.register_term_content) }


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = accepted,
            onCheckedChange = {
                isChecked.value = it
            },
            enabled = true,
        )

        Text(stringResource(id = acceptTerms),
            color = MaterialTheme.colorScheme.onPrimary)
        TextButton(
            onClick = { onTermsClicked() },
        ) {
            Text(stringResource(id = termsOfService),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,)
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

@Preview(device = Devices.PIXEL_C,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewRegisterPageTablet() {
    RegisterPage(
        state = RegisterViewState(loading = true),
        paddingValues = PaddingValues(8.dp),
        openLogin = {},
        onRegisterClicked = {
                phone ->
        },
        onChangedLanguage = {},
        onTermsClicked = {}
    )
}

@Preview(device = Devices.PIXEL_4,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewRegisterPagePhone() {
    RegisterPage(
        state = RegisterViewState(loading = true),
        paddingValues = PaddingValues(8.dp),
        openLogin = {},
        onRegisterClicked = {
                phone ->
        },
        onChangedLanguage = {},
        onTermsClicked = {}
    )
}


