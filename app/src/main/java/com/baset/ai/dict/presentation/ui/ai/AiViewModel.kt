package com.baset.ai.dict.presentation.ui.ai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baset.ai.dict.R
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.domain.CardRepository
import com.baset.ai.dict.domain.PreferenceRepository
import com.baset.ai.dict.domain.entity.Card
import com.baset.ai.dict.presentation.ui.ai.model.PickedMedia
import com.baset.ai.dict.presentation.ui.ai.model.TextType
import com.baset.ai.dict.presentation.ui.ai.model.UiMode
import com.baset.ai.dict.presentation.ui.core.model.UiText
import com.baset.ai.dict.presentation.ui.main.PreferenceItem
import com.baset.ai.dict.presentation.util.ClipboardManager
import com.baset.ai.dict.presentation.util.IntentResolver
import com.baset.ai.dict.presentation.util.NetworkMonitor
import com.baset.ai.dict.presentation.util.ResourceProvider
import com.baset.ai.dict.presentation.util.UriConverter
import com.baset.ai.dict.presentation.util.firstOrDefault
import com.baset.ai.dict.presentation.util.isDaytime
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val resourceProvider: ResourceProvider,
    networkMonitor: NetworkMonitor,
    private val uriConverter: UriConverter,
    private val intentResolver: IntentResolver,
    private val clipboardManager: ClipboardManager,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val headlineTitle: UiText = UiText.StringResource(
        R.string.title_what_would_you_like_to_ask,
        resourceProvider.getString(
            if (isDaytime()) {
                R.string.label_today
            } else {
                R.string.label_tonight
            }
        )
    )
    private val commandTextState = TextFieldState()
    private val answerTextState = RichTextState()

    private val messageEventChannel = Channel<String>(Channel.BUFFERED)
    val messageEventFlow = messageEventChannel.receiveAsFlow()

    private val pickedImageState = MutableStateFlow<PickedMedia?>(null)
    private val makeFullScreenState = MutableStateFlow(false)
    private val uiModeState = MutableStateFlow<UiMode>(UiMode.Ask)
    private val networkState = networkMonitor.networkMonitorFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NetworkMonitor.State.Unknown
    )

    val windowServiceEnabledPreferenceState = preferenceRepository.getPreference(
        Constants.PreferencesKey.keyWindowServiceEnabled,
        true
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT),
        true
    )
    private val askOptionItems =
        MutableStateFlow<PersistentMap<String, PreferenceItem>>(persistentMapOf())
    val uiState =
        combine(
            pickedImageState,
            makeFullScreenState,
            uiModeState,
            askOptionItems
        ) { pickedImage,
            makeFullScreenState,
            uiMode,
            askOptionItems ->
            UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState,
                pickedMedia = pickedImage,
                makeFullScreen = makeFullScreenState,
                uiMode = uiMode,
                askOptionItems = askOptionItems.values.toPersistentList()
            )
        }.onStart {
            loadAskOptionItems()
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState
            )
        )
    private val generativeModelState = preferenceRepository.getAllPreferences()
        .map { preferencesMap ->
            val apikey =
                preferencesMap[Constants.PreferencesKey.keyApiKey]?.toString() ?: return@map null
            val modelName =
                (preferencesMap[Constants.PreferencesKey.keyModelName] as? String)
                    ?: Constants.PreferencesKey.defaultAiModelName

            GenerativeModel(
                modelName = modelName,
                apiKey = apikey,
                generationConfig = generateGenerationConfig(preferencesMap),
                safetySettings = generateSafetySettings(preferencesMap),
                requestOptions = generateRequestOptions(preferencesMap),
                tools = generateTools(preferencesMap)
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private fun loadAskOptionItems() {
        viewModelScope.launch {
            val preferencesMap = preferenceRepository.getAllPreferences().first()
            askOptionItems.value = persistentMapOf(
                Constants.PreferencesKey.INCLUDE_GOOGLE_SEARCH to PreferenceItem.Switch(
                    id = Constants.PreferencesKey.INCLUDE_GOOGLE_SEARCH,
                    checked = (preferencesMap[Constants.PreferencesKey.keyIncludeGoogleSearch] as? Boolean)
                        ?: Constants.PreferencesKey.INCLUDE_GOOGLE_SEARCH_DEFAULT_VALUE,
                    title = UiText.StringResource(R.string.title_include_google_search),
                    description = null,
                    onPreferenceSwitchCheckChange = ::onPreferenceSwitchCheckChange
                )
            )
        }
    }

    private fun onPreferenceSwitchCheckChange(preferenceItem: PreferenceItem.Switch) {
        askOptionItems.update {
            it.mutate { preferenceItems ->
                preferenceItems[preferenceItem.id] =
                    preferenceItem.copy(checked = preferenceItem.checked.not())
            }
        }
    }

    private fun generateTools(preferencesMap: Map<Preferences.Key<*>, Any>): List<Tool> {
        // TODO: I am waiting for the Gemini SDK's maintainers to
        //  merge https://github.com/google-gemini/generative-ai-android/pull/232 pull request.
        //  this pull request let me complete the include Google search in results feature.
        return listOf()
    }

    private fun generateSafetySettings(preferencesMap: Map<Preferences.Key<*>, Any>): List<SafetySetting> {
        val harmCategoryHarassmentThreshold =
            BlockThreshold.entries.firstOrDefault(BlockThreshold.NONE) {
                val storedValue =
                    preferencesMap[Constants.PreferencesKey.keyHarmCategoryHarassment]
                        ?.toString()
                        ?: Constants.PreferencesKey.harmCategoryHarassmentDefaultValue
                storedValue.contentEquals(it.name)
            }

        val harmCategoryHateSpeechThreshold =
            BlockThreshold.entries.firstOrDefault(BlockThreshold.NONE) {
                val storedValue =
                    preferencesMap[Constants.PreferencesKey.keyHarmCategoryHateSpeech]
                        ?.toString()
                        ?: Constants.PreferencesKey.harmCategoryHateSpeechDefaultValue
                storedValue.contentEquals(it.name)
            }

        val harmCategorySexuallyExplicitThreshold =
            BlockThreshold.entries.firstOrDefault(BlockThreshold.NONE) {
                val storedValue =
                    preferencesMap[Constants.PreferencesKey.keyHarmCategorySexuallyExplicit]
                        ?.toString()
                        ?: Constants.PreferencesKey.harmCategorySexuallyExplicitDefaultValue
                storedValue.contentEquals(it.name)
            }

        val harmCategoryDangerousContentThreshold =
            BlockThreshold.entries.firstOrDefault(BlockThreshold.NONE) {
                val storedValue =
                    preferencesMap[Constants.PreferencesKey.keyHarmCategoryDangerousContent]
                        ?.toString()
                        ?: Constants.PreferencesKey.harmCategoryDangerousContentDefaultValue
                storedValue.contentEquals(it.name)
            }
        return listOf(
            SafetySetting(HarmCategory.HARASSMENT, harmCategoryHarassmentThreshold),
            SafetySetting(HarmCategory.HATE_SPEECH, harmCategoryHateSpeechThreshold),
            SafetySetting(
                HarmCategory.SEXUALLY_EXPLICIT,
                harmCategorySexuallyExplicitThreshold
            ),
            SafetySetting(
                HarmCategory.DANGEROUS_CONTENT,
                harmCategoryDangerousContentThreshold
            )
        )
    }

    private fun generateRequestOptions(preferencesMap: Map<Preferences.Key<*>, Any>): RequestOptions {
        val requestTimeoutException =
            (preferencesMap[Constants.PreferencesKey.keyRequestTimeout] as? Long)
                ?: Constants.PreferencesKey.REQUEST_TIME_OUT_DEFAULT_VALUE
        val apiVersion =
            preferencesMap[Constants.PreferencesKey.keyApiVersion]?.toString()
                ?: Constants.PreferencesKey.API_VERSION_DEFAULT_VALUE

        return RequestOptions(
            requestTimeoutException,
            apiVersion
        )
    }

    private fun generateGenerationConfig(preferencesMap: Map<Preferences.Key<*>, Any>): GenerationConfig {
        return with(GenerationConfig.builder()) {
            temperature = (preferencesMap[Constants.PreferencesKey.keyTemperature] as? Float)
                ?: Constants.PreferencesKey.TEMPERATURE_DEFAULT_VALUE
            maxOutputTokens = (preferencesMap[Constants.PreferencesKey.keyMaxOutputTokens] as? Int)
                ?: Constants.PreferencesKey.MAX_OUTPUT_TOKENS_DEFAULT_VALUE
            topK = (preferencesMap[Constants.PreferencesKey.keyTopK] as? Int)
                ?: Constants.PreferencesKey.TOP_K_DEFAULT_VALUE
            topP = (preferencesMap[Constants.PreferencesKey.keyTopP] as? Float)
                ?: Constants.PreferencesKey.TOP_P_DEFAULT_VALUE
            candidateCount = (preferencesMap[Constants.PreferencesKey.keyCandidateCount] as? Int)
                ?: Constants.PreferencesKey.CANDIDATE_COUNT_DEFAULT_VALUE
            build()
        }
    }

    fun onExtrasRetrieved(extras: Bundle?) {
        extras ?: return
        uiModeState.value = UiMode.Ask
        val extraText = extras.getString(Intent.EXTRA_TEXT)
        val processText = extras.getString(Intent.EXTRA_PROCESS_TEXT)
        commandTextState.edit {
            append(Constants.SPACE)
            append(processText ?: extraText)
        }
    }

    fun onAskCriteriaClicked() {

    }

    fun onMakeFulScreenClicked() {
        makeFullScreenState.update { !it }
    }

    fun onPickMediaResult(uri: Uri?) {
        pickedImageState.value = uri?.let { PickedMedia(it) }
    }

    fun onRemovePickedMediaClicked() {
        pickedImageState.value = null
    }

    fun onSendCommandClicked() {
        if (networkState.value != NetworkMonitor.State.Available) {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_check_network))
            return
        }
        val generativeModel = generativeModelState.value ?: run {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_model_config))
            return
        }
        uiModeState.value = UiMode.Loading
        viewModelScope.launch {
            runCatching {
                var attachedMediaByteArray: ByteArray? = null
                pickedImageState.value?.let { pickedMedia ->
                    attachedMediaByteArray = uriConverter.toByteArray(pickedMedia.pickedMedia)
                }
                val englishLevel = preferenceRepository.getPreference(
                    Constants.PreferencesKey.keyEnglishLevel,
                    Constants.PreferencesKey.defaultEnglishLevel
                ).first()
                val includeGoogleSearch =
                    (askOptionItems.value[Constants.PreferencesKey.INCLUDE_GOOGLE_SEARCH] as PreferenceItem.Switch).checked
                val content = content {
                    if (!englishLevel.contentEquals(
                            Constants.PreferencesKey.defaultEnglishLevel,
                            true
                        )
                    ) {
                        text("${resourceProvider.getString(R.string.command_my_english_level_is)} $englishLevel")
                        text(resourceProvider.getString(R.string.command_do_not_mention_my_english_level))
                    }
                    if (includeGoogleSearch) {
                        text(resourceProvider.getString(R.string.command_include_google_search))
                        text(resourceProvider.getString(R.string.command_do_not_mention_that_your_answer_is_based_on_google))
                    }
                    text(resourceProvider.getString(R.string.label_my_command))
                    text(commandTextState.text.toString())
                    attachedMediaByteArray?.let {
                        blob("image/jpeg", it)
                    }
                }
                val result = generativeModel.generateContent(content)
                uiModeState.value = UiMode.Answer
                typeAnswer(result.text.orEmpty().trim())
            }.onFailure {
                uiModeState.value = UiMode.Error
                typeAnswer(it.localizedMessage.orEmpty().trim())
            }
        }
    }

    private fun typeAnswer(text: String) {
        answerTextState.setMarkdown(text)
    }

    private fun textToShare(textType: TextType): String {
        val selection = answerTextState.selection
        val isTextSelected = selection.start != selection.end
        val text = when (textType) {
            TextType.HTML -> answerTextState.toHtml()
            TextType.MARKDOWN -> answerTextState.toMarkdown()
            TextType.PLAIN -> answerTextState.toText()
        }
        return if (isTextSelected) {
            text.substring(selection.start, selection.end)
        } else {
            text
        }
    }

    fun onGoogleResultClicked() {
        intentResolver.googleResult(textToShare(TextType.PLAIN))
    }

    fun onShareTextClicked() {
        intentResolver.shareText(
            resourceProvider.getString(R.string.title_share_via),
            textToShare(TextType.PLAIN)
        )
    }

    fun onCopyTextClicked() {
        clipboardManager.copyFormattedTextToClipboard(
            label = resourceProvider.getString(R.string.app_name),
            plainTextToCopy = textToShare(TextType.PLAIN),
            htmlTextToCopy = textToShare(TextType.HTML)
        )
    }

    fun onCreateAnkiCardClicked() {
        intentResolver.createAnkiCard(
            commandTextState.text.toString(),
            textToShare(TextType.HTML)
        )
    }

    fun onSaveAsCardClicked() {
        viewModelScope.launch {
            cardRepository.insertCard(
                Card(
                    front = commandTextState.text.toString(),
                    back = answerTextState.toMarkdown()
                )
            )
            messageEventChannel.trySend(
                resourceProvider.getString(R.string.message_save_accomplished)
            )
        }
    }

    fun onAskAnotherQuestionClicked() {
        answerTextState.clear()
        commandTextState.clearText()
        pickedImageState.value = null
        uiModeState.value = UiMode.Ask
    }

    data class UiState(
        val headlineTitle: UiText,
        val commandTextState: TextFieldState,
        val answerTextState: RichTextState,
        val pickedMedia: PickedMedia? = null,
        val makeFullScreen: Boolean = false,
        val uiMode: UiMode = UiMode.Ask,
        val askOptionItems: ImmutableList<PreferenceItem> = persistentListOf()
    )
}