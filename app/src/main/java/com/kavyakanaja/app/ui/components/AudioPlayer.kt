package com.kavyakanaja.app.ui.components

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.ui.theme.DeepSaffron
import com.kavyakanaja.app.ui.theme.GoldenAccent
import java.util.Locale
import kotlinx.coroutines.delay

private const val TTS_UTTERANCE_ID = "kavyakanaja_poem"

/**
 * Plays a poem's audio:
 *  1. Prefer a bundled MP3 in `assets/audio/poem_XXX.mp3` (always available
 *     offline, generated at build/setup time via `tools/gen_audio.py`).
 *  2. Otherwise, fall back to [Poem.audioUrl] streaming via ExoPlayer.
 *  3. On stream error or if no URL is available, fall back to the device
 *     TextToSpeech engine reading the verse.
 *  4. If TTS also isn't usable, show a friendly notice.
 */
@Composable
fun PoemAudioPlayer(poem: Poem) {
  val context = LocalContext.current
  val assetName = "audio/poem_%03d.mp3".format(poem.id)
  val hasAsset = remember(poem.id) {
    runCatching { context.assets.open(assetName).close() }.isSuccess
  }
  val resolvedUrl = when {
    hasAsset -> "asset:///$assetName"
    poem.audioUrl.isNotBlank() -> poem.audioUrl.trim()
    else -> ""
  }
  var useTts by remember(resolvedUrl) { mutableStateOf(resolvedUrl.isBlank()) }

  if (useTts) {
    TtsPoemPlayer(poem)
  } else {
    StreamingAudioPlayer(
      url = resolvedUrl,
      isLocalAsset = hasAsset,
      onUnrecoverableError = { useTts = true }
    )
  }
}

// ---------- Streaming (ExoPlayer) ----------

@Composable
private fun StreamingAudioPlayer(
  url: String,
  isLocalAsset: Boolean,
  onUnrecoverableError: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var isPlaying by remember { mutableStateOf(false) }
  var isBuffering by remember { mutableStateOf(true) }
  var progress by remember { mutableFloatStateOf(0f) }
  var position by remember { mutableLongStateOf(0L) }
  var duration by remember { mutableLongStateOf(0L) }
  var isUserSeeking by remember { mutableStateOf(false) }
  var seekTarget by remember { mutableFloatStateOf(0f) }

  val exoPlayer = remember(url) {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(MediaItem.fromUri(url))
      prepare()
    }
  }

  DisposableEffect(exoPlayer) {
    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(playing: Boolean) {
        isPlaying = playing
      }

      override fun onPlaybackStateChanged(state: Int) {
        isBuffering = state == Player.STATE_BUFFERING
        if (state == Player.STATE_ENDED) {
          exoPlayer.seekTo(0)
          exoPlayer.pause()
        }
      }

      override fun onPlayerError(error: PlaybackException) {
        isBuffering = false
        onUnrecoverableError()
      }
    }
    exoPlayer.addListener(listener)
    onDispose {
      exoPlayer.removeListener(listener)
      exoPlayer.release()
    }
  }

  DisposableEffect(lifecycleOwner, exoPlayer) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_PAUSE && exoPlayer.isPlaying) exoPlayer.pause()
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  LaunchedEffect(exoPlayer, isPlaying) {
    while (isPlaying) {
      if (!isUserSeeking) {
        position = exoPlayer.currentPosition.coerceAtLeast(0L)
        duration = exoPlayer.duration.coerceAtLeast(0L)
        progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
      }
      delay(250)
    }
    position = exoPlayer.currentPosition.coerceAtLeast(0L)
    duration = exoPlayer.duration.coerceAtLeast(0L)
    if (duration > 0 && !isUserSeeking) progress = position.toFloat() / duration.toFloat()
  }

  PlayerSurface {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = { exoPlayer.seekTo((exoPlayer.currentPosition - 10_000).coerceAtLeast(0L)) },
        modifier = Modifier.size(40.dp)
      ) { Icon(Icons.Default.Replay10, contentDescription = "10s ಹಿಂದೆ / Back") }
      Spacer(Modifier.padding(4.dp))
      RoundActionButton(
        isLoading = isBuffering,
        isPlaying = isPlaying,
        onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() }
      )
      Spacer(Modifier.padding(4.dp))
      IconButton(
        onClick = {
          val max = exoPlayer.duration.coerceAtLeast(0L)
          exoPlayer.seekTo((exoPlayer.currentPosition + 10_000).coerceAtMost(max))
        },
        modifier = Modifier.size(40.dp)
      ) { Icon(Icons.Default.Forward10, contentDescription = "10s ಮುಂದೆ / Forward") }
    }
    Spacer(Modifier.height(8.dp))
    Slider(
      value = (if (isUserSeeking) seekTarget else progress).coerceIn(0f, 1f),
      onValueChange = {
        isUserSeeking = true
        seekTarget = it
      },
      onValueChangeFinished = {
        if (duration > 0) exoPlayer.seekTo((seekTarget * duration).toLong())
        isUserSeeking = false
      },
      colors = SliderDefaults.colors(thumbColor = GoldenAccent, activeTrackColor = GoldenAccent),
      modifier = Modifier.fillMaxWidth()
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      val shownPos =
        if (isUserSeeking && duration > 0) (seekTarget * duration).toLong() else position
      Text(formatMs(shownPos), style = MaterialTheme.typography.labelSmall)
      Text(formatMs(duration), style = MaterialTheme.typography.labelSmall)
    }
    Spacer(Modifier.height(8.dp))
    Text(
      if (isLocalAsset)
        "ವಾಚನ: ಸ್ಥಳೀಯ ಧ್ವನಿ (ಪೂರ್ವ ಸಿದ್ಧಪಡಿಸಿದ) / Recited by: Bundled Kannada narration"
      else
        "ವಾಚನ: ಆನ್‌ಲೈನ್ ಧ್ವನಿ / Recited by: Streaming audio",
      style = MaterialTheme.typography.labelSmall
    )
  }
}

// ---------- On-device Text-To-Speech fallback ----------

@Composable
private fun TtsPoemPlayer(poem: Poem) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var isReady by remember { mutableStateOf(false) }
  var isEngineAvailable by remember { mutableStateOf(true) }
  var isSpeaking by remember { mutableStateOf(false) }
  var pendingChunks by remember { mutableStateOf(0) }
  var useTransliteration by remember { mutableStateOf(false) }
  var knSupported by remember { mutableStateOf(false) }
  var ttsRef by remember { mutableStateOf<TextToSpeech?>(null) }
  // Token bumps whenever we (re)build a TTS engine so DisposableEffect re-runs.
  var engineToken by remember { mutableStateOf(0) }
  // Bumps on every speak() call so the watchdog LaunchedEffect re-arms.
  var speakTick by remember { mutableStateOf(0) }

  fun fallbackToTransliteration() {
    isSpeaking = false
    pendingChunks = 0
    when {
      knSupported && !useTransliteration -> {
        // First failure with Kannada voice - retry once with the Latin
        // transliteration (en-US is much more stable on emulators).
        useTransliteration = true
        engineToken += 1
      }
      else -> {
        // Already on the fallback path and still failing - the on-device
        // engine is unusable; surface a friendly notice.
        isEngineAvailable = false
      }
    }
  }

  // Watchdog: Google TTS on some emulators crashes natively mid-synthesis
  // without ever invoking onError. If a speak() request never reaches onStart
  // within a few seconds, assume failure and switch to the transliteration.
  LaunchedEffect(speakTick) {
    if (speakTick == 0) return@LaunchedEffect
    delay(3500)
    if (pendingChunks > 0 && !isSpeaking) fallbackToTransliteration()
  }

  DisposableEffect(engineToken) {
    var tts: TextToSpeech? = null
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        val t = tts ?: return@TextToSpeech
        val kn = Locale("kn", "IN")
        val result = t.setLanguage(kn)
        knSupported =
          result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        if (!knSupported || useTransliteration) {
          t.language = Locale.US
        }
        t.setSpeechRate(0.85f)
        t.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) { isSpeaking = true }
          override fun onDone(utteranceId: String?) {
            pendingChunks = (pendingChunks - 1).coerceAtLeast(0)
            if (pendingChunks == 0) isSpeaking = false
          }
          @Deprecated("Required override; superseded by onError(id, code)")
          override fun onError(utteranceId: String?) { fallbackToTransliteration() }
          override fun onError(utteranceId: String?, errorCode: Int) {
            fallbackToTransliteration()
          }
        })
        isReady = true
      } else {
        isEngineAvailable = false
      }
    }
    ttsRef = tts
    onDispose {
      tts?.stop()
      tts?.shutdown()
      ttsRef = null
      isReady = false
    }
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_PAUSE) {
        ttsRef?.stop()
        isSpeaking = false
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  if (!isEngineAvailable) {
    PlayerSurface {
      Text(
        "🎙 ಧ್ವನಿ ಸದ್ಯಕ್ಕೆ ಲಭ್ಯವಿಲ್ಲ / Audio not available on this device",
        style = MaterialTheme.typography.bodyMedium
      )
      Spacer(Modifier.height(4.dp))
      Text(
        "Install a Text-to-Speech engine to enable poem narration.",
        style = MaterialTheme.typography.labelSmall
      )
    }
    return
  }

  PlayerSurface {
    Row(verticalAlignment = Alignment.CenterVertically) {
      RoundActionButton(
        isLoading = !isReady,
        isPlaying = isSpeaking,
        onClick = {
          val tts = ttsRef ?: return@RoundActionButton
          if (isSpeaking) {
            tts.stop()
            isSpeaking = false
            pendingChunks = 0
          } else {
            val source = when {
              useTransliteration -> poem.transliteration.ifBlank { poem.verse }
              else -> poem.verse.ifBlank { poem.transliteration }
            }
            val chunks = source
              .split('\n', '।', '|')
              .map { it.trim() }
              .filter { it.isNotEmpty() }
            if (chunks.isEmpty()) return@RoundActionButton
            pendingChunks = chunks.size
            chunks.forEachIndexed { index, chunk ->
              val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
              tts.speak(chunk, mode, null, "${TTS_UTTERANCE_ID}_$index")
            }
            speakTick += 1
          }
        }
      )
      Spacer(Modifier.padding(8.dp))
      Column(Modifier.weight(1f)) {
        Text(
          when {
            isSpeaking -> "ಓದುತ್ತಿದೆ… / Speaking…"
            isReady -> "ಆಡಲು ಒತ್ತಿ / Tap to listen"
            else -> "ಧ್ವನಿ ಸಿದ್ಧವಾಗುತ್ತಿದೆ… / Preparing voice…"
          },
          style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          color = GoldenAccent
        )
      }
      if (isSpeaking) {
        IconButton(onClick = {
          ttsRef?.stop(); isSpeaking = false; pendingChunks = 0
        }) {
          Icon(Icons.Default.Stop, contentDescription = "ನಿಲ್ಲಿಸಿ / Stop")
        }
      }
    }
    Spacer(Modifier.height(8.dp))
    Text(
      if (useTransliteration)
        "ವಾಚನ: ಸ್ಥಳೀಯ ಧ್ವನಿ (ಲಿಪ್ಯಂತರ) / Recited by: On-device voice (transliteration)"
      else
        "ವಾಚನ: ಸ್ಥಳೀಯ ಧ್ವನಿ / Recited by: On-device voice",
      style = MaterialTheme.typography.labelSmall
    )
  }
}

// ---------- Shared UI helpers ----------

@Composable
private fun PlayerSurface(content: @Composable () -> Unit) {
  Column(
    Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .padding(14.dp)
  ) { content() }
}

@Composable
private fun RoundActionButton(isLoading: Boolean, isPlaying: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier.size(56.dp).clip(CircleShape).background(DeepSaffron),
    contentAlignment = Alignment.Center
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        color = Color.White,
        strokeWidth = 3.dp
      )
    } else {
      IconButton(onClick = onClick) {
        Icon(
          if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "ತಡೆ / Pause" else "ಆಡು / Play",
          tint = Color.White
        )
      }
    }
  }
}

private fun formatMs(ms: Long): String {
  val total = (ms / 1000).coerceAtLeast(0)
  val min = total / 60
  val sec = total % 60
  return "%d:%02d".format(min, sec)
}
