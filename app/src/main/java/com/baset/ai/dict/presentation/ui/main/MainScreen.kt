package com.baset.ai.dict.presentation.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.baset.ai.dict.R
import com.baset.ai.dict.domain.entity.Card
import com.baset.ai.dict.presentation.ui.core.theme.SwipeDeleteBackground
import com.baset.ai.dict.presentation.ui.core.theme.SwipeShareBackground
import com.baset.ai.dict.presentation.ui.core.theme.Typography
import com.baset.ai.dict.presentation.ui.core.theme.margin12
import com.baset.ai.dict.presentation.ui.core.theme.margin8
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MainRoute(
    modifier: Modifier,
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(
        modifier = modifier,
        preferencesUiState = uiState.preferencesUiState,
        cards = uiState.cards,
        onPreferenceSwitchCheckChange = remember(mainViewModel) { mainViewModel::onPreferenceSwitchCheckChange },
        onPreferenceOptionItemSelected = remember(mainViewModel) { mainViewModel::onPreferenceOptionItemSelected },
        onInputPreferenceDone = remember(mainViewModel) { mainViewModel::onInputPreferenceDone },
        onOpenProjectOnGithubClicked = remember(mainViewModel) { mainViewModel::onOpenProjectOnGithubClicked },
        onSwipedToShareCardToAnki = remember(mainViewModel) { mainViewModel::onSwipedToShareCardToAnki },
        onSwipedToDeleteCard = remember(mainViewModel) { mainViewModel::onSwipedToDeleteCard }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    preferencesUiState: PreferencesUiState,
    cards: ImmutableList<Card>,
    onPreferenceSwitchCheckChange: (PreferenceItem) -> Unit,
    onPreferenceOptionItemSelected: (preferenceItem: PreferenceItem, optionItem: OptionItem) -> Unit,
    onInputPreferenceDone: (PreferenceItem, String) -> Unit,
    onOpenProjectOnGithubClicked: () -> Unit,
    onSwipedToShareCardToAnki: (Card) -> Boolean,
    onSwipedToDeleteCard: (Card) -> Boolean
) {
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = Typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        showOptionsBottomSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_description_preferences)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showOptionsBottomSheet) {
            ModalBottomSheet(modifier = Modifier.padding(innerPadding),
                onDismissRequest = {
                    showOptionsBottomSheet = false
                }) {
                PreferencesModalBottomSheetContent(
                    preferenceItems = preferencesUiState.preferenceItems,
                    onPreferenceSwitchCheckChange = onPreferenceSwitchCheckChange,
                    onPreferenceOptionItemSelected = onPreferenceOptionItemSelected,
                    onInputPreferenceDone = onInputPreferenceDone,
                    onOpenProjectOnGithubClicked = onOpenProjectOnGithubClicked
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(margin12)
        ) {
            itemsIndexed(cards, key = { _, item -> item.id }) { index, card ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        return@rememberSwipeToDismissBoxState when (dismissValue) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onSwipedToShareCardToAnki(card)
                            }

                            SwipeToDismissBoxValue.EndToStart -> {
                                onSwipedToDeleteCard(card)
                            }

                            else -> false
                        }
                    }
                )
                SwipeToDismissBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> SwipeShareBackground
                                SwipeToDismissBoxValue.EndToStart -> SwipeDeleteBackground
                                SwipeToDismissBoxValue.Settled -> Color.Transparent
                            },
                            label = "CardSwipeActionColorAnimation"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = color,
                                    shape = CardDefaults.shape
                                )
                                .padding(margin12),
                            contentAlignment = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                SwipeToDismissBoxValue.Settled -> Alignment.Center
                            }
                        ) {
                            when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    Image(
                                        painter = painterResource(R.drawable.ic_anki_logo),
                                        contentDescription = stringResource(R.string.content_description_swipe_to_add_anki)
                                    )
                                }

                                SwipeToDismissBoxValue.EndToStart,
                                SwipeToDismissBoxValue.Settled -> {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.content_description_swipe_to_delete_card)
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Column(modifier = Modifier.padding(margin12)) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                text = card.front,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.size(margin8))
                            val backState = rememberRichTextState()
                            LaunchedEffect(card) {
                                backState.setMarkdown(card.back)
                            }
                            RichText(
                                modifier = Modifier.fillMaxWidth(),
                                state = backState,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                if (index != cards.lastIndex) {
                    Spacer(modifier = Modifier.size(margin8))
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    MainScreen(
        preferencesUiState = PreferencesUiState(),
        cards = persistentListOf(),
        onPreferenceSwitchCheckChange = {},
        onPreferenceOptionItemSelected = { _, _ -> },
        onInputPreferenceDone = { _, _ -> },
        onOpenProjectOnGithubClicked = {},
        onSwipedToDeleteCard = { _ -> true },
        onSwipedToShareCardToAnki = { _ -> true }
    )
}