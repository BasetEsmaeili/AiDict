package com.baset.ai.dict.common

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.baset.ai.dict.common.Constants.Arrays.englishLevels
import com.baset.ai.dict.common.Constants.Arrays.models
import com.baset.ai.dict.common.Constants.Arrays.securityOptions

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

        const val ENGLISH_LEVEL = "english_level"
        val keyEnglishLevel = stringPreferencesKey(ENGLISH_LEVEL)
        val defaultEnglishLevel: String = englishLevels[0]

        const val REQUEST_TIME_OUT = "request_time_out"
        val keyRequestTimeout = longPreferencesKey(REQUEST_TIME_OUT)
        const val REQUEST_TIME_OUT_DEFAULT_VALUE = 5000L

        const val API_VERSION = "api_version"
        val keyApiVersion = stringPreferencesKey(API_VERSION)
        const val API_VERSION_DEFAULT_VALUE = "v1beta"

        const val HARM_CATEGORY_HARASSMENT = "harm_category_harassment"
        val keyHarmCategoryHarassment = stringPreferencesKey(HARM_CATEGORY_HARASSMENT)
        val harmCategoryHarassmentDefaultValue = securityOptions[0]

        const val HARM_CATEGORY_HATE_SPEECH = "harm_category_hate_speech"
        val keyHarmCategoryHateSpeech = stringPreferencesKey(HARM_CATEGORY_HATE_SPEECH)
        val harmCategoryHateSpeechDefaultValue = securityOptions[0]

        const val HARM_CATEGORY_SEXUALLY_EXPLICIT = "harm_category_sexually_explicit"
        val keyHarmCategorySexuallyExplicit = stringPreferencesKey(HARM_CATEGORY_SEXUALLY_EXPLICIT)
        val harmCategorySexuallyExplicitDefaultValue = securityOptions[0]

        const val HARM_CATEGORY_DANGEROUS_CONTENT = "harm_category_dangerous_content"
        val keyHarmCategoryDangerousContent = stringPreferencesKey(HARM_CATEGORY_DANGEROUS_CONTENT)
        val harmCategoryDangerousContentDefaultValue = securityOptions[0]

        const val TEMPERATURE = "temperature"
        val keyTemperature = floatPreferencesKey(TEMPERATURE)
        const val TEMPERATURE_DEFAULT_VALUE = 0.1f

        const val MAX_OUTPUT_TOKENS = "max_output_tokens"
        val keyMaxOutputTokens = intPreferencesKey(MAX_OUTPUT_TOKENS)
        const val MAX_OUTPUT_TOKENS_DEFAULT_VALUE = 150

        const val TOP_K = "top_k"
        val keyTopK = intPreferencesKey(TOP_K)
        const val TOP_K_DEFAULT_VALUE = 50

        const val TOP_P = "top_p"
        val keyTopP = floatPreferencesKey(TOP_P)
        const val TOP_P_DEFAULT_VALUE = 0.95f

        const val CANDIDATE_COUNT = "candidate_count"
        val keyCandidateCount = intPreferencesKey(CANDIDATE_COUNT)
        const val CANDIDATE_COUNT_DEFAULT_VALUE = 1


        const val INCLUDE_GOOGLE_SEARCH = "include_google_search"
        val keyIncludeGoogleSearch = booleanPreferencesKey(INCLUDE_GOOGLE_SEARCH)
        const val INCLUDE_GOOGLE_SEARCH_DEFAULT_VALUE = false

        const val DELETE_AFTER_SHARE_TO_ANKI = "delete_after_share_to_anki"
        val keyDeleteAfterShareToAnki = booleanPreferencesKey(DELETE_AFTER_SHARE_TO_ANKI)
        const val DELETE_AFTER_SHARE_TO_ANKI_DEFAULT_VALUE = true
    }

    object Database {
        const val DB_NAME = "ai_dict"
    }

    object Arrays {
        val models = listOf(
            "gemini-2.0-flash-exp",
            "gemini-1.5-flash",
            "gemini-1.5-flash-8b",
            "gemini-1.5-pro"
        )

        val englishLevels = listOf(
            "Unknown",
            "A1",
            "A2",
            "B1",
            "B2",
            "C1",
            "C2"
        )

        val securityOptions = listOf(
            "NONE",
            "UNSPECIFIED",
            "LOW_AND_ABOVE",
            "MEDIUM_AND_ABOVE",
            "ONLY_HIGH"
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