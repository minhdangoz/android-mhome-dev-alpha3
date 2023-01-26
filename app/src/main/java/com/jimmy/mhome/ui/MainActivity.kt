package com.jimmy.mhome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.firebase.FirebaseApp
import com.jimmy.BaseActivity
import com.jimmy.ContentViewSetter
import com.jimmy.common.compose.LocalDateFormatter
import com.jimmy.common.compose.shouldUseDarkColors
import com.jimmy.common.compose.theme.JAppTheme
import com.jimmy.mhome.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.jimmy.mhome.R
import com.jimmy.settings.AppPreferences
import com.jimmy.util.AppDateFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    internal lateinit var contentViewSetter: ContentViewSetter

    @Inject internal lateinit var appDateFormatter: AppDateFormatter

    @Inject
    internal lateinit var preferences: AppPreferences

    private val viewModel: UserViewModel by viewModels()

    companion object {
        const val splashFadeDurationMillis = 600
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val splashWasDisplayed = savedInstanceState != null
        if (!splashWasDisplayed) {
            val splashScreen = installSplashScreen()
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                // Get icon instance and start a fade out animation
                splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .withEndAction {
                        // After the fade out, remove the splash and set content view
                        splashScreenViewProvider.remove()
                        setContent {
                            setAppContent()
                        }
                    }.start()
            }

        } else {
            setTheme(R.style.Theme_MHome_NoActionBar)
            setContent {
                setAppContent()
            }
        }

        FirebaseApp.initializeApp(this)

    }

    @Composable
    private fun setAppContent() {


        val authState = viewModel.isAuthenticated()
        val composeView = ComposeView(this).apply {
            setContent {
                MainScreen(authState)
            }
        }

        // Copied from setContent {} ext-fun
        setOwners()
        contentViewSetter.setContentView(this, composeView)
    }

    @Composable
    private fun MainScreen(authState: Boolean) {
        CompositionLocalProvider(
            LocalDateFormatter provides appDateFormatter,
        ) {
            ProvideWindowInsets(
                consumeWindowInsets = false,
                windowInsetsAnimationsEnabled = true) {
                JAppTheme(
                    isDarkTheme = preferences.shouldUseDarkColors()
                ) {
                    Main(authState)
                }
            }
        }

    }

}

private fun ComponentActivity.setOwners() {
    val decorView = window.decorView
    if (ViewTreeLifecycleOwner.get(decorView) == null) {
        ViewTreeLifecycleOwner.set(decorView, this)
    }
    if (ViewTreeViewModelStoreOwner.get(decorView) == null) {
        ViewTreeViewModelStoreOwner.set(decorView, this)
    }

    if (decorView.findViewTreeSavedStateRegistryOwner() == null) {
        decorView.setViewTreeSavedStateRegistryOwner(this)
    }
}
