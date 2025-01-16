package com.baset.anki.pro.generator.presentation.ui.main

import androidx.compose.runtime.Immutable
import com.baset.anki.pro.generator.domain.entity.Card
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MainUiState(
    val cards: ImmutableList<Card> = persistentListOf(),
    val preferencesUiState: PreferencesUiState = PreferencesUiState()
)
