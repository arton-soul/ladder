package com.dearyoti.lexiladder.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Manages native Android TextToSpeech engine for offline English pronunciation.
 */
class TtsManager private constructor(context: Context) : TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "TtsManager"

        @Volatile
        private var instance: TtsManager? = null

        fun getInstance(context: Context): TtsManager {
            return instance ?: synchronized(this) {
                instance ?: TtsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to instantiate TextToSpeech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val engine = tts ?: return

            // Prefer US English, fallback to UK, then generic English
            val result = engine.setLanguage(Locale.US)
            val isLanguageAvailable = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED

            if (!isLanguageAvailable) {
                val ukResult = engine.setLanguage(Locale.UK)
                val isUkAvailable = ukResult != TextToSpeech.LANG_MISSING_DATA &&
                        ukResult != TextToSpeech.LANG_NOT_SUPPORTED

                if (!isUkAvailable) {
                    engine.setLanguage(Locale.ENGLISH)
                }
            }

            // Optimal speech rate for language learners (slightly relaxed for clarity)
            engine.setSpeechRate(0.88f)
            engine.setPitch(1.0f)

            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    Log.w(TAG, "TTS utterance error: $errorCode for id: $utteranceId")
                }
            })

            _isReady.value = true
            Log.d(TAG, "TextToSpeech initialized successfully with language: ${engine.voice?.locale?.displayName ?: "English"}")
        } else {
            Log.w(TAG, "TextToSpeech initialization failed with status: $status")
            _isReady.value = false
        }
    }

    /**
     * Speaks the English text aloud.
     * @param text The word or sentence to pronounce.
     * @param rate Optional playback speed (default 0.88f).
     */
    fun speak(text: String, rate: Float = 0.88f) {
        val engine = tts
        if (engine == null || !_isReady.value || text.isBlank()) {
            Log.w(TAG, "TTS not ready or empty text: $text")
            return
        }

        try {
            engine.setSpeechRate(rate)
            val utteranceId = "tts_${System.currentTimeMillis()}"
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e(TAG, "Error pronouncing text: ${e.message}")
        }
    }

    /**
     * Stops current speech.
     */
    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS: ${e.message}")
        }
    }

    /**
     * Releases TTS resources.
     */
    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            _isReady.value = false
            _isSpeaking.value = false
            instance = null
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS: ${e.message}")
        }
    }
}
