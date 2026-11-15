package com.maimuna.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var speech: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private lateinit var transcript: TextView
    private lateinit var listenButton: Button
    private var volumeDownAt = 0L
    private var volumeUpAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildScreen()
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speech = SpeechRecognizer.createSpeechRecognizer(this)
            speech.setRecognitionListener(listener)
        } else status.text = "इस डिवाइस पर Speech Recognition उपलब्ध नहीं है"
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 71)
        }
    }

    private fun buildScreen() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 48, 28, 32)
            setBackgroundColor(Color.rgb(5, 10, 22))
        }
        val title = TextView(this).apply {
            text = "J.A.R.V.I.S"
            textSize = 32f
            setTextColor(Color.rgb(80, 210, 255))
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "आपका AI Assistant तैयार है"
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        transcript = TextView(this).apply {
            text = "माइक दबाकर हिंदी या English में बोलें"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }
        listenButton = Button(this).apply {
            text = "🎙  JARVIS से बात करें"
            textSize = 18f
            setOnClickListener { startListening() }
        }
        layout.addView(title, LinearLayout.LayoutParams(-1, -2))
        layout.addView(status, LinearLayout.LayoutParams(-1, -2))
        layout.addView(transcript, LinearLayout.LayoutParams(-1, 0, 1f))
        layout.addView(listenButton, LinearLayout.LayoutParams(-1, -2))
        setContentView(layout)
    }

    private fun startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 71); return
        }
        status.text = "सुन रहा हूँ…"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        speech.startListening(intent)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { status.text = "बोलिए, मैं सुन रहा हूँ" }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { status.text = "समझ रहा हूँ…" }
        override fun onError(error: Int) { status.text = "आवाज़ समझ नहीं आई। फिर कोशिश करें।" }
        override fun onResults(results: Bundle?) {
            val words = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            transcript.text = words
            status.text = "आपने कहा:"
            if (words.isNotBlank()) {
                val reply = basicReply(words)
                transcript.text = "$words\n\n$reply"
                speak(reply)
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    // Stage 1 fallback only. Cloud AI connection will be added in the next stage.
    private fun basicReply(input: String): String {
        val s = input.lowercase(Locale.ROOT)
        return when {
            s.contains("समय") || s.contains("time") -> "अभी समय ${java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(java.util.Date())} है।"
            s.contains("नमस्ते") || s.contains("hello") || s.contains("हेलो") -> "नमस्ते सर! मैं JARVIS हूँ। बताइए, क्या मदद करूँ?"
            s.contains("तुम कौन") || s.contains("who are you") -> "मैं आपका JARVIS AI Assistant हूँ।"
            else -> "मैंने आपकी बात सुनी: $input। अगले चरण में मेरा AI Brain जोड़ा जाएगा।"
        }
    }

    private fun speak(text: String) {
        if (::tts.isInitialized) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply")
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            tts.language = Locale("hi", "IN")
        }
    }

    // Volume-key long-press gesture is supported only while this Activity is foreground.
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val now = System.currentTimeMillis()
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) volumeDownAt = now
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) volumeUpAt = now
        if (volumeDownAt > 0 && volumeUpAt > 0 && kotlin.math.abs(volumeDownAt - volumeUpAt) < 5000) {
            status.text = "JARVIS सक्रिय — बोलिए"
            startListening()
            volumeDownAt = 0L; volumeUpAt = 0L
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        if (::speech.isInitialized) speech.destroy()
        if (::tts.isInitialized) tts.shutdown()
        super.onDestroy()
    }
}
