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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.bodyWidth
import com.jimmy.common.compose.rememberStateWithLifecycle

@Composable
fun MainLogin(
    openLogin: () -> Unit,
    openSignup: () -> Unit,
    openTerms: () -> Unit,
) {

    Scaffold{ paddingValues ->
        // content
        WelcomePage(
            viewModel = hiltViewModel(),
            paddingValues = paddingValues,
            openLogin = openLogin,
            openSignup = openSignup,
            openTerms = openTerms,
        )
    }
}


@Composable
private fun WelcomePage(
    viewModel: LoginViewModel,
    paddingValues: PaddingValues,
    openLogin: () -> Unit,
    openSignup: () -> Unit,
    openTerms: () -> Unit,
) {

    val viewState by rememberStateWithLifecycle(viewModel.state)

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
                .navigationBarsPadding()
                .padding(paddingValues)
        ) {


            WelcomeForm(
                state = viewState,
                openLogin = openLogin,
                openSignup = openSignup,
                openTerms = openTerms,
                onChangedLanguage = {lang -> viewModel.updateLanguage(lang)}
            )


        }
    }
}


@Composable
private fun WelcomeForm(
    state: LoginViewState,
    openLogin: () -> Unit,
    openSignup: () -> Unit,
    openTerms: () -> Unit,
    onChangedLanguage: (String) -> Unit,
) {


    SetLanguage(state.language)


    val loginStringRes by remember { mutableStateOf(R.string.log_in) }
    val signUpStringRes by remember { mutableStateOf(R.string.sign_up) }

    val showDialog  = remember { mutableStateOf(false)}


    showDialog(
        showDialog = showDialog,
        onAccept = openLogin,
        onOpenTerms = openTerms,
    )

    Column(
        modifier = Modifier
            .padding(horizontal = Layout.bodyMargin)
    ) {


        LanguageSection(state, onChangedLanguage)

        Spacer(modifier = Modifier.height(24.dp))

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

        }

        Spacer(modifier = Modifier.weight(1f))


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showDialog.value = true
            },
            shape = RoundedCornerShape(4.dp)
        ) {

            Text(text = stringResource(id = loginStringRes).uppercase())
        }


        Spacer(modifier = Modifier.height(4.dp))

        Button(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = openSignup,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0XFFBCBCBC),
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        ) {

            Text(text = stringResource(id = signUpStringRes).uppercase())
        }


        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
private fun showDialog(
    showDialog: MutableState<Boolean>,
    onAccept: () -> Unit,
    onOpenTerms: () -> Unit,
){

    if(showDialog.value){
        PrivacyPolicyDialog(
            showDialog,
            onAccept = onAccept,
            onOpenTerms = onOpenTerms,
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyDialog(
    openDialog: MutableState<Boolean>,
    onAccept: () -> Unit,
    onOpenTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { openDialog.value = false}) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = modifier.padding(10.dp),
        ) {
            Column(
                modifier = modifier.background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                val title = R.string.dialog_privacy_policy
                val message = R.string.dialog_message_privacy_policy
                val viewDetails = R.string.dialog_privacy_policy

                val accept = R.string.dialog_accept
                val decline = R.string.dialog_decline

                Column(modifier = modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = title),
                        textAlign = TextAlign.Center,
                        modifier = modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(id = message),
                        textAlign = TextAlign.Start,
                        modifier = modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    TextButton(onClick = { onOpenTerms() }) {
                        Text(
                            text = stringResource(id = viewDetails),
                            textAlign = TextAlign.Start,
                            modifier = modifier
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                }


                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = {
                            openDialog.value = false
                        },
                    ) {
                        Text(
                            text = stringResource(id = decline),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    TextButton(
                        onClick = {
                            openDialog.value = false
                            onAccept()
                        },
                    ) {
                        Text(
                            text = stringResource(id = accept),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}




@Composable
private fun LanguageSection(
    state: LoginViewState,
    onChangedLanguage: (lang: String) -> Unit,
) {
    Spacer(modifier = Modifier.height(18.dp))

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



@Preview(device = Devices.PIXEL_C,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewWelcomeTablet() {
    WelcomeForm(
        state = LoginViewState(loading = true),
        onChangedLanguage = {},
        openTerms = {},
        openSignup = {},
        openLogin = {},
    )
}

@Preview(device = Devices.PIXEL_4,
    showBackground = true,
    backgroundColor = 0xffffff)
@Composable
fun PreviewWelcomePhone() {
    WelcomeForm(
        state = LoginViewState(loading = true),
        onChangedLanguage = {},
        openTerms = {},
        openSignup = {},
        openLogin = {},
    )
}