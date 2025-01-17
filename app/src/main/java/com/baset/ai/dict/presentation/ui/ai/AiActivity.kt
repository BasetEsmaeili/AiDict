package com.baset.ai.dict.presentation.ui.ai

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.baset.ai.dict.presentation.service.OverlayServiceManager
import com.baset.ai.dict.presentation.ui.core.theme.AiDictTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AiActivity : ComponentActivity() {

    private val viewModel by viewModel<AiViewModel> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.onExtrasRetrieved(intent.extras)
        OverlayServiceManager(
            this,
            viewModel.windowServiceEnabledPreferenceState
        )
        setContent {
            val viewModel = remember {
                viewModel
            }
            val onOutSideClicked = remember {
                {
                    finish()
                }
            }
            AiDictTheme {
                AiRoute(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    onOutSideClicked = onOutSideClicked
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onExtrasRetrieved(intent.extras)
    }
}