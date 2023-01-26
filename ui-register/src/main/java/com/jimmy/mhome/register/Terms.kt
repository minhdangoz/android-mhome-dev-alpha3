package com.jimmy.mhome.register

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.jimmy.common.compose.bodyWidth
import kotlin.math.roundToInt

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Terms() {

    val state = rememberWebViewState("https://mobihome.pro")
    val visibility = remember { mutableStateOf(true)}
    val progress = remember { mutableStateOf(0.0F)}

    val webViewClient = object: AccompanistWebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            visibility.value = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            visibility.value = false
        }
    }

    val chromeClient = object: AccompanistWebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progress.value = newProgress.toFloat()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ){
                if (visibility.value){
                    CircularProgressIndicator(
                    )
                    Text(
                        text = "${progress.value.roundToInt()}%",
                        fontWeight = FontWeight.Bold
                    )
                }

            }

            WebView(
                state,
                onCreated = { it.settings.javaScriptEnabled = true },
                captureBackPresses = false,
                client = webViewClient,
                chromeClient = chromeClient,
            )


        }



    }

}