package com.baset.ai.dict.presentation.service

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

class OverlayServiceManager(
    activity: ComponentActivity,
    overlayServiceAllowed: Flow<Boolean>
) {
    init {
        combine(
            overlayServiceAllowed,
            activity.lifecycle.eventFlow
        ) { allowed, lifecycleState ->
            when (lifecycleState) {
                Lifecycle.Event.ON_RESUME -> {
                    activity.stopService(Intent(activity, ShortcutWindowService::class.java))
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (allowed) {
                        ContextCompat.startForegroundService(
                            activity,
                            Intent(activity, ShortcutWindowService::class.java)
                        )
                    }
                }

                else -> return@combine
            }
        }.launchIn(activity.lifecycleScope)
    }
}