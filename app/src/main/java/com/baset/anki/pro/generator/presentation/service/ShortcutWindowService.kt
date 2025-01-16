package com.baset.anki.pro.generator.presentation.service

import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ServiceCompat
import com.baset.anki.pro.generator.presentation.ui.ai.AiActivity
import com.baset.anki.pro.generator.presentation.ui.core.component.AppLogo
import com.baset.anki.pro.generator.presentation.ui.core.theme.appLogoSizeForOverlayService
import com.baset.anki.pro.generator.presentation.util.NotificationsHelper
import com.baset.anki.pro.generator.presentation.util.isSdk34OrUp

class ShortcutWindowService : ComposeOverlayViewService() {
    private val overlayVisible = mutableStateOf(true)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start(startId)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(startId: Int) {
        NotificationsHelper.createNotificationChannel(this)
        ServiceCompat.startForeground(
            this,
            startId,
            NotificationsHelper.buildWindowServiceNotification(this),
            if (isSdk34OrUp) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
    }

    @Composable
    override fun Content() {
        OverlayDraggableContainer {
            val overlayVisible by remember { this@ShortcutWindowService.overlayVisible }
            AnimatedVisibility(overlayVisible) {
                AppLogo(
                    modifier = Modifier.size(appLogoSizeForOverlayService),
                    onClick = this@ShortcutWindowService::onOverlayBubbleClicked
                )
            }
        }
    }

    private fun onOverlayBubbleClicked() {
        startActivity(Intent(this, AiActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

}