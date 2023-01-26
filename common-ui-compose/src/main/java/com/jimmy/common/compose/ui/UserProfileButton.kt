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

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jimmy.common.compose.R

@Composable
fun UserProfileButton(
    loggedIn: Boolean,
    user: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
//        when {
//            loggedIn && user?.avatarUrl != null -> {
//                Image(
//                    painter = rememberImagePainter(user.avatarUrl!!) {
//                        crossfade(true)
//                    },
//                    contentDescription = stringResource(R.string.cd_profile_pic, user.name),
//                    modifier = Modifier
//                        .size(32.dp)
//                        .clip(CircleShape),
//                )
//            }
//            else -> {
//                Icon(
//                    imageVector = when {
//                        loggedIn -> Icons.Default.Person
//                        else -> Icons.Outlined.Person
//                    },
//                    contentDescription = stringResource(R.string.cd_user_profile)
//                )
//            }
//        }
        Icon(
            imageVector = when {
                loggedIn -> Icons.Default.Person
                else -> Icons.Outlined.Person
            },
            contentDescription = stringResource(R.string.cd_user_profile)
        )
    }
}
