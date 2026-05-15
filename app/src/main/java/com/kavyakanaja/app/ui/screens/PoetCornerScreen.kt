package com.kavyakanaja.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.kavyakanaja.app.ui.components.NetworkStatusBar
import com.kavyakanaja.app.ui.components.PoetCard
import com.kavyakanaja.app.viewmodel.PoetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoetCornerScreen(navController: NavController, padding: PaddingValues, poetVm: PoetViewModel = viewModel()) {
  val poets by poetVm.allPoets.collectAsState()
  val winners by poetVm.jnanpithPoets.collectAsState()
  val isOnline by poetVm.isOnline.collectAsState()
  Scaffold(topBar = { TopAppBar(title = { Text("ಕವಿಗಳ ಮೂಲೆ / Poet's Corner") }) }) { inner ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(inner).padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item { NetworkStatusBar(isOnline, poets.isNotEmpty(), onRetry = { poetVm.refresh() }) }
      item { Text("🏆 ಜ್ಞಾನಪೀಠ ಪ್ರಶಸ್ತಿ ವಿಜೇತರು / Jnanpith Award Winners", style = MaterialTheme.typography.headlineMedium) }
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          items(winners) { poet -> PoetCard(poet, Modifier.size(width = 150.dp, height = 210.dp)) { navController.navigate("poet/${poet.id}") } }
        }
      }
      item { Text("ಎಲ್ಲ ಕವಿಗಳು / All Poets", style = MaterialTheme.typography.headlineMedium) }
      items(poets.chunked(2)) { rowPoets ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          rowPoets.forEach { poet ->
            PoetCard(poet, Modifier.weight(1f).height(220.dp)) { navController.navigate("poet/${poet.id}") }
          }
          if (rowPoets.size == 1) androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        }
      }
    }
  }
}
