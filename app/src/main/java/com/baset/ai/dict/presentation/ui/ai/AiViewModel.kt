package com.baset.ai.dict.presentation.ui.ai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.aallam.openai.client.RetryStrategy
import com.baset.ai.dict.BuildConfig
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
import com.baset.ai.dict.presentation.util.isDaytime
import com.baset.ai.dict.presentation.util.toBase64
import com.mohamedrejeb.richeditor.model.RichTextState
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        uiModeState.value = UiMode.Error
        typeAnswer(e.localizedMessage.orEmpty().trim())
    }
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

    private val commandOptionsStateFlow = MutableStateFlow(
        Pair<Boolean, PersistentList<PreferenceItem>>(
            false,
            persistentListOf()
        )
    )

    private val selectedAiModel =
        preferenceRepository.getPreference(Constants.PreferencesKey.keySelectedAiModel, "")
            .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val uiState =
        combine(
            pickedImageState,
            makeFullScreenState,
            uiModeState,
            commandOptionsStateFlow
        ) { pickedImage,
            makeFullScreenState,
            uiMode,
            commandOptionsState ->
            UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState,
                pickedMedia = pickedImage,
                commandOptionsEnabled = commandOptionsState.first,
                commandOptions = commandOptionsState.second,
                makeFullScreen = makeFullScreenState,
                uiMode = uiMode
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), UiState(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                answerTextState = answerTextState
            )
        )
    private val openAIStateFlow = preferenceRepository.getAllPreferences()
        .map { preferencesMap ->
            val apikey =
                preferencesMap[Constants.PreferencesKey.keyApiKey]?.toString() ?: return@map null
            val timeout = (preferencesMap[Constants.PreferencesKey.keyRequestTimeout] as? Long)
                ?: Constants.PreferencesKey.REQUEST_TIME_OUT_DEFAULT_VALUE
            val host = (preferencesMap[Constants.PreferencesKey.keyHost] as? String)
                ?: Constants.PreferencesKey.defaultHost

            OpenAI(
                OpenAIConfig(
                    token = apikey,
                    logging = LoggingConfig(
                        logLevel = if (BuildConfig.DEBUG) {
                            LogLevel.All
                        } else {
                            LogLevel.None
                        },
                        logger = Logger.Default,
                        sanitize = true
                    ),
                    timeout = Timeout(
                        request = timeout.milliseconds,
                        connect = timeout.milliseconds,
                        socket = timeout.milliseconds
                    ),
                    host = getHostBasedOnSelectedService(host),
                    retry = RetryStrategy(),
                    engine = OkHttpEngine(OkHttpConfig())
                )
            )
        }.flowOn(Dispatchers.IO)
        .filterNotNull()
        .onEach {
            loadAiModels(openAI = it)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    private fun loadAiModels(openAI: OpenAI) {
        viewModelScope.launch(exceptionHandler) {
            val models = openAI.models().map { model ->
                PreferenceItem.Radio(
                    id = model.id.id,
                    title = UiText.DynamicString(model.id.id),
                    checked = model.id.id.contentEquals(selectedAiModel.value),
                    onRadioCheckChange = this@AiViewModel::onAiModelSelected
                )
            }.toPersistentList()
            commandOptionsStateFlow.value = Pair(models.isNotEmpty(), models)
        }
    }

    private fun onAiModelSelected(item: PreferenceItem.Radio) {
        viewModelScope.launch {
            preferenceRepository.putPreference(Constants.PreferencesKey.keySelectedAiModel, item.id)
        }
        commandOptionsStateFlow.update { state ->
            Pair(
                state.first,
                state
                    .second
                    .mutate { list ->
                        for (index in list.indices) {
                            list[index] =
                                (list[index] as PreferenceItem.Radio).copy(checked = list[index].id == item.id)
                        }
                    }
            )
        }
    }

    private fun getHostBasedOnSelectedService(host: String): OpenAIHost {
        return when (host) {
            Constants.DefaultContent.OPEN_AI -> OpenAIHost.OpenAI
            Constants.DefaultContent.DEEP_SEEK -> OpenAIHost(Constants.DefaultContent.DEEP_SEEK_BASE_URL)
            else -> throw IllegalArgumentException("Unknown host")
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
        if (selectedAiModel.value.isEmpty()) {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_select_an_ai_model))
            return
        }
        val openAI = openAIStateFlow.value ?: run {
            messageEventChannel.trySend(resourceProvider.getString(R.string.error_model_config))
            return
        }
        uiModeState.value = UiMode.Loading
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            runCatching {
                startConversation(openAI)
            }.onFailure {
                uiModeState.value = UiMode.Error
                typeAnswer(it.message.orEmpty().trim())
            }
        }
    }

    private suspend fun prepareDefaultInstructions(): String {
        val englishLevel = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyEnglishLevel,
            Constants.PreferencesKey.defaultEnglishLevel
        ).first()

        val defaultInstructions = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyDefaultInstructions,
            resourceProvider.getString(R.string.default_instructions)
        ).first()
        return buildString {
            if (!englishLevel.contentEquals(Constants.PreferencesKey.defaultEnglishLevel, true)) {
                appendLine(resourceProvider.getString(R.string.language_preference, englishLevel))
            }
            appendLine(defaultInstructions)
            appendLine(resourceProvider.getString(R.string.answer_limit))
        }
    }

    private suspend fun startConversation(openAI: OpenAI) {
        var attachedMediaBase64: String? = null
        pickedImageState.value?.let { pickedMedia ->
            attachedMediaBase64 = uriConverter.toByteArray(pickedMedia.mediaUri)?.toBase64()
        }
        val temperature = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyTemperature,
            Constants.PreferencesKey.TEMPERATURE_DEFAULT_VALUE
        ).first()
        val topP = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyTopP,
            Constants.PreferencesKey.TOP_P_DEFAULT_VALUE
        ).first()
        val defaultInstructions: String = prepareDefaultInstructions()
        val maxCompletionTokens = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyMaxCompletionTokens,
            Constants.PreferencesKey.MAX_COMPLETION_TOKENS_DEFAULT_VALUE
        ).first()
        val uniqueId = preferenceRepository.getPreference(
            Constants.PreferencesKey.keyUniqueId,
            ""
        ).first().ifEmpty { null }
        val result = openAI.chatCompletion(
            request = ChatCompletionRequest(
                model = ModelId(selectedAiModel.value),
                messages = buildList {
                    add(
                        chatMessage {
                            role = ChatRole.System
                            content {
                                text(defaultInstructions)
                            }
                        }
                    )
                    add(chatMessage {
                        role = ChatRole.User
                        content {
                            with(commandTextState) {
                                if (attachedMediaBase64 != null) {
                                    image("${Constants.IMAGE_BASE64_PREFIX}$attachedMediaBase64")
                                }
                                text(text.toString())
                            }
                        }
                    })
                },
                temperature = temperature,
                topP = topP,
                maxCompletionTokens = maxCompletionTokens,
                n = Constants.ONE,
                user = uniqueId,
                responseFormat = ChatResponseFormat.Text
            )
        )
        result.choices.firstOrNull()?.let { choice ->
            typeAnswer(choice.message.content.orEmpty())
        }
        uiModeState.value = UiMode.Answer
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
        viewModelScope.launch(exceptionHandler) {
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

    fun onTextToSpeechClicked() {
        /**
         * Disable the speech to text until would be ready.
         */
        messageEventChannel.trySend(resourceProvider.getString(R.string.error_speech_to_text_not_ready))
    }

    data class UiState(
        val headlineTitle: UiText,
        val commandTextState: TextFieldState,
        val answerTextState: RichTextState,
        val pickedMedia: PickedMedia? = null,
        val makeFullScreen: Boolean = false,
        val commandOptionsEnabled: Boolean = false,
        val commandOptions: PersistentList<PreferenceItem> = persistentListOf(),
        val uiMode: UiMode = UiMode.Ask
    )
}