package com.baset.anki.pro.generator.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baset.anki.pro.generator.R
import com.baset.anki.pro.generator.common.Constants
import com.baset.anki.pro.generator.domain.PreferenceRepository
import com.baset.anki.pro.generator.presentation.ui.core.model.UiText
import com.baset.anki.pro.generator.presentation.util.IntentResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val intentResolver: IntentResolver
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
            val modelName = (preferencesMap[Constants.PreferencesKey.keyModelName] as? String)
                ?: Constants.PreferencesKey.defaultAiModelName
            val apiKey = (preferencesMap[Constants.PreferencesKey.keyApiKey] as? String)
                ?: Constants.PreferencesKey.API_KEY_DEFAULT_VALUE
            PreferencesUiState(
                preferenceItems = persistentListOf(
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
                        options = Constants.AI.models.map {
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
                    )
                )
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
            PreferencesUiState()
        )

    val uiState = combine(
        preferencesUiState
    ) { preferencesUiStates ->
        MainUiState(
            preferencesUiState = preferencesUiStates[0]
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
            }
        }
    }

    fun onOpenProjectOnGithubClicked() {
        intentResolver.openProjectOnGithub()
    }
}