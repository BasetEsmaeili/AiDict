package com.baset.anki.pro.generator.presentation.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.baset.anki.pro.generator.R
import com.baset.anki.pro.generator.presentation.service.OverlayServiceManager
import com.baset.anki.pro.generator.presentation.ui.core.model.UiText
import com.baset.anki.pro.generator.presentation.ui.core.theme.AnkiProCardGeneratorTheme
import com.baset.anki.pro.generator.presentation.util.canDrawOverlays
import com.baset.anki.pro.generator.presentation.util.isNecessaryPermissionsAvailable
import com.baset.anki.pro.generator.presentation.util.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel> { parametersOf(this) }
    private val necessaryPermissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results.all { it.value } && canDrawOverlays) {
                startOverlayServiceManager()
            }
        }
    private val drawOverlaysPermissionActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && isNecessaryPermissionsAvailable) {
                startOverlayServiceManager()
                return@registerForActivityResult
            }
            toast(Toast.LENGTH_LONG) {
                UiText.StringResource(R.string.message_accept_necessary_permissions)
            }
            necessaryPermissionsResult.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissionsIfNecessary {
            startOverlayServiceManager()
        }
        setContent {
            val mainViewModel = remember { viewModel }
            AnkiProCardGeneratorTheme {
                MainRoute(
                    modifier = Modifier.fillMaxSize(),
                    mainViewModel = mainViewModel
                )
            }
        }
    }

    private inline fun requestPermissionsIfNecessary(onSuccess: () -> Unit) {
        if (canDrawOverlays) {
            if (!isNecessaryPermissionsAvailable) {
                toast(Toast.LENGTH_LONG) {
                    UiText.StringResource(R.string.message_accept_necessary_permissions)
                }
                necessaryPermissionsResult.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
                return
            }
            onSuccess()
            return
        }
        toast(Toast.LENGTH_LONG) {
            UiText.StringResource(R.string.message_accept_overlay_permission)
        }
        drawOverlaysPermissionActivityResult.launch(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
        )
    }

    private fun startOverlayServiceManager() {
        OverlayServiceManager(
            this,
            viewModel.windowServiceEnabledPreferenceState
        )
    }
}