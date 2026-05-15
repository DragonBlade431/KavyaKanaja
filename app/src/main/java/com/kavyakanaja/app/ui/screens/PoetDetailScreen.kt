package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kavyakanaja.app.ui.components.EmptyStateScreen
import com.kavyakanaja.app.ui.components.PoemListItem
import com.kavyakanaja.app.ui.theme.CardSurface
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.TextSecondary
import com.kavyakanaja.app.viewmodel.PoemViewModel
import com.kavyakanaja.app.viewmodel.PoetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoetDetailScreen(navController: NavController, poetId: Int, poemVm: PoemViewModel = viewModel(), poetVm: PoetViewModel = viewModel()) {
  val poets by poetVm.allPoets.collectAsState()
  val allPoems by poemVm.allPoems.collectAsState()
  val poet = poets.find { it.id == poetId }
  val poems = allPoems.filter { it.poetId == poetId }
  var kannadaBio by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(poet?.nameKannada ?: "ಕವಿ / Poet") },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "ಹಿಂದೆ / Back") } }
      )
    }
  ) { inner ->
    if (poet == null) {
      EmptyStateScreen("ಕವಿ ಸಿಗಲಿಲ್ಲ / Poet not found")
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(inner),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          Card(colors = CardDefaults.cardColors(containerColor = CardSurface), border = BorderStroke(1.dp, GoldenAccent), shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              AsyncImage(
                model = poet.photoUrl,
                contentDescription = poet.name,
                modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(poet.photoPlaceholderColor))),
                contentScale = ContentScale.Crop
              )
              Spacer(Modifier.height(12.dp))
              Text(poet.nameKannada, style = MaterialTheme.typography.displayLarge)
              Text(poet.name, style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = TextSecondary)
              Text(poet.period, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              poet.jnanpithYear?.let {
                Spacer(Modifier.height(8.dp))
                Surface(color = GoldenAccent, shape = RoundedCornerShape(50), contentColor = Color.Black) {
                  Text("🏆 ಜ್ಞಾನಪೀಠ $it / Jnanpith $it", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
              }
            }
          }
        }
        item {
          Text("ಜೀವನ ಪರಿಚಯ / Biography", style = MaterialTheme.typography.headlineMedium)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !kannadaBio, onClick = { kannadaBio = false }, label = { Text("English / ಇಂಗ್ಲಿಷ್") })
            FilterChip(selected = kannadaBio, onClick = { kannadaBio = true }, label = { Text("ಕನ್ನಡ / Kannada") })
          }
          Text(if (kannadaBio) poet.bioKannada else poet.bio, style = MaterialTheme.typography.bodyLarge)
        }
        item {
          Text("ಪ್ರಸಿದ್ಧ ಕೃತಿಗಳು / Famous Works", style = MaterialTheme.typography.headlineMedium)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(poet.famousWorks) { work -> AssistChip(onClick = {}, label = { Text(work) }) }
          }
        }
        item { Text("ಈ ಸಂಗ್ರಹದ ಕವಿತೆಗಳು / Poems in Collection", style = MaterialTheme.typography.headlineMedium) }
        if (poems.isEmpty()) item { EmptyStateScreen("ಕವಿತೆಗಳಿಲ್ಲ / No poems found") }
        items(poems) { poem -> PoemListItem(poem, poet) { navController.navigate("poem/${poem.id}") } }
      }
    }
  }
}
