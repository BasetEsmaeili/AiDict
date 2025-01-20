package com.baset.ai.dict.presentation.ui.main

import com.baset.ai.dict.presentation.ui.core.model.UiText
import kotlinx.collections.immutable.ImmutableList

sealed interface PreferenceItem {
    val id: String
    val type: String

    data class OptionDialog(
        override val id: String,
        val title: UiText,
        val description: UiText?,
        val options: ImmutableList<OptionItem>,
        val onPreferenceOptionItemSelected: (OptionDialog, optionItem: OptionItem) -> Unit
    ) : PreferenceItem {
        override val type: String
            get() = "option_dialog"
    }

    data class Switch(
        override val id: String,
        val checked: Boolean,
        val title: UiText,
        val description: UiText?,
        val onPreferenceSwitchCheckChange: (Switch) -> Unit
    ) : PreferenceItem {
        override val type: String
            get() = "switch"
    }

    data class Input(
        override val id: String,
        val title: UiText,
        val description: UiText?,
        val inputType: InputType,
        val text: String?,
        val onInputPreferenceDone: (Input, String) -> Unit
    ) : PreferenceItem {
        enum class InputType {
            Text, Number
        }

        override val type: String
            get() = "input"
    }

    data class CopyRight(
        override val id: String,
        val onOpenProjectOnGithubClicked: () -> Unit
    ) : PreferenceItem {
        override val type: String
            get() = "copyright"
    }
}