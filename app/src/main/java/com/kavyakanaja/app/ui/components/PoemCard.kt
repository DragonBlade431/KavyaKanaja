package com.kavyakanaja.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.Poet
import com.kavyakanaja.app.ui.theme.CardSurface
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.TextSecondary

@Composable
fun PoemFeatureCard(poem: Poem, poet: Poet?, onOpen: () -> Unit, onFavorite: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, GoldenAccent)
  ) {
    Column(Modifier.padding(18.dp)) {
      Text("❧", color = GoldenAccent, style = MaterialTheme.typography.headlineMedium)
      Text(poem.titleKannada, style = MaterialTheme.typography.headlineMedium, color = GoldenAccent)
      Text(poem.title, style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic), color = TextSecondary)
      Spacer(Modifier.height(12.dp))
      Text(poem.verse.lines().take(2).joinToString("\n"), style = MaterialTheme.typography.bodyMedium, maxLines = 2)
      Spacer(Modifier.height(10.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = {}, label = { Text(poet?.nameKannada ?: "ಕವಿ") })
        AssistChip(onClick = {}, label = { Text(poem.era) })
      }
      Spacer(Modifier.height(12.dp))
      Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onOpen) { Text("ಓದಿ ಕೇಳಿ / Read & Listen") }
        IconButton(onClick = onFavorite) {
          Icon(if (poem.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "ಇಷ್ಟ / Favourite")
        }
      }
    }
  }
}

@Composable
fun PoemListItem(poem: Poem, poet: Poet?, onClick: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(Modifier.padding(14.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
          Text(poem.titleKannada, style = MaterialTheme.typography.bodyMedium, color = GoldenAccent)
          Text(poem.title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
      }
      Text("${poet?.nameKannada ?: "ಕವಿ"} · ${poem.era}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
      Spacer(Modifier.height(6.dp))
      Text(poem.verse.lineSequence().firstOrNull().orEmpty(), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Spacer(Modifier.height(8.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        poem.tags.take(3).forEach { tag ->
          AssistChip(onClick = {}, label = { Text(tag) })
        }
      }
    }
  }
}
