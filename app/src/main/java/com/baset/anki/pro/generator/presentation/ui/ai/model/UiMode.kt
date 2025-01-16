package com.baset.anki.pro.generator.presentation.ui.ai.model

sealed class UiMode {
    data object Loading : UiMode()
    data object Ask : UiMode()
    data object Answer : UiMode()
    data object Error : UiMode()
}