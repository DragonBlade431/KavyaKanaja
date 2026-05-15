package com.kavyakanaja.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kavyakanaja.app.data.model.WordMeaning
import com.kavyakanaja.app.ui.theme.GoldenAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordMeaningSheet(word: WordMeaning?, onDismiss: () -> Unit) {
  if (word != null) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
      Surface(modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, GoldenAccent)) {
        Column(Modifier.padding(22.dp)) {
          Text(word.word, style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp), color = GoldenAccent)
          Spacer(Modifier.height(10.dp))
          Divider()
          Spacer(Modifier.height(12.dp))
          Text("📖 English: ${word.meaning}", style = MaterialTheme.typography.bodyLarge)
          Text("📝 ಕನ್ನಡ: ${word.kannadaMeaning}", style = MaterialTheme.typography.bodyMedium)
          Spacer(Modifier.height(10.dp))
          Text("💬 ಉದಾಹರಣೆ / Example: ${word.usageExample}", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic))
        }
      }
    }
  }
}
