package com.baset.ai.dict.presentation.ui.ai

import android.os.Bundle
import android.speech.RecognitionListener
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.baset.ai.dict.R
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.presentation.ui.ai.model.PickedMedia
import com.baset.ai.dict.presentation.ui.ai.model.UiMode
import com.baset.ai.dict.presentation.ui.core.component.RadioOptionDialog
import com.baset.ai.dict.presentation.ui.core.modifier.animatedGradient
import com.baset.ai.dict.presentation.ui.core.modifier.conditional
import com.baset.ai.dict.presentation.ui.core.theme.AskBorderRadius
import com.baset.ai.dict.presentation.ui.core.theme.DisabledBlackItemBackgroundColor
import com.baset.ai.dict.presentation.ui.core.theme.DisabledItemAlpha
import com.baset.ai.dict.presentation.ui.core.theme.GradientLoadingBackground
import com.baset.ai.dict.presentation.ui.core.theme.IconBackground
import com.baset.ai.dict.presentation.ui.core.theme.Purple80
import com.baset.ai.dict.presentation.ui.core.theme.SemiTransparentBackground
import com.baset.ai.dict.presentation.ui.core.theme.Typography
import com.baset.ai.dict.presentation.ui.core.theme.aiToolsCardRadius
import com.baset.ai.dict.presentation.ui.core.theme.answerOptionItemSize
import com.baset.ai.dict.presentation.ui.core.theme.appLogoSizeForAskAi
import com.baset.ai.dict.presentation.ui.core.theme.askAiCardElevation
import com.baset.ai.dict.presentation.ui.core.theme.askAiCardRadius
import com.baset.ai.dict.presentation.ui.core.theme.gradientLoadingCornerRadius
import com.baset.ai.dict.presentation.ui.core.theme.gradientLoadingHeight
import com.baset.ai.dict.presentation.ui.core.theme.margin12
import com.baset.ai.dict.presentation.ui.core.theme.margin16
import com.baset.ai.dict.presentation.ui.core.theme.margin4
import com.baset.ai.dict.presentation.ui.core.theme.margin8
import com.baset.ai.dict.presentation.ui.core.theme.pickedMediaRadius
import com.baset.ai.dict.presentation.ui.main.PreferenceItem
import com.baset.ai.dict.presentation.util.LifecycleAwareSpeechRecognizer
import com.baset.ai.dict.presentation.util.randomStringUUID
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiRoute(
    modifier: Modifier = Modifier, viewModel: AiViewModel, onOutSideClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        remember(viewModel) { viewModel::onPickMediaResult })
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareSpeechRecognizer = remember {
        LifecycleAwareSpeechRecognizer.Builder(context, lifecycleOwner)
            .recognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onBeginningOfSpeech() {
                    TODO("Not yet implemented")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    TODO("Not yet implemented")
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    TODO("Not yet implemented")
                }

                override fun onEndOfSpeech() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: Int) {
                    TODO("Not yet implemented")
                }

                override fun onResults(results: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    TODO("Not yet implemented")
                }

            })
            .partialRecognitionResults(true)
            .build()
    }
    val isTextToSpeechSupported by produceState(
        initialValue = false,
        key1 = lifecycleAwareSpeechRecognizer
    ) {
        lifecycleAwareSpeechRecognizer.checkRecognitionSupported().collect {
            value = it.supported
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.messageEventFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    AiScreen(
        modifier = modifier,
        makeFullScreen = uiState.makeFullScreen,
        headlineTitle = uiState.headlineTitle.asString(),
        commandTextState = uiState.commandTextState,
        answerState = uiState.answerTextState,
        pickedMedia = uiState.pickedMedia,
        isSpeechToTextSupported = isTextToSpeechSupported,
        onOutSideClicked = onOutSideClicked,
        commandOptions = uiState.commandOptions,
        commandOptionsEnabled = uiState.commandOptionsEnabled,
        uiMode = uiState.uiMode,
        onAskCriteriaClicked = remember(viewModel) { viewModel::onAskCriteriaClicked },
        onMakeFulScreenClicked = remember(viewModel) { viewModel::onMakeFulScreenClicked },
        onTextToSpeechClicked = {
            lifecycleAwareSpeechRecognizer.startListening()
        },
        onOpenGalleryClicked = {
            pickMedia.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    .setDefaultTab(ActivityResultContracts.PickVisualMedia.DefaultTab.PhotosTab)
                    .build()
            )
        },
        onRemovePickedMediaClicked = remember(viewModel) { viewModel::onRemovePickedMediaClicked },
        onSendCommandClicked = remember(viewModel) { viewModel::onSendCommandClicked },
        onGoogleResultClicked = remember(viewModel) { viewModel::onGoogleResultClicked },
        onShareTextClicked = remember(viewModel) { viewModel::onShareTextClicked },
        onCopyTextClicked = remember(viewModel) { viewModel::onCopyTextClicked },
        onCreateAnkiCardClicked = remember(viewModel) { viewModel::onCreateAnkiCardClicked },
        onSaveAsCardClicked = remember(viewModel) { viewModel::onSaveAsCardClicked },
        onAskAnotherQuestionClicked = remember(viewModel) { viewModel::onAskAnotherQuestionClicked }
    )
}

@Composable
private fun AiScreen(
    modifier: Modifier = Modifier,
    makeFullScreen: Boolean,
    headlineTitle: String,
    commandTextState: TextFieldState,
    answerState: RichTextState,
    pickedMedia: PickedMedia?,
    isSpeechToTextSupported: Boolean,
    commandOptions: ImmutableList<PreferenceItem>,
    commandOptionsEnabled: Boolean,
    uiMode: UiMode,
    onOutSideClicked: () -> Unit,
    onAskCriteriaClicked: () -> Unit,
    onMakeFulScreenClicked: () -> Unit,
    onTextToSpeechClicked: () -> Unit,
    onOpenGalleryClicked: () -> Unit,
    onRemovePickedMediaClicked: () -> Unit,
    onSendCommandClicked: () -> Unit,
    onGoogleResultClicked: () -> Unit,
    onShareTextClicked: () -> Unit,
    onCopyTextClicked: () -> Unit,
    onCreateAnkiCardClicked: () -> Unit,
    onSaveAsCardClicked: () -> Unit,
    onAskAnotherQuestionClicked: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .background(SemiTransparentBackground)
            .clickable(
                onClick = onOutSideClicked,
                interactionSource = interactionSource,
                indication = null
            ), contentAlignment = Alignment.BottomCenter
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        AskAiCard(
            modifier = Modifier
                .animateContentSize(alignment = Alignment.BottomCenter)
                .padding(
                    start = margin12,
                    end = margin12
                )
                .fillMaxWidth()
                .conditional(
                    condition = makeFullScreen,
                    modifierTrue = { fillMaxHeight() },
                    modifierFalse = {
                        heightIn(
                            min = ((50 * screenHeight) / 100).dp,
                            max = ((80 * screenHeight) / 100).dp
                        )
                    })
                .systemBarsPadding()
                .clickable(
                    onClick = onAskCriteriaClicked,
                    interactionSource = interactionSource,
                    indication = null
                ),
            headlineTitle = headlineTitle,
            commandTextState = commandTextState,
            answerState = answerState,
            pickedMedia = pickedMedia,
            isSpeechToTextSupported = isSpeechToTextSupported,
            commandOptions = commandOptions,
            commandOptionsEnabled = commandOptionsEnabled,
            uiMode = uiMode,
            onMakeFulScreenClicked = onMakeFulScreenClicked,
            onRemovePickedMediaClicked = onRemovePickedMediaClicked,
            onTextToSpeechClicked = onTextToSpeechClicked,
            onOpenGalleryClicked = onOpenGalleryClicked,
            onSendCommandClicked = onSendCommandClicked,
            onGoogleResultClicked = onGoogleResultClicked,
            onShareTextClicked = onShareTextClicked,
            onCopyTextClicked = onCopyTextClicked,
            onCreateAnkiCardClicked = onCreateAnkiCardClicked,
            onSaveAsCardClicked = onSaveAsCardClicked,
            onAskAnotherQuestionClicked = onAskAnotherQuestionClicked
        )
    }
}

@Composable
private fun AskAiCard(
    modifier: Modifier,
    headlineTitle: String,
    commandTextState: TextFieldState,
    answerState: RichTextState,
    pickedMedia: PickedMedia?,
    isSpeechToTextSupported: Boolean,
    commandOptions: ImmutableList<PreferenceItem>,
    commandOptionsEnabled: Boolean,
    uiMode: UiMode,
    onMakeFulScreenClicked: () -> Unit,
    onRemovePickedMediaClicked: () -> Unit,
    onTextToSpeechClicked: () -> Unit,
    onOpenGalleryClicked: () -> Unit,
    onSendCommandClicked: () -> Unit,
    onGoogleResultClicked: () -> Unit,
    onShareTextClicked: () -> Unit,
    onCopyTextClicked: () -> Unit,
    onCreateAnkiCardClicked: () -> Unit,
    onSaveAsCardClicked: () -> Unit,
    onAskAnotherQuestionClicked: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .shadow(
                elevation = askAiCardElevation,
                shape = RoundedCornerShape(askAiCardRadius)
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(askAiCardRadius)
            )
            .padding(margin16)
    ) {
        when (uiMode) {
            is UiMode.Answer -> AnswerMode(
                answerState = answerState,
                onMakeFulScreenClicked = onMakeFulScreenClicked,
                onGoogleResultClicked = onGoogleResultClicked,
                onShareTextClicked = onShareTextClicked,
                onCopyTextClicked = onCopyTextClicked,
                onCreateAnkiCardClicked = onCreateAnkiCardClicked,
                onSaveAsCardClicked = onSaveAsCardClicked,
                onAskAnotherQuestionClicked = onAskAnotherQuestionClicked
            )

            is UiMode.Ask -> AskMode(
                headlineTitle = headlineTitle,
                commandTextState = commandTextState,
                pickedMedia = pickedMedia,
                isSpeechToTextSupported = isSpeechToTextSupported,
                commandOptions = commandOptions,
                commandOptionsEnabled = commandOptionsEnabled,
                onMakeFulScreenClicked = onMakeFulScreenClicked,
                onRemovePickedMediaClicked = onRemovePickedMediaClicked,
                onTextToSpeechClicked = onTextToSpeechClicked,
                onOpenGalleryClicked = onOpenGalleryClicked,
                onSendCommandClicked = onSendCommandClicked
            )

            is UiMode.Loading -> LoadingMode(
                onMakeFulScreenClicked = onMakeFulScreenClicked
            )

            is UiMode.Error -> ErrorMode(
                answerState = answerState,
                onMakeFulScreenClicked = onMakeFulScreenClicked,
                onTryAgainClicked = onSendCommandClicked
            )
        }
    }
}

@Composable
private fun ConstraintLayoutScope.ErrorMode(
    answerState: RichTextState,
    onMakeFulScreenClicked: () -> Unit,
    onTryAgainClicked: () -> Unit
) {
    val (headerRef,
        spacerRef,
        answerInputRef,
        bottomBarRef
    ) = createRefs()
    HeaderSection(
        modifier = Modifier
            .constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
        headlineTitle = "",
        onMakeFulScreenClicked = onMakeFulScreenClicked
    )

    Spacer(
        modifier = Modifier
            .constrainAs(spacerRef) {
                top.linkTo(headerRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.value(margin16)
            }
    )

    AnswerText(
        modifier = Modifier
            .constrainAs(answerInputRef) {
                top.linkTo(spacerRef.bottom)
                width = Dimension.matchParent
                height = Dimension.wrapContent
            },
        state = answerState
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .constrainAs(bottomBarRef) {
                width = Dimension.wrapContent
                height = Dimension.wrapContent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .clickable(
                onClick = onTryAgainClicked,
                indication = null,
                interactionSource = interactionSource
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.baseline_refresh_24),
            contentDescription = stringResource(R.string.content_description_try_again)
        )
        Spacer(modifier = Modifier.size(margin4))
        Text(
            text = stringResource(R.string.content_description_try_again),
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ConstraintLayoutScope.AnswerMode(
    answerState: RichTextState,
    onMakeFulScreenClicked: () -> Unit,
    onGoogleResultClicked: () -> Unit,
    onShareTextClicked: () -> Unit,
    onCopyTextClicked: () -> Unit,
    onCreateAnkiCardClicked: () -> Unit,
    onSaveAsCardClicked: () -> Unit,
    onAskAnotherQuestionClicked: () -> Unit
) {
    val (headerRef,
        spacerRef,
        answerColumnRef,
        bottomBarRef
    ) = createRefs()
    HeaderSection(
        modifier = Modifier
            .constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
        headlineTitle = "",
        onMakeFulScreenClicked = onMakeFulScreenClicked
    )

    Spacer(
        modifier = Modifier
            .constrainAs(spacerRef) {
                top.linkTo(headerRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.value(margin16)
            }
    )

    LazyColumn(modifier = Modifier.constrainAs(answerColumnRef) {
        top.linkTo(spacerRef.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(bottomBarRef.top, margin12)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }) {
        item {
            AnswerText(
                modifier = Modifier.fillMaxWidth(),
                state = answerState
            )
        }
        item {
            Spacer(modifier = Modifier.size(margin12))
        }
        item {
            AnswerOptions(
                modifier = Modifier.fillMaxWidth(),
                onGoogleResultClicked = onGoogleResultClicked,
                onShareTextClicked = onShareTextClicked,
                onCopyTextClicked = onCopyTextClicked,
                onCreateAnkiCardClicked = onCreateAnkiCardClicked,
                onSaveAsCardClicked = onSaveAsCardClicked
            )
        }

        item {
            Spacer(modifier = Modifier.size(margin12))
        }
    }

    AskAiBottomBar(
        modifier = Modifier.constrainAs(bottomBarRef) {
            width = Dimension.matchParent
            height = Dimension.wrapContent
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        },
        onAskAnotherQuestionClicked = onAskAnotherQuestionClicked
    )
}

@NonRestartableComposable
@Composable
private fun AskAiBottomBar(
    modifier: Modifier = Modifier,
    onAskAnotherQuestionClicked: () -> Unit
) {
    Row(modifier = modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .weight(1f)
                .border(
                    border = BorderStroke(
                        width = AskBorderRadius,
                        color = DisabledBlackItemBackgroundColor
                    ),
                    shape = RoundedCornerShape(aiToolsCardRadius)
                )
                .padding(margin12)
                .clickable(
                    onClick = onAskAnotherQuestionClicked,
                    indication = null,
                    interactionSource = interactionSource
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier.alpha(DisabledItemAlpha),
                color = Color.Black,
                text = stringResource(R.string.title_ask_ai)
            )
        }
        Spacer(modifier = Modifier.size(margin8))
        IconButton(
            enabled = false,
            onClick = {},
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_send_24),
                contentDescription = stringResource(R.string.content_description_send_command)
            )
        }
    }
}

@Composable
private fun AnswerOptions(
    modifier: Modifier,
    onGoogleResultClicked: () -> Unit,
    onShareTextClicked: () -> Unit,
    onCopyTextClicked: () -> Unit,
    onCreateAnkiCardClicked: () -> Unit,
    onSaveAsCardClicked: () -> Unit
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            IconButton(
                modifier = Modifier
                    .requiredSize(answerOptionItemSize),
                onClick = onGoogleResultClicked
            ) {
                Image(
                    modifier = Modifier.padding(margin4),
                    painter = painterResource(R.drawable.ic_google_logo),
                    contentDescription = stringResource(R.string.content_description_google_result)
                )
            }
        }
        item {
            Spacer(Modifier.size(margin12))
        }
        item {
            IconButton(
                modifier = Modifier.requiredSize(answerOptionItemSize),
                onClick = onShareTextClicked
            ) {
                Image(
                    modifier = Modifier.padding(margin4),
                    painter = painterResource(R.drawable.baseline_share_24),
                    contentDescription = stringResource(R.string.content_description_share_result)
                )
            }
        }

        item {
            Spacer(Modifier.size(margin12))
        }

        item {
            IconButton(
                modifier = Modifier.requiredSize(answerOptionItemSize),
                onClick = onCopyTextClicked
            ) {
                Image(
                    modifier = Modifier.padding(margin4),
                    painter = painterResource(R.drawable.baseline_content_copy_24),
                    contentDescription = stringResource(R.string.content_description_copy)
                )
            }
        }

        item {
            Spacer(Modifier.size(margin12))
        }

        item {
            IconButton(
                modifier = Modifier.requiredSize(answerOptionItemSize),
                onClick = onCreateAnkiCardClicked
            ) {
                Image(
                    modifier = Modifier.padding(margin4),
                    painter = painterResource(R.drawable.ic_anki_logo),
                    contentDescription = stringResource(R.string.content_description_anki)
                )
            }
        }

        item {
            Spacer(Modifier.size(margin12))
        }

        item {
            IconButton(
                modifier = Modifier.requiredSize(answerOptionItemSize),
                onClick = onSaveAsCardClicked
            ) {
                Image(
                    modifier = Modifier.padding(margin4),
                    painter = painterResource(R.drawable.baseline_save_24),
                    contentDescription = stringResource(R.string.content_save_as_temporary_card)
                )
            }
        }
    }
}

@Composable
private fun ConstraintLayoutScope.AskMode(
    headlineTitle: String,
    commandTextState: TextFieldState,
    pickedMedia: PickedMedia?,
    isSpeechToTextSupported: Boolean,
    commandOptions: ImmutableList<PreferenceItem>,
    commandOptionsEnabled: Boolean,
    onMakeFulScreenClicked: () -> Unit,
    onRemovePickedMediaClicked: () -> Unit,
    onTextToSpeechClicked: () -> Unit,
    onOpenGalleryClicked: () -> Unit,
    onSendCommandClicked: () -> Unit
) {
    val (headerRef,
        spacerRef,
        pickedMediaRef,
        secondSpacer,
        commandInputRef,
        bottomBarRef
    ) = createRefs()
    HeaderSection(
        modifier = Modifier
            .constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
        headlineTitle = headlineTitle,
        commandOptions = commandOptions,
        commandOptionsVisible = true,
        commandOptionsEnabled = commandOptionsEnabled,
        onMakeFulScreenClicked = onMakeFulScreenClicked
    )
    Spacer(
        modifier = Modifier
            .constrainAs(spacerRef) {
                top.linkTo(headerRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.value(margin16)
            }
    )
    if (pickedMedia != null) {
        PickedMediaSection(
            modifier = Modifier
                .constrainAs(pickedMediaRef) {
                    top.linkTo(spacerRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(secondSpacer.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:.4")
                },
            pickedMedia = pickedMedia,
            onRemovePickedMediaClicked = onRemovePickedMediaClicked
        )
        Spacer(
            modifier = Modifier
                .constrainAs(secondSpacer) {
                    top.linkTo(pickedMediaRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(margin16)
                })
    }
    val focusRequester = remember { FocusRequester() }
    val softwareKeyboard = LocalSoftwareKeyboardController.current
    var requestId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(focusRequester, requestId) {
        if (requestId != null) {
            awaitFrame()
            focusRequester.requestFocus()
            softwareKeyboard?.show()
        }
    }
    LaunchedEffect(Unit) {
        awaitFrame()
        requestId = randomStringUUID()
    }
    val isSendEnabled by remember {
        derivedStateOf { commandTextState.text.isNotEmpty() }
    }
    CommandInput(
        modifier = Modifier
            .focusRequester(focusRequester)
            .constrainAs(commandInputRef) {
                top.linkTo(
                    if (pickedMedia == null) {
                        spacerRef
                    } else {
                        secondSpacer
                    }.bottom
                )
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(bottomBarRef.top)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .imePadding(),
        commandTextState = commandTextState,
        onKeyboardActionClicked = {
            if (isSendEnabled) {
                onSendCommandClicked()
            }
        }
    )
    BottomBarTools(
        modifier = Modifier
            .constrainAs(bottomBarRef) {
                width = Dimension.matchParent
                height = Dimension.wrapContent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        disabled = false,
        isSendEnabled = isSendEnabled,
        isSpeechToTextSupported = isSpeechToTextSupported,
        onSendCommandClicked = onSendCommandClicked,
        onOpenKeyboardClicked = {
            requestId = randomStringUUID()
        },
        onTextToSpeechClicked = onTextToSpeechClicked,
        onOpenGalleryClicked = onOpenGalleryClicked
    )
}

@Composable
private fun ConstraintLayoutScope.LoadingMode(
    onMakeFulScreenClicked: () -> Unit
) {
    val (headerRef, loading1, loading2, loading3, bottomBarRef) = createRefs()
    HeaderSection(
        modifier = Modifier
            .constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
        headlineTitle = "",
        onMakeFulScreenClicked = onMakeFulScreenClicked
    )
    Box(
        modifier = Modifier
            .constrainAs(loading1) {
                width = Dimension.matchParent
                height = Dimension.value(gradientLoadingHeight)
                top.linkTo(headerRef.bottom, margin16)
            }
            .animatedGradient(
                primaryColor = GradientLoadingBackground,
                containerColor = Purple80,
                cornerRadius = CornerRadius(gradientLoadingCornerRadius.value)
            )
    )

    Box(
        modifier = Modifier
            .constrainAs(loading2) {
                width = Dimension.matchParent
                height = Dimension.value(gradientLoadingHeight)
                top.linkTo(loading1.bottom, margin16)
            }
            .animatedGradient(
                primaryColor = GradientLoadingBackground,
                containerColor = Purple80,
                cornerRadius = CornerRadius(gradientLoadingCornerRadius.value)
            )
    )
    Box(
        modifier = Modifier
            .constrainAs(loading3) {
                width = Dimension.percent(.7f)
                height = Dimension.value(gradientLoadingHeight)
                top.linkTo(loading2.bottom, margin16)
            }
            .animatedGradient(
                primaryColor = GradientLoadingBackground,
                containerColor = Purple80,
                cornerRadius = CornerRadius(gradientLoadingCornerRadius.value)
            )
    )

    BottomBarTools(
        modifier = Modifier
            .constrainAs(bottomBarRef) {
                width = Dimension.matchParent
                height = Dimension.wrapContent
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        disabled = true
    )
}

@Composable
private fun PickedMediaSection(
    modifier: Modifier, pickedMedia: PickedMedia,
    onRemovePickedMediaClicked: () -> Unit
) {
    Box(modifier = modifier) {
        var showCloseIcon by remember(pickedMedia) { mutableStateOf(false) }
        SubcomposeAsyncImage(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(pickedMediaRadius)),
            contentScale = ContentScale.Crop,
            model = pickedMedia.mediaUri,
            onSuccess = {
                showCloseIcon = true
            },
            onError = {
                showCloseIcon = true
            },
            loading = {
                CircularProgressIndicator(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center)
                )
            },
            contentDescription = stringResource(R.string.content_picked_media_from_gallery)
        )
        if (showCloseIcon) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onRemovePickedMediaClicked
            ) {
                Icon(
                    modifier = Modifier
                        .background(
                            color = IconBackground, shape = CircleShape
                        )
                        .padding(margin4),
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_description_remove_attached_media)
                )
            }
        }
    }
}

@Composable
private fun CommandInput(
    modifier: Modifier = Modifier,
    commandTextState: TextFieldState,
    onKeyboardActionClicked: () -> Unit
) {
    BasicTextField(
        modifier = modifier,
        state = commandTextState,
        textStyle = Typography.bodyLarge,
        decorator = { innerTextField ->
            Box(contentAlignment = Alignment.TopStart) {
                if (commandTextState.text.isEmpty()) {
                    Text(
                        stringResource(R.string.label_type_or_talk), style = Typography.bodyLarge
                    )
                }
                innerTextField()
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            showKeyboardOnFocus = true
        ),
        onKeyboardAction = { onKeyboardActionClicked() }
    )
}

@Composable
private fun HeaderSection(
    modifier: Modifier = Modifier,
    headlineTitle: String,
    onMakeFulScreenClicked: () -> Unit,
    commandOptions: ImmutableList<PreferenceItem> = persistentListOf(),
    commandOptionsVisible: Boolean = false,
    commandOptionsEnabled: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(appLogoSizeForAskAi),
            painter = painterResource(R.drawable.ic_logo),
            tint = colorResource(R.color.black),
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.size(margin8))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = headlineTitle,
            style = Typography.titleSmall,
            textAlign = TextAlign.Start
        )
        if (commandOptionsVisible) {
            var showOptionDialog by remember {
                mutableStateOf(false)
            }
            IconButton(
                modifier = Modifier.size(appLogoSizeForAskAi),
                enabled = commandOptionsEnabled,
                onClick = {
                    showOptionDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.content_description_command_options)
                )
            }
            if (showOptionDialog) {
                val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                val coroutineScale = rememberCoroutineScope()
                RadioOptionDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight / 2),
                    options = commandOptions,
                    onPreferenceItemClicked = {
                        coroutineScale.launch {
                            delay(Constants.DIALOG_DISMISS)
                            showOptionDialog = false
                        }
                    }
                ) {
                    showOptionDialog = false
                }
            }
        }
        Spacer(modifier = Modifier.size(margin12))
        IconButton(
            modifier = Modifier.size(appLogoSizeForAskAi),
            onClick = onMakeFulScreenClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_make_full_screen),
                contentDescription = stringResource(R.string.content_description_full_screen)
            )
        }
    }
}

@Composable
private fun BottomBarTools(
    modifier: Modifier = Modifier,
    disabled: Boolean,
    isSendEnabled: Boolean = false,
    isSpeechToTextSupported: Boolean = false,
    onSendCommandClicked: () -> Unit = {},
    onOpenKeyboardClicked: () -> Unit = {},
    onTextToSpeechClicked: () -> Unit = {},
    onOpenGalleryClicked: () -> Unit = {}
) {
    Box(modifier) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            enabled = isSendEnabled && !disabled,
            onClick = onSendCommandClicked,
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_send_24),
                contentDescription = stringResource(R.string.content_description_send_command)
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = Purple80,
                    shape = RoundedCornerShape(aiToolsCardRadius)
                )
                .padding(margin4),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            IconButton(
                onClick = onOpenKeyboardClicked,
                enabled = !disabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_alt_24),
                    contentDescription = stringResource(R.string.content_description_open_keyboard)
                )
            }
            IconButton(
                onClick = onTextToSpeechClicked,
                enabled = isSpeechToTextSupported && !disabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_mic_none_24),
                    contentDescription = stringResource(R.string.content_description_speech_to_text)
                )
            }
            IconButton(
                onClick = onOpenGalleryClicked,
                enabled = !disabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_image_search_24),
                    contentDescription = stringResource(R.string.content_description_open_gallery)
                )
            }
        }
    }
}

@Composable
fun AnswerText(
    modifier: Modifier,
    state: RichTextState
) {
    BasicRichTextEditor(
        modifier = modifier,
        state = state,
        readOnly = true,
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Preview
@Composable
private fun AiScreenPreview() {
    AskAiCard(
        modifier = Modifier.wrapContentSize(),
        headlineTitle = "Hello World",
        commandTextState = TextFieldState(),
        answerState = rememberRichTextState(),
        pickedMedia = null,
        isSpeechToTextSupported = false,
        commandOptions = persistentListOf(),
        commandOptionsEnabled = true,
        uiMode = UiMode.Ask,
        onMakeFulScreenClicked = {},
        onRemovePickedMediaClicked = {},
        onTextToSpeechClicked = {},
        onOpenGalleryClicked = {},
        onSendCommandClicked = {},
        onGoogleResultClicked = {},
        onShareTextClicked = {},
        onCopyTextClicked = {},
        onCreateAnkiCardClicked = {},
        onSaveAsCardClicked = {},
        onAskAnotherQuestionClicked = {}
    )
}