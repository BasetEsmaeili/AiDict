package com.baset.ai.dict.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.baset.ai.dict.presentation.ui.core.model.UiText


val isSdk34OrUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

val isSdk33OrUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

val Context.canDrawOverlays: Boolean
    get() = Settings.canDrawOverlays(this)

val Context.isNecessaryPermissionsAvailable: Boolean
    get() = (!isSdk33OrUp || isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS))
            /*
            Disable the speech to text until is ready.

            isPermissionGranted(Manifest.permission.RECORD_AUDIO)
            */

fun Context.isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(
    applicationContext,
    permission
) == PackageManager.PERMISSION_GRANTED

inline fun Context.toast(length: Int, text: () -> UiText) {
    Toast.makeText(
        applicationContext,
        text().asString(applicationContext),
        length
    ).show()
}