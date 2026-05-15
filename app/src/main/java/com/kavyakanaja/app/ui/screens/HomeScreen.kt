package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kavyakanaja.app.ui.components.EmptyStateScreen
import com.kavyakanaja.app.ui.components.LoadingScreen
import com.kavyakanaja.app.ui.components.NetworkStatusBar
import com.kavyakanaja.app.ui.components.PoemFeatureCard
import com.kavyakanaja.app.ui.components.PoemListItem
import com.kavyakanaja.app.ui.components.PoetCard
import com.kavyakanaja.app.ui.theme.CachedAmber
import com.kavyakanaja.app.ui.theme.CardSurface
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.OnlineGreen
import com.kavyakanaja.app.ui.theme.OfflineRed
import com.kavyakanaja.app.ui.theme.TextSecondary
import com.kavyakanaja.app.utils.DateUtils
import com.kavyakanaja.app.viewmodel.PoemViewModel
import com.kavyakanaja.app.viewmodel.PoetViewModel

val tagPairs = listOf("All" to "ಎಲ್ಲವೂ", "vachana" to "ವಚನ", "keertane" to "ಕೀರ್ತನೆ", "nadageethe" to "ನಾಡಗೀತೆ", "modern" to "ಆಧುನಿಕ", "devotional" to "ಭಕ್ತಿ", "nature" to "ಪ್ರಕೃತಿ")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, padding: PaddingValues, poemVm: PoemViewModel = viewModel(), poetVm: PoetViewModel = viewModel()) {
  val loading by poemVm.isLoading.collectAsState()
  val isOnline by poemVm.isOnline.collectAsState()
  val poems by poemVm.filteredPoems.collectAsState()
  val allPoems by poemVm.allPoems.collectAsState()
  val pod by poemVm.poemOfTheDay.collectAsState()
  val poets by poetVm.allPoets.collectAsState()
  val jnanpith by poetVm.jnanpithPoets.collectAsState()
  val selectedTag by poemVm.selectedTag.collectAsState()
  val lastRead by poemVm.lastReadPoemId.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("ಕಾವ್ಯ-ಕಣಜ", style = MaterialTheme.typography.bodyMedium)
            Text("Kavya-Kanaja", style = MaterialTheme.typography.labelSmall)
          }
        },
        actions = {
          val color = if (isOnline) OnlineGreen else if (allPoems.isNotEmpty()) CachedAmber else OfflineRed
          Spacer(Modifier.size(12.dp).clip(CircleShape).background(color))
          Spacer(Modifier.size(16.dp))
        }
      )
    }
  ) { inner ->
    if (loading) LoadingScreen() else LazyColumn(
      modifier = Modifier.fillMaxSize().padding(inner).padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item { NetworkStatusBar(isOnline = isOnline, hasCache = allPoems.isNotEmpty(), onRetry = { poemVm.refresh(); poetVm.refresh() }) }
      item { Text(DateUtils.getTodayFormatted(), style = MaterialTheme.typography.labelSmall, color = TextSecondary) }
      item { Text("ಇಂದಿನ ಕವಿತೆ / Poem of the Day", style = MaterialTheme.typography.headlineMedium) }
      item {
        pod?.let { poem ->
          PoemFeatureCard(poem, poets.find { it.id == poem.poetId }, onOpen = { navController.navigate("poem/${poem.id}") }, onFavorite = { poemVm.toggleFavorite(poem) })
        } ?: EmptyStateScreen(onRetry = { poemVm.refresh() })
      }
      lastRead?.let { id ->
        allPoems.find { it.id == id }?.let { poem ->
          item {
            Card(colors = CardDefaults.cardColors(containerColor = CardSurface), modifier = Modifier.fillMaxWidth().clickable { navController.navigate("poem/$id") }) {
              Column(Modifier.padding(14.dp)) {
                Text("ಓದುವುದನ್ನು ಮುಂದುವರೆಸಿ / Continue Reading", color = GoldenAccent)
                Text("${poem.titleKannada} / ${poem.title}", style = MaterialTheme.typography.bodyMedium)
              }
            }
          }
        }
      }
      item { Text("ಜ್ಞಾನಪೀಠ ವಿಜೇತರು / Jnanpith Awardees", style = MaterialTheme.typography.headlineMedium) }
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          items(jnanpith) { poet -> PoetCard(poet, Modifier.size(width = 150.dp, height = 210.dp)) { navController.navigate("poet/${poet.id}") } }
        }
      }
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(tagPairs) { (tag, kn) ->
            FilterChip(selected = selectedTag == tag, onClick = { poemVm.filterByTag(tag) }, label = { Text("$kn / ${tag.replaceFirstChar { it.uppercase() }}") })
          }
        }
      }
      items(poems) { poem ->
        PoemListItem(poem, poets.find { it.id == poem.poetId }) { navController.navigate("poem/${poem.id}") }
      }
    }
  }
}
