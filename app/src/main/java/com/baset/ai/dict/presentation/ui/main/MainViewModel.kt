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
import com.baset.ai.dict.presentation.util.ResourceProvider
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
    private val cardRepository: CardRepository,
    private val resourcesProvider: ResourceProvider
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
            val apiKey = (preferencesMap[Constants.PreferencesKey.keyApiKey] as? String)
                ?: Constants.PreferencesKey.API_KEY_DEFAULT_VALUE
            val englishLevel = (preferencesMap[Constants.PreferencesKey.keyEnglishLevel] as? String)
                ?: Constants.PreferencesKey.defaultEnglishLevel
            val requestTimeout =
                (preferencesMap[Constants.PreferencesKey.keyRequestTimeout] as? Long)
                    ?: Constants.PreferencesKey.REQUEST_TIME_OUT_DEFAULT_VALUE
            val temperature = (preferencesMap[Constants.PreferencesKey.keyTemperature] as? Float)
                ?: Constants.PreferencesKey.TEMPERATURE_DEFAULT_VALUE
            val topP = (preferencesMap[Constants.PreferencesKey.keyTopP] as? Float)
                ?: Constants.PreferencesKey.TOP_P_DEFAULT_VALUE
            val host = (preferencesMap[Constants.PreferencesKey.keyHost] as? String)
                ?: Constants.PreferencesKey.defaultHost
            val useAssistantsEnabled: Boolean =
                (preferencesMap[Constants.PreferencesKey.keyUseAssistants] as? Boolean)
                    ?: Constants.PreferencesKey.USE_ASSISTANTS_DEFAULT_VALUE
            val defaultInstructions = resourcesProvider.getString(R.string.default_instructions)
            PreferencesUiState(
                preferenceItems = persistentListOf(
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.ENGLISH_LEVEL,
                        title = UiText.StringResource(R.string.title_your_english_level),
                        description = UiText.StringResource(R.string.description_english_level),
                        options = Constants.DefaultContent.englishLevels.map {
                            OptionItem(
                                id = Constants.PreferencesKey.ENGLISH_LEVEL,
                                selected = it.contentEquals(englishLevel, true),
                                text = it
                            )
                        }.toImmutableList(),
                        onPreferenceOptionItemSelected = ::onPreferenceOptionItemSelected
                    ),
                    PreferenceItem.Switch(
                        id = Constants.PreferencesKey.DELETE_AFTER_SHARE_TO_ANKI,
                        checked = deleteAfterShareToAnki,
                        title = UiText.StringResource(R.string.title_delete_after_share_to_anki),
                        description = UiText.StringResource(R.string.description_delete_after_share_to_anki),
                        onPreferenceSwitchCheckChange = ::onPreferenceSwitchCheckChange
                    ),
                    PreferenceItem.Switch(
                        id = Constants.PreferencesKey.WINDOW_SERVICE_ID,
                        checked = windowServiceEnabled,
                        title = UiText.StringResource(R.string.title_window_service),
                        description = UiText.StringResource(R.string.description_window_service),
                        onPreferenceSwitchCheckChange = ::onPreferenceSwitchCheckChange
                    ),
                    PreferenceItem.Switch(
                        id = Constants.PreferencesKey.USE_ASSISTANTS,
                        checked = useAssistantsEnabled,
                        title = UiText.StringResource(R.string.title_use_assistants),
                        description = UiText.StringResource(R.string.description_use_assistants),
                        onPreferenceSwitchCheckChange = ::onPreferenceSwitchCheckChange
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.API_KEY,
                        title = UiText.StringResource(R.string.title_api_key),
                        description = UiText.StringResource(R.string.description_api_key),
                        inputType = PreferenceItem.Input.InputType.Text,
                        text = apiKey,
                        onInputPreferenceDone = ::onInputPreferenceDone
                    ),
                    PreferenceItem.OptionDialog(
                        id = Constants.PreferencesKey.HOST,
                        title = UiText.StringResource(R.string.title_your_ai_service_provider),
                        description = UiText.StringResource(R.string.description_your_ai_service_provider),
                        options = Constants.DefaultContent.hosts.map {
                            OptionItem(
                                id = Constants.PreferencesKey.HOST,
                                selected = it.contentEquals(host, true),
                                text = it
                            )
                        }.toImmutableList(),
                        onPreferenceOptionItemSelected = ::onPreferenceOptionItemSelected
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.DEFAULT_INSTRUCTIONS,
                        title = UiText.StringResource(R.string.title_default_instructions),
                        description = UiText.StringResource(R.string.description_default_instructions),
                        inputType = PreferenceItem.Input.InputType.Text,
                        text = defaultInstructions,
                        onInputPreferenceDone = ::onInputPreferenceDone
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.REQUEST_TIME_OUT,
                        title = UiText.StringResource(R.string.title_request_timeout),
                        description = UiText.StringResource(R.string.description_request_timeout),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = requestTimeout.toString(),
                        onInputPreferenceDone = ::onInputPreferenceDone
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.TEMPERATURE,
                        title = UiText.StringResource(R.string.title_temperature),
                        description = UiText.StringResource(R.string.description_temperature),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = temperature.toString(),
                        onInputPreferenceDone = ::onInputPreferenceDone
                    ),
                    PreferenceItem.Input(
                        id = Constants.PreferencesKey.TOP_P,
                        title = UiText.StringResource(R.string.title_top_p),
                        description = UiText.StringResource(R.string.description_top_p),
                        inputType = PreferenceItem.Input.InputType.Number,
                        text = topP.toString(),
                        onInputPreferenceDone = ::onInputPreferenceDone
                    ),
                    PreferenceItem.CopyRight(
                        id = "Copyright",
                        onOpenProjectOnGithubClicked = ::onOpenProjectOnGithubClicked
                    )
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

                Constants.PreferencesKey.USE_ASSISTANTS -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyUseAssistants,
                        (preferenceItem as PreferenceItem.Switch).checked.not()
                    )
                }
            }
        }
    }

    fun onPreferenceOptionItemSelected(preferenceItem: PreferenceItem, optionItem: OptionItem) {
        viewModelScope.launch {
            when (preferenceItem.id) {
                Constants.PreferencesKey.ENGLISH_LEVEL -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyEnglishLevel,
                        optionItem.text
                    )
                }

                Constants.PreferencesKey.HOST -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyHost,
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

                Constants.PreferencesKey.TEMPERATURE -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyTemperature,
                        value.toDouble()
                    )
                }

                Constants.PreferencesKey.TOP_P -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyTopP,
                        value.toDouble()
                    )
                }

                Constants.PreferencesKey.DEFAULT_INSTRUCTIONS -> {
                    preferenceRepository.putPreference(
                        Constants.PreferencesKey.keyDefaultInstructions,
                        value
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