package com.baset.ai.dict.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.data.CardRepositoryImpl
import com.baset.ai.dict.data.PreferenceRepositoryImpl
import com.baset.ai.dict.data.db.AppDatabase
import com.baset.ai.dict.data.pref.PreferenceHelper
import com.baset.ai.dict.domain.CardRepository
import com.baset.ai.dict.domain.PreferenceRepository
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