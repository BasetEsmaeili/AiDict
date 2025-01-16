package com.baset.anki.pro.generator.presentation.ui.core.modifier

import androidx.compose.ui.Modifier

fun Modifier.conditional(
    condition: Boolean,
    modifierTrue: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifierTrue(Modifier))
    } else {
        this
    }
}

fun Modifier.conditional(
    condition: Boolean,
    modifierTrue: Modifier.() -> Modifier,
    modifierFalse: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifierTrue(Modifier))
    } else {
        then(modifierFalse(Modifier))
    }
}
