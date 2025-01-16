package com.baset.anki.pro.generator.presentation.ui.main

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PreferencesUiState(
    val preferenceItems: PersistentList<PreferenceItem> = persistentListOf()
)

