package com.baset.anki.pro.generator.common

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.baset.anki.pro.generator.common.Constants.AI.models

object Constants {
    const val FLOW_TIMEOUT = 5000L
    const val SPACE = " "

    object PreferencesKey {
        const val WINDOW_SERVICE_ID = "window_service_enabled"
        val keyWindowServiceEnabled = booleanPreferencesKey(WINDOW_SERVICE_ID)
        const val WINDOW_SERVICE_DEFAULT_VALUE = true

        const val MODEL_NAME_ID = "model_name"
        val keyModelName = stringPreferencesKey(MODEL_NAME_ID)
        val defaultAiModelName = models[0]

        const val API_KEY = "api_key"
        val keyApiKey = stringPreferencesKey(API_KEY)
        val API_KEY_DEFAULT_VALUE: String? = null

        const val REQUEST_TIME_OUT = "request_time_out"
        val keyRequestTimeout = longPreferencesKey(REQUEST_TIME_OUT)
        const val REQUEST_TIME_OUT_DEFAULT = 5000L

        const val API_VERSION = "api_version"
        val keyApiVersion = longPreferencesKey(API_VERSION)
        const val API_VERSION_DEFAULT = "v1beta"

        const val HARM_CATEGORY_HARASSMENT = "harm_category_harassment"
        val keyHarmCategoryHarassment = stringPreferencesKey(HARM_CATEGORY_HARASSMENT)
        const val HARM_CATEGORY_HARASSMENT_DEFAULT = "NONE"

        const val HARM_CATEGORY_HATE_SPEECH = "harm_category_hate_speech"
        val keyHarmCategoryHateSpeech = stringPreferencesKey(HARM_CATEGORY_HATE_SPEECH)
        const val HARM_CATEGORY_HATE_SPEECH_DEFAULT = "NONE"

        const val HARM_CATEGORY_SEXUALLY_EXPLICIT = "harm_category_sexually_explicit"
        val keyHarmCategorySexuallyExplicit = stringPreferencesKey(HARM_CATEGORY_SEXUALLY_EXPLICIT)
        const val HARM_CATEGORY_SEXUALLY_EXPLICIT_DEFAULT = "NONE"

        const val HARM_CATEGORY_DANGEROUS_CONTENT = "harm_category_dangerous_content"
        val keyHarmCategoryDangerousContent = stringPreferencesKey(HARM_CATEGORY_DANGEROUS_CONTENT)
        const val HARM_CATEGORY_DANGEROUS_CONTENT_DEFAULT = "NONE"

        const val INCLUDE_GOOGLE_SEARCH = "include_google_search"
        val keyIncludeGoogleSearch = booleanPreferencesKey(INCLUDE_GOOGLE_SEARCH)
        const val INCLUDE_GOOGLE_SEARCH_DEFAULT = false
    }

    object AI {
        val models = listOf(
            "gemini-2.0-flash-exp",
            "gemini-1.5-flash",
            "gemini-1.5-flash-8b",
            "gemini-1.5-pro"
        )
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