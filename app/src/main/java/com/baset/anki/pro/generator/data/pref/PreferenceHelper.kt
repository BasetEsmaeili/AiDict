package com.baset.anki.pro.generator.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PreferenceHelper(private val dataStore: DataStore<Preferences>) {
    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
            Flow<T> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val result = preferences[key] ?: defaultValue
        result
    }

    fun getAllPreferences() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences.asMap()
    }


    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    suspend fun clearAllPreference() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}