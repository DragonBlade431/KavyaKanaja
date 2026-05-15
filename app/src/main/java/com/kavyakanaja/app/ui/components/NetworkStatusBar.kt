package com.kavyakanaja.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kavyakanaja.app.ui.theme.CachedAmber
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.OfflineRed
import kotlinx.coroutines.delay

@Composable
fun NetworkStatusBar(isOnline: Boolean, hasCache: Boolean = true, onRetry: () -> Unit = {}) {
  var showOnline by remember(isOnline) { mutableStateOf(isOnline) }
  LaunchedEffect(isOnline) {
    if (isOnline) {
      showOnline = true
      delay(3000)
      showOnline = false
    }
  }
  val visible = if (isOnline) showOnline else true
  val text = when {
    isOnline -> "☁ ಸಂಪರ್ಕಿತ / Connected"
    hasCache -> "📚 ಸಂಗ್ರಹದಿಂದ / From cache"
    else -> "📵 ಆಫ್‌ಲೈನ್ / Offline"
  }
  val color = when {
    isOnline -> GoldenAccent
    hasCache -> CachedAmber
    else -> OfflineRed
  }
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    Row(
      modifier = Modifier.fillMaxWidth().background(color).padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = text, style = MaterialTheme.typography.labelSmall, color = Color.Black)
      if (!isOnline && !hasCache) {
        Button(onClick = onRetry) { Text("ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ / Retry") }
      }
    }
  }
}
