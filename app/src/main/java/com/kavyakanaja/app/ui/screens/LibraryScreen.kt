package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.kavyakanaja.app.ui.components.NetworkStatusBar
import com.kavyakanaja.app.ui.components.PoemListItem
import com.kavyakanaja.app.viewmodel.PoemViewModel
import com.kavyakanaja.app.viewmodel.PoetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController, padding: PaddingValues, poemVm: PoemViewModel = viewModel(), poetVm: PoetViewModel = viewModel()) {
  val poems by poemVm.filteredPoems.collectAsState()
  val poets by poetVm.allPoets.collectAsState()
  val isOnline by poemVm.isOnline.collectAsState()
  val selectedTag by poemVm.selectedTag.collectAsState()
  val query by poemVm.searchQuery.collectAsState()
  Scaffold(topBar = { TopAppBar(title = { Text("ಕಾವ್ಯ ಸಂಗ್ರಹ / Poetry Library") }) }) { inner ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(inner).padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item { NetworkStatusBar(isOnline, poems.isNotEmpty(), onRetry = { poemVm.refresh() }) }
      item {
        OutlinedTextField(
          value = query,
          onValueChange = poemVm::search,
          modifier = Modifier.fillParentMaxWidth(),
          placeholder = { Text("ಶೋಧಿಸಿ / Search poems or poets...") },
          singleLine = true
        )
      }
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(tagPairs) { (tag, kn) ->
            FilterChip(selected = selectedTag == tag, onClick = { poemVm.filterByTag(tag) }, label = { Text("$kn / ${tag.replaceFirstChar { it.uppercase() }}") })
          }
        }
      }
      if (poems.isEmpty()) item { EmptyStateScreen(onRetry = { poemVm.refresh() }) }
      items(poems) { poem ->
        PoemListItem(poem, poets.find { it.id == poem.poetId }) { navController.navigate("poem/${poem.id}") }
      }
    }
  }
}
