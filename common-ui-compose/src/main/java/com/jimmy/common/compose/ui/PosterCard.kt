/*
 * Copyright 2021 Google LLC
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

package com.jimmy.common.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jimmy.common.compose.R
import com.jimmy.common.compose.theme.Orange40
import com.jimmy.datasource.entities.CameraEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterCard(
    show: CameraEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
    ) {
        Box(
            modifier =
            if (onClick != null)
                Modifier.clickable(onClick = onClick)
            else
                Modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_camera_360),
                    contentDescription = "camera",
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .size(68.dp),
                    contentScale = ContentScale.FillHeight,
                )


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = show.cameraName?.take(10) ?: "Camera",
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = show.boxName ?: "Box name",
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Row(){
//                        Text(
//                            text = "Living room",
//                            modifier = Modifier.padding(4.dp),
//                            fontSize = 10.sp,
//                            fontWeight = FontWeight.Light
//                        )
//                        Divider(
//                            modifier = Modifier
//                            .fillMaxHeight()
//                            .width(1.dp),
//
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
//                        )
                        Text(
                            text = "Device is offline",
                            modifier = Modifier.padding(4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light,
                            color = Orange40
                        )
                    }


                }
            }



        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderPosterCard(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Box {
            // TODO: display something better
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun PreviewPosterCard(){

    PosterCard(
        show = CameraEntity(
            cameraName = "TP-IPC",
            resolution = "720P",
            groupName = "Group name",
        )
    )
}
