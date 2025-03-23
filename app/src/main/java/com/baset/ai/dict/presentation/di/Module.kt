package com.baset.ai.dict.presentation.di

import android.app.Activity
import com.baset.ai.dict.presentation.ui.ai.AiViewModel
import com.baset.ai.dict.presentation.ui.main.MainViewModel
import com.baset.ai.dict.presentation.util.ClipboardManager
import com.baset.ai.dict.presentation.util.IntentResolver
import com.baset.ai.dict.presentation.util.NetworkMonitor
import com.baset.ai.dict.presentation.util.ResourceProvider
import com.baset.ai.dict.presentation.util.UriConverter
import com.baset.ai.dict.presentation.worker.UniqueIdGeneratorWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val presentationModule = lazyModule {
    factory { NetworkMonitor(androidContext()) }
    factory { UriConverter(androidContext()) }
    factory { ClipboardManager(androidContext()) }
    viewModel { (activity: Activity) ->
        MainViewModel(
            get(),
            IntentResolver(activity),
            get(),
            ResourceProvider(activity)
        )
    }
    viewModel { (activity: Activity) ->
        AiViewModel(
            get(),
            ResourceProvider(activity),
            get(),
            get(),
            IntentResolver(activity),
            get(),
            get()
        )
    }
    worker { UniqueIdGeneratorWorker(get(), get(), get()) }
}