package com.baset.anki.pro.generator.presentation.ui.main

import androidx.compose.runtime.Immutable

@Immutable
data class MainUiState(
    val preferencesUiState: PreferencesUiState = PreferencesUiState()
)
