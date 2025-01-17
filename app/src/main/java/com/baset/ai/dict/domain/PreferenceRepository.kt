package com.baset.ai.dict.domain

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T>

    fun getAllPreferences(): Flow<Map<Preferences.Key<*>, Any>>

    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)

    suspend fun <T> removePreference(key: Preferences.Key<T>)

    suspend fun clearAllPreference()
}