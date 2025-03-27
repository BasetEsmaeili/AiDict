package com.baset.ai.dict.common

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.baset.ai.dict.common.Constants.DefaultContent.englishLevels
import com.baset.ai.dict.common.Constants.DefaultContent.hosts

object Constants {
    const val ONE = 1
    const val FLOW_TIMEOUT = 5000L
    const val DIALOG_DISMISS = 150L
    const val SPACE = " "
    const val UNDERLINE = "_"
    const val IMAGE_BASE64_PREFIX = "data:image/png;base64,"

    object PreferencesKey {
        const val WINDOW_SERVICE_ID = "window_service_enabled"
        val keyWindowServiceEnabled = booleanPreferencesKey(WINDOW_SERVICE_ID)
        const val WINDOW_SERVICE_DEFAULT_VALUE = true

        const val API_KEY = "api_key"
        val keyApiKey = stringPreferencesKey(API_KEY)
        val API_KEY_DEFAULT_VALUE: String? = null

        const val ENGLISH_LEVEL = "english_level"
        val keyEnglishLevel = stringPreferencesKey(ENGLISH_LEVEL)
        val defaultEnglishLevel: String = englishLevels[0]

        const val REQUEST_TIME_OUT = "request_time_out"
        val keyRequestTimeout = longPreferencesKey(REQUEST_TIME_OUT)
        const val REQUEST_TIME_OUT_DEFAULT_VALUE = 5000L

        const val TEMPERATURE = "temperature"
        val keyTemperature = doublePreferencesKey(TEMPERATURE)
        const val TEMPERATURE_DEFAULT_VALUE = 0.1

        const val TOP_P = "top_p"
        val keyTopP = doublePreferencesKey(TOP_P)
        const val TOP_P_DEFAULT_VALUE = 0.95

        const val DELETE_AFTER_SHARE_TO_ANKI = "delete_after_share_to_anki"
        val keyDeleteAfterShareToAnki = booleanPreferencesKey(DELETE_AFTER_SHARE_TO_ANKI)
        const val DELETE_AFTER_SHARE_TO_ANKI_DEFAULT_VALUE = true

        const val HOST = "host"
        val keyHost = stringPreferencesKey(HOST)
        val defaultHost = hosts[0]

        const val DEFAULT_INSTRUCTIONS = "default_instructions"
        val keyDefaultInstructions = stringPreferencesKey(DEFAULT_INSTRUCTIONS)

        const val SELECTED_AI_MODEL = "selected_ai_model"
        val keySelectedAiModel = stringPreferencesKey(SELECTED_AI_MODEL)

        const val MAX_COMPLETION_TOKENS = "max_completion_tokens"
        val keyMaxCompletionTokens = intPreferencesKey(MAX_COMPLETION_TOKENS)
        const val MAX_COMPLETION_TOKENS_DEFAULT_VALUE = 400

        const val UNIQUE_ID = "unique_id"
        val keyUniqueId = stringPreferencesKey(UNIQUE_ID)

        const val EXPORT_TYPE = "export_type"
        val keyExportType = stringPreferencesKey(EXPORT_TYPE)
    }

    object Database {
        const val DB_NAME = "ai_dict"
    }

    object DefaultContent {
        val englishLevels = listOf(
            "Unknown",
            "A1",
            "A2",
            "B1",
            "B2",
            "C1",
            "C2"
        )
        const val OPEN_AI = "OpenAI"
        const val DEEP_SEEK = "DeepSeek"
        val hosts = listOf(
            OPEN_AI,
            DEEP_SEEK
        )
        const val DEEP_SEEK_BASE_URL = "https://api.deepseek.com"
    }

    object Intent {
        const val GITHUB_URI: String = "https://github.com/BasetEsmaeili/AiDict"
        const val ANKI_PACKAGE = "com.ichi2.anki"
        const val ANKI_ACTION = "org.openintents.action.CREATE_FLASHCARD"
        const val CARD_FRONT = "SOURCE_TEXT"
        const val CARD_BACK = "TARGET_TEXT"
        const val PLAIN_TEXT = "text/plain"
    }
}