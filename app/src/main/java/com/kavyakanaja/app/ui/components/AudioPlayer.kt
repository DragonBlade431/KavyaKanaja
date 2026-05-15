package com.kavyakanaja.app.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.ui.theme.DeepSaffron
import com.kavyakanaja.app.ui.theme.GoldenAccent
import kotlinx.coroutines.delay

@Composable
fun PoemAudioPlayer(poem: Poem) {
  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(false) }
  var hasError by remember { mutableStateOf(false) }
  var progress by remember { mutableFloatStateOf(0f) }
  var position by remember { mutableLongStateOf(0L) }
  var duration by remember { mutableLongStateOf(0L) }
  val exoPlayer = remember(poem.audioUrl) {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(MediaItem.fromUri(poem.audioUrl))
      prepare()
    }
  }

  DisposableEffect(poem.audioUrl) {
    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(isPlayingNow: Boolean) {
        isPlaying = isPlayingNow
      }

      override fun onPlayerError(error: PlaybackException) {
        hasError = true
      }
    }
    exoPlayer.addListener(listener)
    onDispose {
      exoPlayer.removeListener(listener)
      exoPlayer.release()
    }
  }

  LaunchedEffect(exoPlayer, isPlaying) {
    while (true) {
      position = exoPlayer.currentPosition.coerceAtLeast(0L)
      duration = exoPlayer.duration.coerceAtLeast(0L)
      progress = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
      delay(500)
    }
  }

  Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp)) {
    if (hasError) {
      AssistChip(onClick = {}, label = { Text("🎙 ಶೀಘ್ರದಲ್ಲೇ ಬರಲಿದೆ / Audio coming soon") })
    } else {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { if (isPlaying) exoPlayer.pause() else exoPlayer.play() },
          modifier = Modifier.size(56.dp).clip(CircleShape).background(DeepSaffron)
        ) {
          Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "ಆಡು / Play", tint = Color.White)
        }
        Spacer(Modifier.padding(8.dp))
        Column(Modifier.weight(1f)) {
          LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = GoldenAccent)
          Spacer(Modifier.height(6.dp))
          Text("${formatMs(position)} / ${formatMs(duration)}", style = MaterialTheme.typography.labelSmall)
        }
      }
    }
    Spacer(Modifier.height(8.dp))
    Text("ವಾಚನ: ಅನುಕರಣ ಧ್ವನಿ / Recited by: Simulated Voice", style = MaterialTheme.typography.labelSmall)
  }
}

private fun formatMs(ms: Long): String {
  val total = (ms / 1000).coerceAtLeast(0)
  val min = total / 60
  val sec = total % 60
  return "%d:%02d".format(min, sec)
}
