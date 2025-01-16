package com.baset.anki.pro.generator.presentation.di

import android.app.Activity
import com.baset.anki.pro.generator.presentation.ui.ai.AiViewModel
import com.baset.anki.pro.generator.presentation.ui.main.MainViewModel
import com.baset.anki.pro.generator.presentation.util.ClipboardManager
import com.baset.anki.pro.generator.presentation.util.IntentResolver
import com.baset.anki.pro.generator.presentation.util.NetworkMonitor
import com.baset.anki.pro.generator.presentation.util.ResourceProvider
import com.baset.anki.pro.generator.presentation.util.UriConverter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.lazyModule

val presentationModule = lazyModule {
    factory { NetworkMonitor(androidContext()) }
    factory { UriConverter(androidContext()) }
    factory { ClipboardManager(androidContext()) }
    viewModel { (activity: Activity) ->
        MainViewModel(
            get(),
            IntentResolver(activity)
        )
    }
    viewModel { (activity: Activity) ->
        AiViewModel(
            get(),
            ResourceProvider(activity),
            get(),
            get(),
            IntentResolver(activity),
            get()
        )
    }
}