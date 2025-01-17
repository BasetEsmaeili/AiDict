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
        val options: ImmutableList<OptionItem>
    ) : PreferenceItem {
        override val type: String
            get() = "option_dialog"
    }

    data class Switch(
        override val id: String,
        val checked: Boolean,
        val title: UiText,
        val description: UiText?
    ) : PreferenceItem {
        override val type: String
            get() = "switch"
    }

    data class Input(
        override val id: String,
        val title: UiText,
        val description: UiText?,
        val inputType: InputType,
        val text: String?
    ) : PreferenceItem {
        enum class InputType {
            Text, Number
        }

        override val type: String
            get() = "input"
    }
}