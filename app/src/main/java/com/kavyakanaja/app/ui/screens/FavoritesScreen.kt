package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kavyakanaja.app.ui.components.EmptyStateScreen
import com.kavyakanaja.app.ui.components.PoemListItem
import com.kavyakanaja.app.viewmodel.PoemViewModel
import com.kavyakanaja.app.viewmodel.PoetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, padding: PaddingValues, poemVm: PoemViewModel = viewModel(), poetVm: PoetViewModel = viewModel()) {
  val poems by poemVm.favorites.collectAsState()
  val poets by poetVm.allPoets.collectAsState()
  Scaffold(topBar = { TopAppBar(title = { Text("ನೆಚ್ಚಿನ ಕವಿತೆಗಳು / Favourite Poems") }) }) { inner ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(inner).padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (poems.isEmpty()) item { EmptyStateScreen("ಇನ್ನೂ ಇಷ್ಟಪಡಿಲ್ಲ / No favourites yet") }
      items(poems) { poem -> PoemListItem(poem, poets.find { it.id == poem.poetId }) { navController.navigate("poem/${poem.id}") } }
    }
  }
}
