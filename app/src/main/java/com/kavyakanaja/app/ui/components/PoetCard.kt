package com.kavyakanaja.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kavyakanaja.app.data.model.Poet
import com.kavyakanaja.app.ui.theme.CardSurface
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.TextSecondary

@Composable
fun PoetCard(poet: Poet, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(1.dp, GoldenAccent.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      AsyncImage(
        model = poet.photoUrl,
        contentDescription = poet.name,
        modifier = Modifier.size(66.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(poet.photoPlaceholderColor))),
        contentScale = ContentScale.Crop
      )
      Spacer(Modifier.height(8.dp))
      Text(poet.nameKannada, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, maxLines = 1)
      Text(poet.name, style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Text(poet.period.substringBefore("(").trim(), style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center, maxLines = 2)
      poet.jnanpithYear?.let {
        Spacer(Modifier.height(6.dp))
        Surface(color = GoldenAccent, shape = RoundedCornerShape(50), contentColor = Color.Black) {
          Text("ಜ್ಞಾನಪೀಠ $it / Jnanpith $it", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}
