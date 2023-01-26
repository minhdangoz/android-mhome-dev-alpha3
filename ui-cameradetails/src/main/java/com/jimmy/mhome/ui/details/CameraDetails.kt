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

package com.jimmy.mhome.ui.details

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.halfBodyWidth
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.AutoSizedCircularProgressIndicator
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import com.jimmy.mhome.ui.details.ptzcontrol.AbsDirectionCtrlView
import com.jimmy.mhome.ui.details.ptzcontrol.DirectionCtrlViewNew
import de.tavendo.autobahn.WebSocket
import io.antmedia.webrtcandroidframework.IDataChannelObserver
import io.antmedia.webrtcandroidframework.IWebRTCClient
import io.antmedia.webrtcandroidframework.IWebRTCListener
import io.antmedia.webrtcandroidframework.StreamInfo
import io.antmedia.webrtcandroidframework.WebRTCClient
import io.antmedia.webrtcandroidframework.apprtc.CallActivity
import org.webrtc.DataChannel
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import java.nio.charset.StandardCharsets

@Composable
fun CameraDetails(
    navigateUp: () -> Unit,
    openPlayback: (cameraId: String) -> Unit,
    openMonitor: (cameraId: String) -> Unit,
    openAlbum: (cameraId: String) -> Unit,
    openSettings: (cameraId: String) -> Unit,
) {
    CameraDetails(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
        openPlayback = openPlayback,
        openMonitor = openMonitor,
        openAlbum = openAlbum,
        openSettings = openSettings,
    )
}

@Composable
internal fun CameraDetails(
    viewModel: CameraDetailsViewModel,
    navigateUp: () -> Unit,
    openPlayback: (cameraId: String) -> Unit,
    openMonitor: (cameraId: String) -> Unit,
    openAlbum: (cameraId: String) -> Unit,
    openSettings: (cameraId: String) -> Unit,
) {
    val viewState by rememberStateWithLifecycle(viewModel.state)

    CameraDetails(
        viewModel = viewModel,
        viewState = viewState,
        navigateUp = navigateUp,
        refresh = { viewModel.refresh() },
        onMessageShown = { viewModel.clearMessage(it) },
        openPlayback = openPlayback,
        openMonitor = openMonitor,
        openAlbum = openAlbum,
        openSettings = openSettings,
    )
}

@Composable
internal fun CameraDetails(
    viewModel: CameraDetailsViewModel,
    viewState: CameraDetailsViewState,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    openPlayback: (cameraId: String) -> Unit,
    openMonitor: (cameraId: String) -> Unit,
    openAlbum: (cameraId: String) -> Unit,
    openSettings: (cameraId: String) -> Unit,
    onMessageShown: (id: Long) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    viewState.message?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
            // Notify the view model that the message has been dismissed
            onMessageShown(message.id)
        }
    }


    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        topBar = {
            var appBarHeight by remember { mutableStateOf(0) }
            val showAppBarBackground by remember {
                derivedStateOf {
                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    when {
                        visibleItemsInfo.isEmpty() -> false
                        appBarHeight <= 0 -> false
                        else -> {
                            val firstVisibleItem = visibleItemsInfo[0]
                            when {
                                // If the first visible item is > 0, we want to show the app bar background
                                firstVisibleItem.index > 0 -> true
                                // If the first item is visible, only show the app bar background once the only
                                // remaining part of the item is <= the app bar
                                else -> firstVisibleItem.size + firstVisibleItem.offset <= appBarHeight
                            }
                        }
                    }
                }
            }

            CameraDetailsAppBar(
                title = viewState.camera?.cameraName ?: "Camera",
                isRefreshing = viewState.refreshing,
                showAppBarBackground = showAppBarBackground,
                navigateUp = navigateUp,
                refresh = refresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { appBarHeight = it.height }
            )
        },
        bottomBar = {
            BottomAppBarView(
                state = viewState,
                openPlayback = openPlayback,
                openMonitor = openMonitor,
                openAlbum = openAlbum,
                openSettings = openSettings,
            )
        },
        snackbarHost = { snackBarHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(horizontal = Layout.bodyMargin)
                    .navigationBarsPadding()
                    .fillMaxWidth()
            )
        }
    ) {

        CameraDetailsContent(
            viewModel = viewModel,
            state = viewState,
            listState = listState,
            modifier = Modifier.fillMaxSize(),
            openPlayback = openPlayback,
        )
    }
}

@Composable
private fun CameraDetailsContent(
    viewModel: CameraDetailsViewModel,
    state: CameraDetailsViewState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    openPlayback: (cameraId: String) -> Unit,
    scrollState: ScrollableState = rememberScrollState()
) {

    Column(
        modifier = modifier.scrollable(
            state = scrollState,
            orientation = Orientation.Vertical
        )
    ){

        Column(modifier = modifier.weight(1f)){
            LiveCameraView(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
//                    .aspectRatio(4 / 3f)
//                    .clipToBounds()
            )
        }

        Column(modifier = modifier.weight(1f)){

            PtzControlView(
                viewModel = viewModel,
                state = state,
            )
        }

    }
}


@Composable
private fun PtzControlView(
    viewModel: CameraDetailsViewModel,
    state: CameraDetailsViewState,
    modifier: Modifier = Modifier,
){
    val TAG = "==PtzControlView=="

    AndroidView(factory = { context ->
        DirectionCtrlViewNew(context).apply {
            setDisable(true)
            setSupportCenterClick(true)

//            layoutParams =  LinearLayout.LayoutParams(150, 150)



            this.setOnDirectionCtrlListener(object: AbsDirectionCtrlView.OnDirectionCtrlListener{
                override fun onActionDown() {
                }

                override fun onActionUp(z: Boolean) {
                }

                override fun onCenterClick() {
                }

                /**
                 * 1: LEFT
                 * 2: RIGHT
                 * 3: TOP
                 * 4: BOT
                 * */
                override fun onClickPTZDirection(direction: Int) {
                    Log.i(TAG, "--> onClickPTZDirection: $direction")
//                    viewModel.handlePtzEvent()
                }
            })

        }


    },
        modifier = modifier.halfBodyWidth()
    )

}


@Composable
private fun BottomAppBarView(
    state: CameraDetailsViewState,
    modifier: Modifier = Modifier,
    openPlayback: (cameraId: String) -> Unit,
    openMonitor: (cameraId: String) -> Unit,
    openAlbum: (cameraId: String) -> Unit,
    openSettings: (cameraId: String) -> Unit,
){

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = modifier.padding(
            rememberInsetsPaddingValues(
                LocalWindowInsets.current.navigationBars,
                applyEnd = false
            )
        ),
        icons = {
            Row(
                modifier = Modifier.weight(4f),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                IconButton(onClick = { /* doSomething() */ }) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Icon(
                            imageVector = Icons.Outlined.DoorBack,
                            contentDescription = "Monitor",
                        )
                        Text(
                            text = stringResource(id = R.string.monitor),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                }

                IconButton(
                    onClick = {
                        println("--> open playback")
                        state.cameraId?.let{
                            openPlayback(it)
                        }
                    },

                    ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Icon(Icons.Outlined.SlowMotionVideo,
                            contentDescription = "Playback",
                        )
                        Text(
                            text = stringResource(id = R.string.playback),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                }


                IconButton(
                    onClick = {
                        println("--> openAlbum")

                        state.cameraId?.let{
                            openAlbum(it)
                        }
                    }
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "Album",
                        )
                        Text(
                            text = stringResource(id = R.string.album),
                            style = MaterialTheme.typography.labelMedium
                        )

                    }

                }

                IconButton(
                    onClick = {
                        println("--> openSettings")

                    }) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface,)
                        Text(
                            text = stringResource(id = R.string.settings),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }


                }
            }

        },
        floatingActionButton = {
            // TODO(b/228588827): Replace with Secondary FAB when available.
            FloatingActionButton(
                onClick = { /*TODO*/ },
                elevation = BottomAppBarDefaults.floatingActionButtonElevation()
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = "Microphone",
                    tint = MaterialTheme.colorScheme.onSurface,
                )

            }
        }
    )

}


val openDialog = mutableStateOf(false)

@Composable
private fun LiveCameraView(
    state: CameraDetailsViewState,
    modifier: Modifier = Modifier,
) {


    Surface(modifier = modifier.padding(bottom = Layout.bodyMargin)) {
        Box {

            val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

            // Show dialog
            if (openDialog.value) {
                CustomDialog(openDialogCustom = openDialog)
            }

            val context = LocalContext.current
            val SERVER_ADDRESS = "edge.mcloudcam.vn"
            val webRTCMode = IWebRTCClient.MODE_PLAY
            val enableDataChannel = true
            val SERVER_URL = "wss://$SERVER_ADDRESS/MobifoneCloudCamProd/websocket"
            val webRTCListener = IWebRTCListenerImpl(context)
            val dataChannelObserver = IDataChannelObserverImpl()


            val webRTCClient = WebRTCClient(webRTCListener, context)


            val tokenId = state.camera?.token
            val streamId = state.camera?.rtmpStreamId
            val active = state.camera?.active
            val liveStream = state.camera?.liveStream

            println("-->tokenId = $tokenId<--")
            println("-->streamId = $streamId<--")
            println("-->active = $liveStream<--")

            if(tokenId != null && streamId != null) {

                AndroidView(
                    { context ->
                        LayoutInflater.from(context).inflate(
                            R.layout.live_camera_frame, FrameLayout(context), false
                        ).apply {

                            val cameraViewRenderer =
                                this.findViewById<SurfaceViewRenderer>(R.id.camera_view_renderer)

                            val pipViewRenderer =
                                this.findViewById<SurfaceViewRenderer>(R.id.pip_view_renderer)

                            val intent = Intent()
                            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, true)
                            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, 30)
                            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, 1500)
                            intent.putExtra(
                                CallActivity.EXTRA_DATA_CHANNEL_ENABLED,
                                enableDataChannel
                            )

                            webRTCClient.setVideoRenderers(pipViewRenderer, cameraViewRenderer)
                            webRTCClient.switchVideoScaling(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED)

                            webRTCClient.init(
                                SERVER_URL,
                                streamId,
                                webRTCMode,
                                tokenId,
                                intent,
                            )

                            webRTCClient.setDataChannelObserver(dataChannelObserver)

                            if (tokenId.isNotEmpty()) {

                                openDialog.value = false

                                println("-->got token<--")

                                // intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, 24);

                                if (!webRTCClient.isStreaming) {
                                    println("-->start playing <--")

                                    webRTCClient.startStream()
                                    if (webRTCMode === IWebRTCClient.MODE_JOIN) {
                                        pipViewRenderer.setZOrderOnTop(true)
                                    }
                                } else {
                                    webRTCClient.stopStream()
                                }
                            }

                            val observer = LifecycleEventObserver { _, event ->
                                if (event == Lifecycle.Event.ON_PAUSE) {

                                    println("--->Lifecycle ON_PAUSE<--")
                                } else if (event == Lifecycle.Event.ON_STOP) {

                                    println("--->Lifecycle ON_STOP stop stream<--")
                                    webRTCClient.stopStream()
                                }
                            }

                            // Add the observer to the lifecycle
                            lifecycleOwner.lifecycle.addObserver(observer)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )


                Text(
                    text = state.camera.cameraName,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Layout.gutter)
                )
            }


            if(liveStream != null && !liveStream
                && active != null && !active){
                DeviceOffline()
            }

        }
    }
}

@Composable
fun DeviceOffline(){
    val deviceOffline = stringResource(id = R.string.dialog_device_offline)

    val originalTextStyle = MaterialTheme.typography.bodyMedium

    val shadowSize = with(LocalDensity.current) {
        originalTextStyle.fontSize.toPx() / 16
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ){
        Text(
            text = deviceOffline,
            style = originalTextStyle.copy(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(shadowSize, shadowSize),
                )
            ),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(Layout.gutter * 2)
        )

    }
}

@Composable
fun CustomDialog(openDialogCustom: MutableState<Boolean>) {
    Dialog(onDismissRequest = { openDialogCustom.value = false}) {
        CustomDialogUI(openDialogCustom = openDialogCustom)
    }
}


//Layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    openDialogCustom: MutableState<Boolean>
){
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp),
    ) {
        Column(
            modifier = modifier.background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val deviceOffline = R.string.dialog_device_offline
            val offlineTip = R.string.offline_help_tip
            val backToHome = R.string.dialog_back_to_home
            val viewDetails = R.string.dialog_view_details

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){

                IconButton(onClick = { openDialogCustom.value = false }) {
                    Image(
                        painter = painterResource(id = R.drawable.dialog_camera_helper_close),
                        contentDescription = "close dialog",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(15.dp),
                    )
                }
            }

            Image(
                painter = painterResource(id = com.jimmy.common.compose.R.drawable.ic_camera_360),
                contentDescription = "camera",
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .size(68.dp),
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.Center
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = deviceOffline),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(id = offlineTip),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            //.......................................................................
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

//                TextButton(
//                    onClick = {
//                        openDialogCustom.value = false
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                        .padding(horizontal = Layout.bodyMargin)
//
//                ) {
//
//                    Text(
//                        text = stringResource(id = viewDetails),
//                        fontWeight = FontWeight.Light,
//                        fontSize = 10.sp,
//                        color = MaterialTheme.colorScheme.onSurface,
//                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
//                    )
//                }

                OutlinedButton(
                    onClick = { openDialogCustom.value = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Layout.bodyMargin)
                ) {
                    Text(
                        text = stringResource(id = backToHome),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}


@Composable
private fun CameraDetailsAppBar(
    title: String?,
    isRefreshing: Boolean,
    showAppBarBackground: Boolean,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val backgroundColor by animateColorAsState(
        targetValue = when {
            showAppBarBackground -> MaterialTheme.colorScheme.surface
            else -> Color.Transparent
        },
        animationSpec = spring(),
    )

    val elevation by animateDpAsState(
        targetValue = when {
            showAppBarBackground -> 4.dp
            else -> 0.dp
        },
        animationSpec = spring(),
    )

    TopAppBar(
        title = {
            Crossfade(showAppBarBackground && title != null) { show ->
                if (show) Text(text = title!!)
            }
        },
        contentPadding = rememberInsetsPaddingValues(
            LocalWindowInsets.current.systemBars,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
//                modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_up),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            if (isRefreshing) {
                AutoSizedCircularProgressIndicator(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .padding(16.dp)
                )
            } else {
                IconButton(
                    onClick = refresh,
//                    modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.cd_refresh)
                    )
                }
            }
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}



class IWebRTCListenerImpl(val context: Context) : IWebRTCListener {

    override fun onDisconnected(streamId: String?) {
    }

    override fun onPublishFinished(streamId: String?) {
    }

    override fun onPlayFinished(streamId: String?) {
    }

    override fun onPublishStarted(streamId: String?) {
    }

    override fun onPlayStarted(streamId: String?) {

//        webRTCClient.switchVideoScaling(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
//        webRTCClient!!.getStreamInfoList()

        Toast.makeText(context, "Play started", Toast.LENGTH_LONG).show()
    }

    override fun noStreamExistsToPlay(streamId: String?) {
        println("-->noStreamExistsToPlay<--")
        Toast.makeText(context, "noStreamExistsToPlay", Toast.LENGTH_LONG).show()

        openDialog.value = true
    }

    override fun onError(description: String?, streamId: String?) {
        Toast.makeText(context, "Error: $description", Toast.LENGTH_LONG).show()

        openDialog.value = true
    }

    override fun onSignalChannelClosed(
        code: WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification?,
        streamId: String?
    ) {
    }

    override fun streamIdInUse(streamId: String?) {
    }

    override fun onIceConnected(streamId: String?) {
    }

    override fun onIceDisconnected(streamId: String?) {
    }

    override fun onTrackList(tracks: Array<out String>?) {
    }

    override fun onBitrateMeasurement(
        streamId: String?,
        targetBitrate: Int,
        videoBitrate: Int,
        audioBitrate: Int
    ) {
    }

    override fun onStreamInfoList(streamId: String?, streamInfoList: ArrayList<StreamInfo>?) {
    }
}


class IDataChannelObserverImpl: IDataChannelObserver {
    override fun onBufferedAmountChange(previousAmount: Long, dataChannelLabel: String) {
    }

    override fun onStateChange(state: DataChannel.State, dataChannelLabel: String) {
    }

    override fun onMessage(buffer: DataChannel.Buffer, dataChannelLabel: String) {
        val data = buffer.data
        val messageText = String(data.array(), StandardCharsets.UTF_8)
        println("-->$messageText<--")
    }

    override fun onMessageSent(buffer: DataChannel.Buffer, successful: Boolean) {
    }
}



//
//
//@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
//@Composable
//fun previewCameraDetails(){
//
//    CameraDetails(
//        viewState = CameraDetailsViewState(),
//        navigateUp = {},
//        onMessageShown = {},
//        refresh = {},
//        openPlayback = {},
//    )
//}
//
//@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
//@Composable
//fun previewCameraDetailsTablet(){
//    CameraDetails(
//        viewState = CameraDetailsViewState(),
//        navigateUp = {},
//        onMessageShown = {},
//        refresh = {},
//        openPlayback = {},
//
//        )
//}