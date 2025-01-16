package com.baset.anki.pro.generator.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.baset.anki.pro.generator.common.Constants
import com.baset.anki.pro.generator.data.CardRepositoryImpl
import com.baset.anki.pro.generator.data.PreferenceRepositoryImpl
import com.baset.anki.pro.generator.data.db.AppDatabase
import com.baset.anki.pro.generator.data.pref.PreferenceHelper
import com.baset.anki.pro.generator.domain.CardRepository
import com.baset.anki.pro.generator.domain.PreferenceRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.lazyModule

val Context.dataStore by preferencesDataStore(name = "ai_dict")

val dataModule = lazyModule {
    single { provideDataStore(get()) }
    single { PreferenceHelper(get()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            Constants.Database.DB_NAME
        ).build()
    }
    single { get<AppDatabase>().cardDAO() }
    factory { PreferenceRepositoryImpl(get()) }
    factory { CardRepositoryImpl(get()) }
    factoryOf(::CardRepositoryImpl) { bind<CardRepository>() }
    factoryOf(::PreferenceRepositoryImpl) { bind<PreferenceRepository>() }
}

fun provideDataStore(context: Context): DataStore<Preferences> {
    return context.dataStore
}