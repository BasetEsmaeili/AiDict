package com.baset.anki.pro.generator.presentation.ui.core.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

fun Modifier.enabled(
    condition: Boolean,
    enabledAlpha: Float = 1f,
    disabledAlpha: Float = 0.5f
): Modifier =
    alpha(
        if (condition) {
            enabledAlpha
        } else {
            disabledAlpha
        }
    )
