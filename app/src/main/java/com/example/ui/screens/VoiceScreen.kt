package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSecondarySurface
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SubtitleColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VioletHighlight
import com.example.ui.theme.VioletPrimary
import com.example.ui.theme.WarmAccent
import com.example.viewmodel.BrainHelperViewModel
import com.example.viewmodel.VoiceState
import java.util.Locale

@Composable
fun VoiceScreen(
    viewModel: BrainHelperViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val transcript by viewModel.voiceTranscript.collectAsStateWithLifecycle()
    val response by viewModel.voiceResponse.collectAsStateWithLifecycle()
    val isBengali by viewModel.voiceLangIsBengali.collectAsStateWithLifecycle()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    // Text To Speech
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = if (isBengali) Locale("bn", "BD") else Locale.US
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Speak when response changes in SPEAKING state
    LaunchedEffect(response, voiceState) {
        if (voiceState == VoiceState.SPEAKING && response.isNotBlank()) {
            tts?.language = if (isBengali) Locale("bn", "BD") else Locale.US
            tts?.speak(response, TextToSpeech.QUEUE_FLUSH, null, "tts_reply")
        }
    }

    // Speech Recognizer
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (speechRecognizer == null) {
            viewModel.processVoiceInput(
                if (isBengali) "আজকের দিনটি কীভাবে সাজাব?" else "Plan my day please"
            )
            return
        }

        viewModel.setVoiceState(VoiceState.LISTENING)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isBengali) "bn-BD" else "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                viewModel.setVoiceState(VoiceState.IDLE)
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognized = matches?.firstOrNull() ?: ""
                viewModel.processVoiceInput(recognized)
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognized = matches?.firstOrNull() ?: ""
                viewModel.setVoiceTranscript(recognized)
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        viewModel.setVoiceState(VoiceState.IDLE)
        tts?.stop()
    }

    // Pulse Animation for Voice Orb
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) 1.22f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Text(
                text = stringResource(id = R.string.voice_title),
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Language Toggle (English <-> বাংলা)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSecondarySurface)
                    .clickable(
                        onClick = { viewModel.toggleVoiceLanguage() }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("voice_language_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBengali) "বাংলা" else "EN",
                    color = VioletHighlight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Central Animated AI Orb Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            val orbColors = when (voiceState) {
                VoiceState.LISTENING -> listOf(ElectricBlue, VioletPrimary, MintSuccess)
                VoiceState.PROCESSING -> listOf(WarmAccent, VioletHighlight, ElectricBlue)
                VoiceState.SPEAKING -> listOf(MintSuccess, ElectricBlue, VioletPrimary)
                VoiceState.IDLE -> listOf(VioletPrimary, Color(0xFF4A3FC9), ObsidianSecondarySurface)
            }

            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(orbScale)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(orbColors))
                    .clickable(
                        onClick = {
                            if (voiceState == VoiceState.LISTENING) stopListening() else startListening()
                        }
                    )
                    .testTag("voice_orb"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (voiceState) {
                        VoiceState.LISTENING -> Icons.Default.Mic
                        VoiceState.SPEAKING -> Icons.Default.VolumeUp
                        VoiceState.PROCESSING -> Icons.Default.Refresh
                        VoiceState.IDLE -> Icons.Default.Mic
                    },
                    contentDescription = "Voice Action",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val statusText = when (voiceState) {
                VoiceState.LISTENING -> stringResource(id = R.string.voice_listening)
                VoiceState.PROCESSING -> stringResource(id = R.string.voice_processing)
                VoiceState.SPEAKING -> stringResource(id = R.string.voice_speaking)
                VoiceState.IDLE -> stringResource(id = R.string.voice_tap_to_speak)
            }

            Text(
                text = statusText,
                color = VioletHighlight,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Live Transcription & Response Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (transcript.isNotBlank()) "\"$transcript\"" else "Listening for speech…",
                    color = if (transcript.isNotBlank()) TextPrimary else TextMuted,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp
                )

                if (response.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = response,
                        color = ElectricBlue,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (voiceState == VoiceState.LISTENING) stopListening() else startListening() },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("voice_start_stop_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (voiceState == VoiceState.LISTENING) Color(0xFFD32F2F) else VioletPrimary
                )
            ) {
                Icon(
                    imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (voiceState == VoiceState.LISTENING) "Stop" else "Speak Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
