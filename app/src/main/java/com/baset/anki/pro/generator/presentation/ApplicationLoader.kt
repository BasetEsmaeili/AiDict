package com.baset.anki.pro.generator.presentation

import android.app.Application
import com.baset.anki.pro.generator.data.di.dataModule
import com.baset.anki.pro.generator.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.dsl.KoinAppDeclaration

@OptIn(KoinExperimentalAPI::class)
class ApplicationLoader : Application(), KoinStartup {

    override fun onKoinStartup(): KoinAppDeclaration = {
        androidContext(this@ApplicationLoader)
        lazyModules(dataModule, presentationModule)
    }
}