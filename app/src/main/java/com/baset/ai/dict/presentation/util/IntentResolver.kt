package com.baset.ai.dict.presentation.util

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.baset.ai.dict.common.Constants

class IntentResolver(private val context: Context) {
    fun googleResult(query: String): Boolean {
        return launchIntent {
            action = Intent.ACTION_WEB_SEARCH
            putExtra(SearchManager.QUERY, query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun shareText(
        chooserTitle: String,
        textToShare: String
    ): Boolean {
        return launchChooserIntent(chooserTitle) {
            action = Intent.ACTION_SEND
            type = Constants.Intent.PLAIN_TEXT
            putExtra(Intent.EXTRA_TEXT, textToShare)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun createAnkiCard(
        front: String,
        back: String
    ): Boolean {
        return launchIntent {
            action = Constants.Intent.ANKI_ACTION
            putExtra(Constants.Intent.CARD_FRONT, front)
            putExtra(Constants.Intent.CARD_BACK, back)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            `package` = Constants.Intent.ANKI_PACKAGE
        }
    }

    fun openProjectOnGithub() {
        launchIntent {
            action = Intent.ACTION_VIEW
            data = Constants.Intent.GITHUB_URI.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    private fun launchIntent(intent: Intent.() -> Unit): Boolean {
        return try {
            context.startActivity(Intent().apply(intent))
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun launchChooserIntent(
        chooserTitle: String,
        intent: Intent.() -> Unit
    ): Boolean {
        return try {
            context.startActivity(Intent.createChooser(Intent().apply(intent), chooserTitle))
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}