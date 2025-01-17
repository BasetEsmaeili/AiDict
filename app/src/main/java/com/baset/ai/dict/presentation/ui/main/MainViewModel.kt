package com.baset.ai.dict.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baset.ai.dict.R
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.domain.CardRepository
import com.baset.ai.dict.domain.PreferenceRepository
import com.baset.ai.dict.domain.entity.Card
import com.baset.ai.dict.presentation.ui.core.model.UiText
import com.baset.ai.dict.presentation.util.IntentResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val intentResolver: IntentResolver,
    private val cardRepository: CardRepository
) : ViewModel() {
    val windowServiceEnabledPreferenceState = preferenceRepository.getPreference(
        Constants.PreferencesKey.keyWindowServiceEnabled,
        Constants.PreferencesKey.WINDOW_SERVICE_DEFAULT_VALUE
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
        Constants.PreferencesKey.WINDOW_SERVICE_DEFAULT_VALUE
    )

    private val preferencesUiState = preferenceRepository
        .getAllPreferences()
        .map { preferencesMap ->
            val windowServiceEnabled: Boolean =
                (preferencesMap[Constants.PreferencesKey.keyWindowServiceEnabled] as? Boolean)
                    ?: Constants.PreferencesKey.WINDOW_SERVICE_DEFAULT_VALUE
            val deleteAfterShareToAnki =
                (preferencesMap[Constants.PreferencesKey.keyDeleteAfterShareToAnki] as? Boolean)
                    ?: Constants.PreferencesKey.DELETE_AFTER_SHARE_TO_ANKI_DEFAULT_VALUE
            val modelName = (preferencesMap[Constants.PreferencesKey.keyModelName] as? String)
                ?: Constants.PreferencesKey.defaultAiModelName
            val apiKey = (preferencesMap[Constants.PreferencesKey.keyApiKey] as? String)
                ?: Constants.PreferencesKey.API_KEY_DEFAULT_VALUE
            val englishLevel = (preferencesMap[Constants.PreferencesKey.keyEnglishLevel] as? String)
                ?: Constants.PreferencesKey.defaultEnglishLevel
            val harassment =
                (preferencesMap[Constants.PreferencesKey.keyHarmCategoryHarassment] as? String)
                    ?: Constants.PreferencesKey.harmCategoryHarassmentDefaultValue
            val hateSpeech =
                (preferencesMap[Constants.PreferencesKey.keyHarmCategoryHateSpeech] as? String)
                    ?: Constants.PreferencesKey.harmCategoryHateSpeechDefaultValue
            val sexuallyExplicit =
                (preferencesMap[Constants.PreferencesKey.keyHarmCategorySexuallyExplicit] as? String)
                    ?: Constants.PreferencesKey.harmCategorySexuallyExplicitDefaultValue
            val dangerousContent =
                (preferencesMap[Constants.PreferencesKey.keyHarmCategoryDangerousContent] as? String)
                    ?: Constants.PreferencesKey.harmCategoryDangerousContentDefaultValue
            val requestTimeout =
                (preferencesMap[Constants.PreferencesKey.keyRequestTimeout] as? Long)
                    ?: Constants.PreferencesKey.REQUEST_TIME_OUT_DEFAULT_VALUE
            val apiVersion = (preferencesMap[Constants.PreferencesKey.keyApiVersion] as? String)
                ?: Constants.PreferencesKey.API_VERSION_DEFAULT_VALUE
            val temperature = (preferencesMap[Constants.PreferencesKey.keyTemperature] as? Float)
                ?: Constants.PreferencesKey.TEMPERATURE_DEFAULT_VALUE
            val maxOutputTokens =
                (preferencesMap[Constants.PreferencesKey.keyMaxOutputTokens] as? Int)
                    ?: Constants.PreferencesKey.MAX_OUTPUT_TOKENS_DEFAULT_VALUE
            val topK = (preferencesMap[Constants.PreferencesKey.keyTopK] as? Int)
                ?: Constants.PreferencesKey.TOP_K_DEFAULT_VALUE
            val topP = (preferencesMap[Constants.PreferencesKey.keyTopP] as? Float)
                ?: Constants.PreferencesKey.TOP_P_DEFAULT_VALUE
            val candidateCount =
                (preferencesMap[Constants.PreferencesKey.keyCandidateCount] as? Int)
                    ?: Constants.PreferencesKey.CANDIDATE_COUNT_DEFAULT_VALUE
            PreferencesUiState(
                preferenceItems = persistentListOf(
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.ENGLISH_LEVEL,
                        title = UiText.StringResource(R.string.title_your_english_level),
                        description = UiText.StringResource(R.string.description_english_level),
                        options = Constants.Arrays.englishLevels.map {
                            OptionItem(
                                id = Constants.PreferencesKey.ENGLISH_LEVEL,
                                selected = it.contentEquals(englishLevel, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.Switch(
                        id = Constants.PreferencesKey.DELETE_AFTER_SHARE_TO_ANKI,
                        checked = deleteAfterShareToAnki,
                        title = UiText.StringResource(R.string.title_delete_after_share_to_anki),
                        description = UiText.StringResource(R.string.description_delete_after_share_to_anki)
                    ),
                    PreferenceItem.Switch(
                        id = Constants.PreferencesKey.WINDOW_SERVICE_ID,
                        checked = windowServiceEnabled,
                        title = UiText.StringResource(R.string.title_window_service),
                        description = UiText.StringResource(R.string.description_window_service)
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.MODEL_NAME_ID,
                        title = UiText.StringResource(R.string.title_model_name),
                        description = UiText.StringResource(R.string.description_model_name),
                        options = Constants.Arrays.models.map {
                            OptionItem(
                                id = Constants.PreferencesKey.MODEL_NAME_ID,
                                selected = it.contentEquals(modelName, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.API_KEY,
                        title = UiText.StringResource(R.string.title_api_key),
                        description = UiText.StringResource(R.string.description_api_key),
                        inputType = PreferenceItem.Input.InputType.Text,
                        text = apiKey
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.HARM_CATEGORY_HARASSMENT,
                        title = UiText.StringResource(R.string.title_harm_category_harassment),
                        description = UiText.StringResource(R.string.description_harm_category_harassment),
                        options = Constants.Arrays.securityOptions.map {
                            OptionItem(
                                id = Constants.PreferencesKey.HARM_CATEGORY_HARASSMENT,
                                selected = it.contentEquals(harassment, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.HARM_CATEGORY_HATE_SPEECH,
                        title = UiText.StringResource(R.string.title_harm_category_hate_speech),
                        description = UiText.StringResource(R.string.description_harm_category_hate_speech),
                        options = Constants.Arrays.securityOptions.map {
                            OptionItem(
                                id = Constants.PreferencesKey.HARM_CATEGORY_HATE_SPEECH,
                                selected = it.contentEquals(hateSpeech, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.HARM_CATEGORY_SEXUALLY_EXPLICIT,
                        title = UiText.StringResource(R.string.title_harm_category_sexually_explicit),
                        description = UiText.StringResource(R.string.description_harm_category_sexually_explicit),
                        options = Constants.Arrays.securityOptions.map {
                            OptionItem(
                                id = Constants.PreferencesKey.HARM_CATEGORY_SEXUALLY_EXPLICIT,
                                selected = it.contentEquals(sexuallyExplicit, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.HARM_CATEGORY_DANGEROUS_CONTENT,
                        title = UiText.StringResource(R.string.title_harm_category_dangerous_content),
                        description = UiText.StringResource(R.string.description_harm_category_dangerous_content),
                        options = Constants.Arrays.securityOptions.map {
                            OptionItem(
                                id = Constants.PreferencesKey.HARM_CATEGORY_DANGEROUS_CONTENT,
                                selected = it.contentEquals(dangerousContent, true),
                                text = it
                            )
                        }.toImmutableList()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.REQUEST_TIME_OUT,
                        title = UiText.StringResource(R.string.title_request_timeout),
                        description = UiText.StringResource(R.string.description_request_timeout),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = requestTimeout.toString()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.API_VERSION,
                        title = UiText.StringResource(R.string.title_api_version),
                        description = UiText.StringResource(R.string.description_api_version),
                        inputType = PreferenceItem.Input.InputType.Text,
                        text = apiVersion
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.TEMPERATURE,
                        title = UiText.StringResource(R.string.title_temperature),
                        description = UiText.StringResource(R.string.description_temperature),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = temperature.toString()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.MAX_OUTPUT_TOKENS,
                        title = UiText.StringResource(R.string.title_max_output_tokens),
                        description = UiText.StringResource(R.string.description_max_output_tokens),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = maxOutputTokens.toString()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.TOP_K,
                        title = UiText.StringResource(R.string.title_top_k),
                        description = UiText.StringResource(R.string.description_top_k),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = topK.toString()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.TOP_P,
                        title = UiText.StringResource(R.string.title_top_p),
                        description = UiText.StringResource(R.string.description_top_p),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = topP.toString()
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.CANDIDATE_COUNT,
                        title = UiText.StringResource(R.string.title_candidate_count),
                        description = UiText.StringResource(R.string.description_candidate_count),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = candidateCount.toString()
                    ),
                )
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
            PreferencesUiState()
        )

    private val cardsState = cardRepository
        .cardsFlow()
        .map { it.toImmutableList() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
            persistentListOf()
        )

    val uiState = combine(
        preferencesUiState,
        cardsState
    ) { preferencesUiStates, cards ->
        MainUiState(
            cards = cards,
            preferencesUiState = preferencesUiStates
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
        MainUiState()
    )

    fun onPreferenceSwitchCheckChange(preferenceItem: PreferenceItem) {
        viewModelScope.launch {
            when (preferenceItem.id) {
                Constants.PreferencesKey.WINDOW_SERVICE_ID -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyWindowServiceEnabled,
                        (preferenceItem as PreferenceItem.Switch).checked.not()
                    )
                }

                Constants.PreferencesKey.DELETE_AFTER_SHARE_TO_ANKI -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyDeleteAfterShareToAnki,
                        (preferenceItem as PreferenceItem.Switch).checked.not()
                    )
                }
            }
        }
    }

    fun onPreferenceOptionItemSelected(preferenceItem: PreferenceItem, optionItem: OptionItem) {
        viewModelScope.launch {
            when (preferenceItem.id) {
                Constants.PreferencesKey.MODEL_NAME_ID -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyModelName,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.ENGLISH_LEVEL -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyEnglishLevel,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.HARM_CATEGORY_HARASSMENT -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyHarmCategoryHarassment,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.HARM_CATEGORY_HATE_SPEECH -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyHarmCategoryHateSpeech,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.HARM_CATEGORY_SEXUALLY_EXPLICIT -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyHarmCategorySexuallyExplicit,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.HARM_CATEGORY_DANGEROUS_CONTENT -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyHarmCategoryDangerousContent,
                        optionItem.text
                    )
                }
            }
        }
    }

    fun onInputPreferenceDone(preferenceItem: PreferenceItem, value: String) {
        viewModelScope.launch {
            val inputPreferenceItem = preferenceItem as PreferenceItem.Input
            if (value.contentEquals(preferenceItem.text)) {
                return@launch
            }
            when (inputPreferenceItem.id) {
                Constants.PreferencesKey.API_KEY -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyApiKey,
                        value
                    )
                }

                Constants.PreferencesKey.REQUEST_TIME_OUT -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyRequestTimeout,
                        value.toLong()
                    )
                }

                Constants.PreferencesKey.API_VERSION -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyApiVersion,
                        value
                    )
                }

                Constants.PreferencesKey.TEMPERATURE -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyTemperature,
                        value.toFloat()
                    )
                }

                Constants.PreferencesKey.MAX_OUTPUT_TOKENS -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyMaxOutputTokens,
                        value.toInt()
                    )
                }

                Constants.PreferencesKey.TOP_K -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyTopK,
                        value.toInt()
                    )
                }

                Constants.PreferencesKey.TOP_P -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyTopP,
                        value.toFloat()
                    )
                }

                Constants.PreferencesKey.CANDIDATE_COUNT -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyCandidateCount,
                        value.toInt()
                    )
                }
            }
        }
    }

    fun onOpenProjectOnGithubClicked() {
        intentResolver.openProjectOnGithub()
    }

    fun onSwipedToShareCardToAnki(card: Card): Boolean {
        intentResolver.createAnkiCard(
            card.front,
            card.back
        )
        viewModelScope.launch {
            val deleteAfterShare = preferenceRepository.getPreference(
                Constants.PreferencesKey.keyDeleteAfterShareToAnki,
                Constants.PreferencesKey.DELETE_AFTER_SHARE_TO_ANKI_DEFAULT_VALUE
            ).first()
            if (deleteAfterShare) {
                cardRepository.deleteCard(card)
            }
        }
        return false
    }

    fun onSwipedToDeleteCard(card: Card): Boolean {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
        return false
    }
}