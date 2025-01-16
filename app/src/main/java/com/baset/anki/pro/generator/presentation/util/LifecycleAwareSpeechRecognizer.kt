package com.baset.anki.pro.generator.presentation.util

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognitionSupport
import android.speech.RecognitionSupportCallback
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.baset.anki.pro.generator.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class LifecycleAwareSpeechRecognizer private constructor(private val builder: Builder) {
    class Builder(
        val context: Context,
        val lifecycleOwner: LifecycleOwner
    ) {
        var locale: Locale = Locale.getDefault()
            private set
        var languageModel: String = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            private set
        var promptText: String = context.getString(R.string.label_say_some_thing)
            private set
        var partialRecognitionResults: Boolean = true
            private set
        var recognitionListener: RecognitionListener? = null
            private set

        fun locale(locale: Locale): Builder {
            this.locale = locale
            return this
        }

        fun languageModel(languageModel: String): Builder {
            this.languageModel = languageModel
            return this
        }

        fun promptText(promptText: String): Builder {
            this.promptText = promptText
            return this
        }

        fun partialRecognitionResults(partialRecognitionResults: Boolean): Builder {
            this.partialRecognitionResults = partialRecognitionResults
            return this
        }

        fun recognitionListener(recognitionListener: RecognitionListener): Builder {
            this.recognitionListener = recognitionListener
            return this
        }

        fun build(): LifecycleAwareSpeechRecognizer {
            return LifecycleAwareSpeechRecognizer(this)
        }

    }

    private lateinit var speechRecognizer: SpeechRecognizer

    private val recognizerIntent: Intent
        get() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                builder.languageModel
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                builder.locale
            )
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                builder.promptText
            )
            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                builder.partialRecognitionResults
            )
            putExtra(
                RecognizerIntent.EXTRA_CALLING_PACKAGE,
                builder.context.packageName
            )
        }

    init {
        builder.lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(builder.context)
                speechRecognizer.setRecognitionListener(builder.recognitionListener)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                speechRecognizer.destroy()
            }
        })
    }

    fun startListening() {
        require(this::speechRecognizer.isInitialized) {
            "The speechRecognizer has not been initialized!"
        }
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopListening() {
        require(this::speechRecognizer.isInitialized) {
            "The speechRecognizer has not been initialized!"
        }
        speechRecognizer.stopListening()
    }

    fun cancel() {
        require(this::speechRecognizer.isInitialized) {
            "The speechRecognizer has not been initialized!"
        }
        speechRecognizer.cancel()
    }

    fun checkRecognitionSupported() = callbackFlow {
        if (isSdk33OrUp) {
            speechRecognizer.checkRecognitionSupport(
                recognizerIntent,
                builder.context.mainExecutor,
                object : RecognitionSupportCallback {
                    override fun onSupportResult(recognitionSupport: RecognitionSupport) {
                        val languages = recognitionSupport.supportedOnDeviceLanguages +
                                recognitionSupport.installedOnDeviceLanguages +
                                recognitionSupport.onlineLanguages
                        trySend(
                            SupportResult(
                                supported = languages.any { it.contains(builder.locale.toLanguageTag()) }
                            )
                        )
                    }

                    override fun onError(error: Int) {
                        trySend(
                            SupportResult(
                                supported = false,
                                errorCode = error
                            )
                        )
                    }
                }
            )
        } else {
            trySend(SupportResult(SpeechRecognizer.isRecognitionAvailable(builder.context)))
        }
        awaitClose()
    }

    data class SupportResult(
        val supported: Boolean = false,
        val errorCode: Int? = null
    )
}