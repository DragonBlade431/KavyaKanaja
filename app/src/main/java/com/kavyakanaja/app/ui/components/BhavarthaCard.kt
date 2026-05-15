package com.kavyakanaja.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.SurfaceDark

@Composable
fun BhavarthaCard(bhavartha: String, bhavarthaKannada: String) {
  var expanded by remember { mutableStateOf(true) }
  var kannada by remember { mutableStateOf(false) }
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = BorderStroke(1.dp, GoldenAccent),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("ಭಾವಾರ್ಥ ✦ Meaning & Essence", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = GoldenAccent)
        IconButton(onClick = { expanded = !expanded }) {
          Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "ತೆರೆ / Expand")
        }
      }
      AnimatedVisibility(expanded) {
        Column {
          Row {
            FilterChip(selected = !kannada, onClick = { kannada = false }, label = { Text("English / ಇಂಗ್ಲಿಷ್") })
            Spacer(Modifier.padding(4.dp))
            FilterChip(selected = kannada, onClick = { kannada = true }, label = { Text("ಕನ್ನಡ / Kannada") })
          }
          Spacer(Modifier.height(10.dp))
          Text("🪔 ${if (kannada) bhavarthaKannada else bhavartha}", style = MaterialTheme.typography.bodyLarge)
        }
      }
    }
  }
}
