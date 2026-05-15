package com.kavyakanaja.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyStateScreen(message: String = "ಯಾವುದೇ ಕವಿತೆಗಳಿಲ್ಲ / No poems found", onRetry: (() -> Unit)? = null) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(12.dp))
    Text(message, style = MaterialTheme.typography.bodyLarge)
    if (onRetry != null) {
      Spacer(Modifier.height(12.dp))
      Button(onClick = onRetry) { Text("ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ / Retry") }
    }
  }
}
