package com.iamashad.ashad_swipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iamashad.ashad_swipe.ui.theme.Ashad_SwipeTheme
import com.iamashad.ashad_swipe.userinterface.list.ProductListScreen
import kotlinx.coroutines.launch

/**
 * Main activity hosting the product list UI.
 * - Controls splash visibility until initial data is ready.
 * - Observes theme preference from DataStore.
 * - Displays dynamic product list screen.
 */
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash before super.onCreate()
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition { !appViewModel.isAppReady.value }
        splash.setOnExitAnimationListener { provider ->
            provider.view.animate()
                .alpha(0f)
                .setDuration(160L)
                .withEndAction { provider.remove() }
                .start()
        }

        // Warm up app (DataStore read, theme preload)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appViewModel.warmUpIfNeeded()
            }
        }

        setContent {
            val dynamicColor by appViewModel.dynamicColor.collectAsState()

            Ashad_SwipeTheme(dynamicColor = dynamicColor) {
                ProductListScreen(
                    dynamicColor = dynamicColor,
                    onToggleDynamicColor = { appViewModel.setDynamicColor(it) }
                )
            }
        }
    }
}
