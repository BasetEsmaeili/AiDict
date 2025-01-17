package com.baset.ai.dict.data

import androidx.datastore.preferences.core.Preferences
import com.baset.ai.dict.data.pref.PreferenceHelper
import com.baset.ai.dict.domain.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val preferenceHelper: PreferenceHelper
) : PreferenceRepository {
    override fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        preferenceHelper.getPreference(key, defaultValue)

    override fun getAllPreferences(): Flow<Map<Preferences.Key<*>, Any>> =
        preferenceHelper.getAllPreferences()

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) =
        preferenceHelper.putPreference(key, value)

    override suspend fun <T> removePreference(key: Preferences.Key<T>) =
        preferenceHelper.removePreference(key)

    override suspend fun clearAllPreference() = preferenceHelper.clearAllPreference()
}