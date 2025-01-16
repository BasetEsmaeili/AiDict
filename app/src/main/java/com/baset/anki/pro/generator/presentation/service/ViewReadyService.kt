package com.baset.anki.pro.generator.presentation.service

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.baset.anki.pro.generator.presentation.util.lazyFast

/**
 * Service that is ready to display view, provide a ui context on the primary screen, and all the tools needed to built a view with state management, view model etc
 */
abstract class ViewReadyService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    /**
     * Build our saved state registry controller
     */
    private val savedStateRegistryController: SavedStateRegistryController by lazyFast {
        SavedStateRegistryController.create(this)
    }

    /**
     * Build our view model store
     */
    private val internalViewModelStore: ViewModelStore by lazyFast {
        ViewModelStore()
    }

    /**
     * Context dedicated to the view
     */
    internal val overlayContext: Context by lazyFast {
        // Get the default display
        val defaultDisplay: Display =
            getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        // Create a display context, and then the window context
        createDisplayContext(defaultDisplay)
            .createWindowContext(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, null)
    }

    override fun onCreate() {
        super.onCreate()
        // Restore the last saved state registry
        savedStateRegistryController.performRestore(null)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = internalViewModelStore
}