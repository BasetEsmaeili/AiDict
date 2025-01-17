package com.baset.ai.dict.presentation.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

class ClipboardManager(context: Context) {
    private val clipboardManager by lazyFast {
        ContextCompat.getSystemService(context, ClipboardManager::class.java)
    }

    fun copyTextToClipboard(
        label: String,
        textToCopy: String
    ) {
        clipboardManager?.setPrimaryClip(ClipData.newPlainText(label, textToCopy))
    }

    fun copyFormattedTextToClipboard(
        label: String,
        plainTextToCopy: String,
        htmlTextToCopy: String
    ) {
        clipboardManager?.setPrimaryClip(
            ClipData.newHtmlText(
                label,
                plainTextToCopy,
                htmlTextToCopy
            )
        )
    }
}