package com.baset.anki.pro.generator.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.baset.anki.pro.generator.data.PreferenceRepositoryImpl
import com.baset.anki.pro.generator.data.pref.PreferenceHelper
import com.baset.anki.pro.generator.domain.PreferenceRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.lazyModule

val Context.dataStore by preferencesDataStore(name = "ai_dict")

val dataModule = lazyModule {
    single { provideDataStore(get()) }
    single { PreferenceHelper(get()) }
    factory { PreferenceRepositoryImpl(get()) }
    factoryOf(::PreferenceRepositoryImpl) { bind<PreferenceRepository>() }
}

fun provideDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}