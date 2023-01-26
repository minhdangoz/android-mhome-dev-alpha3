package com.jimmy.settings.preferences

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.os.Process
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.OverflowMenu
import com.jimmy.settings.R
import com.jimmy.settings.Routes
import com.jimmy.settings.preferences.components.PreferenceLayout

@ExperimentalAnimationApi
@Composable
fun PreferencesDashboard() {

    PreferenceLayout(
        label = stringResource(id = R.string.settings),
        backArrowVisible = false,
        actions = {PreferencesOverflowMenu()}
    ) {

        PreferenceCategory(
            label = stringResource(R.string.settings_account),
            description = stringResource(R.string.settings_account),
            iconResource = R.drawable.ic_store,
            route = Routes.ACCOUNT
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_general),
            description = stringResource(R.string.settings_general),
            iconResource = R.drawable.ic_store,
            route = Routes.GENERAL
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_scheduled),
            description = stringResource(R.string.settings_scheduled),
            iconResource = R.drawable.ic_baseline_calendar_month_24,
            route = Routes.SCHEDULED
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_call),
            description = stringResource(R.string.settings_call),
            iconResource = R.drawable.ic_baseline_settings_phone_24,
            route = Routes.CALL
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_message),
            description = stringResource(R.string.settings_message),
            iconResource = R.drawable.ic_baseline_message_24,
            route = Routes.MESSAGE
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_feedback),
            description = stringResource(R.string.settings_feedback),
            iconResource = R.drawable.ic_baseline_feedback_24,
            route = Routes.FEEDBACK
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_user_manual),
            description = stringResource(R.string.settings_user_manual),
            iconResource = R.drawable.ic_baseline_library_books_24,
            route = Routes.USER_MANUAL
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_contact),
            description = stringResource(R.string.settings_contact),
            iconResource = R.drawable.ic_baseline_support_24,
            route = Routes.CONTACT
        )

        PreferenceCategory(
            label = stringResource(R.string.settings_about),
            description = stringResource(R.string.settings_about),
            iconResource = R.drawable.ic_baseline_info_24,
            route = Routes.ABOUT
        )


    }


}


@Composable
fun PreferencesOverflowMenu() {
//    val navController = LocalNavController.current
//    val enableDebug by preferenceManager().enableDebugMenu.observeAsState()
//    if (enableDebug) {
//        val resolvedRoute = subRoute(name = Routes.DEBUG_MENU)
//        ClickableIcon(
//            imageVector = Icons.Rounded.Build,
//            onClick = { navController.navigate(resolvedRoute) },
//        )
//    }
    OverflowMenu(
        imageVector = Icons.Outlined.MoreVert,
        block = {
            val context = LocalContext.current
            DropdownMenuItem(onClick = {
                openAppInfo(context)
                hideMenu()
            }) {
                Text(text = stringResource(id = R.string.app_info))
            }

            DropdownMenuItem(onClick = {
                hideMenu()
            }) {
                Text(text = stringResource(id = R.string.sign_out))
            }
        }

    )
}

private fun openAppInfo(context: Context) {
    val launcherApps = context.getSystemService<LauncherApps>()
    val componentName = ComponentName(context, "MainActivity")
    launcherApps?.startAppDetailsActivity(componentName, Process.myUserHandle(), null, null)
}
