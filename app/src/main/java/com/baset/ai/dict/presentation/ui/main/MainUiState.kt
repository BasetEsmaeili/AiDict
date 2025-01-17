package com.baset.ai.dict.presentation.ui.main

import androidx.compose.runtime.Immutable
import com.baset.ai.dict.domain.entity.Card
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MainUiState(
    val cards: ImmutableList<Card> = persistentListOf(),
    val preferencesUiState: PreferencesUiState = PreferencesUiState()
)
