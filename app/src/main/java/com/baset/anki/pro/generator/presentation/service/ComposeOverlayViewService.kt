package com.baset.anki.pro.generator.presentation.service

import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.baset.anki.pro.generator.presentation.util.lazyFast
import kotlin.math.roundToInt

/**
 * Service that is ready to display compose overlay view
 * @author Kishan
 */
abstract class ComposeOverlayViewService : ViewReadyService() {

    // Build the layout param for our popup
    private val layoutParams by lazyFast {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
    }

    // The current offset of our overlay composable
    private var overlayOffset by mutableStateOf(Offset.Zero)

    // Access our window manager
    private val windowManager by lazyFast {
        overlayContext.getSystemService(WindowManager::class.java)
    }

    // Build our compose view
    private val composeView by lazyFast {
        ComposeView(overlayContext)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Bound the compose lifecycle, view model and view tree saved state, into our view service
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        // Set the content of our compose view
        composeView.setContent { Content() }

        // Push the compose view into our window manager
        windowManager.addView(composeView, layoutParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove our compose view from the window manager
        windowManager.removeView(composeView)
    }

    @Composable
    abstract fun Content()

    /**
     * Draggable box container (not used by default, since not every overlay should be draggable)
     */
    @Composable
    internal fun OverlayDraggableContainer(
        modifier: Modifier = Modifier,
        content: @Composable BoxScope.() -> Unit
    ) =
        Box(
            modifier = modifier.pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    // Update our current offset
                    val newOffset = overlayOffset + dragAmount
                    overlayOffset = newOffset

                    // Update the layout params, and then the view
                    layoutParams.apply {
                        x = overlayOffset.x.roundToInt()
                        y = overlayOffset.y.roundToInt()
                    }
                    windowManager.updateViewLayout(composeView, layoutParams)
                }
            },
            content = content
        )
}