package com.kavyakanaja.app.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.WordMeaning
import com.kavyakanaja.app.ui.components.BhavarthaCard
import com.kavyakanaja.app.ui.components.EmptyStateScreen
import com.kavyakanaja.app.ui.components.PoemAudioPlayer
import com.kavyakanaja.app.ui.components.WordMeaningSheet
import com.kavyakanaja.app.ui.theme.CardSurface
import com.kavyakanaja.app.ui.theme.GoldenAccent
import com.kavyakanaja.app.ui.theme.TextSecondary
import com.kavyakanaja.app.viewmodel.PoemViewModel
import com.kavyakanaja.app.viewmodel.PoetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(
  navController: NavController,
  poemId: Int,
  poemVm: PoemViewModel = viewModel(),
  poetVm: PoetViewModel = viewModel()
) {
  val allPoems by poemVm.allPoems.collectAsState()
  val poets by poetVm.allPoets.collectAsState()
  val poem = allPoems.find { it.id == poemId }
  val poet = poets.find { it.id == poem?.poetId }
  val context = LocalContext.current
  var showTranslit by remember { mutableStateOf(false) }
  var selectedWord by remember { mutableStateOf<WordMeaning?>(null) }
  var translationOpen by remember { mutableStateOf(true) }

  LaunchedEffect(poemId) {
    if (poemId > 0) poemVm.rememberLastRead(poemId)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(poem?.titleKannada ?: "ಕವಿತೆ / Poem") },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "ಹಿಂದೆ / Back")
          }
        },
        actions = {
          poem?.let {
            IconButton(onClick = { poemVm.toggleFavorite(it) }) {
              Icon(
                if (it.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "ಇಷ್ಟ / Favourite"
              )
            }
            IconButton(onClick = { sharePoem(context, it, poet?.nameKannada.orEmpty(), poet?.name.orEmpty()) }) {
              Icon(Icons.Default.Share, contentDescription = "ಹಂಚಿಕೊಳ್ಳಿ / Share")
            }
          }
        }
      )
    },
    floatingActionButton = {
      poem?.let {
        ExtendedFloatingActionButton(
          onClick = { sharePoem(context, it, poet?.nameKannada.orEmpty(), poet?.name.orEmpty()) },
          icon = { Icon(Icons.Default.Share, contentDescription = null) },
          text = { Text("✦ ಹಂಚಿಕೊಳ್ಳಿ / Share") }
        )
      }
    }
  ) { inner ->
    if (poem == null) {
      EmptyStateScreen("ಕವಿತೆ ಸಿಗಲಿಲ್ಲ / Poem not found")
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(inner),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          Column {
            Text(
              poem.titleKannada,
              style = MaterialTheme.typography.displayLarge.copy(fontSize = 26.sp),
              color = GoldenAccent
            )
            Text(
              poem.title,
              style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
              color = TextSecondary
            )
            poet?.let {
              Text(
                "${it.nameKannada} / ${it.name}",
                modifier = Modifier.clickable { navController.navigate("poet/${it.id}") },
                color = GoldenAccent
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              AssistChip(onClick = {}, label = { Text(poem.era) })
              poem.tags.take(3).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
            }
            Text("◆◇◆◇◆◇◆", color = GoldenAccent)
          }
        }
        item { PoemAudioPlayer(poem) }
        item {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("ಭಾಷೆ / Script:", modifier = Modifier.weight(1f))
            Text("ಕನ್ನಡ")
            Switch(checked = showTranslit, onCheckedChange = { showTranslit = it })
            Text("Roman")
          }
          AnimatedContent(targetState = showTranslit, label = "script") { show ->
            val displayText = if (show) poem.transliteration else poem.verse
            val annotated = remember(displayText, poem.wordMeanings) {
              buildVerseText(displayText, poem.wordMeanings, enableAnnotations = !show)
            }
            ClickableText(
              text = annotated,
              style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 19.sp,
                lineHeight = 32.sp,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
              ),
              onClick = { offset ->
                annotated.getStringAnnotations("WORD_MEANING", offset, offset).firstOrNull()?.let { ann ->
                  selectedWord = poem.wordMeanings.find { it.word == ann.item }
                }
              }
            )
          }
          WordMeaningSheet(selectedWord) { selectedWord = null }
        }
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, GoldenAccent)
          ) {
            Column(Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  "🇬🇧 English Translation / ಇಂಗ್ಲಿಷ್ ಭಾಷಾಂತರ",
                  modifier = Modifier.weight(1f),
                  color = GoldenAccent
                )
                IconButton(onClick = { translationOpen = !translationOpen }) {
                  Icon(
                    if (translationOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "ತೆರೆ / Expand"
                  )
                }
              }
              AnimatedVisibility(translationOpen) {
                Text(poem.englishTranslation, style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic))
              }
            }
          }
        }
        item { BhavarthaCard(poem.bhavartha, poem.bhavarthaKannada) }
        poet?.let {
          item {
            Card(colors = CardDefaults.cardColors(containerColor = CardSurface), modifier = Modifier.fillMaxWidth()) {
              Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = it.photoUrl, contentDescription = it.name, modifier = Modifier.size(48.dp).padding(2.dp))
                Spacer(Modifier.padding(8.dp))
                Column(Modifier.weight(1f)) {
                  Text("${it.nameKannada} / ${it.name}", color = GoldenAccent)
                  Text(it.period, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                TextButton(onClick = { navController.navigate("poet/${it.id}") }) {
                  Text("ಇನ್ನಷ್ಟು ತಿಳಿಯಿರಿ / Learn More")
                }
              }
            }
          }
        }
      }
    }
  }
}

private fun buildVerseText(
  text: String,
  words: List<WordMeaning>,
  enableAnnotations: Boolean
): AnnotatedString = buildAnnotatedString {
  append(text)
  if (enableAnnotations) {
    words.forEach { wordMeaning ->
      val word = wordMeaning.word
      if (word.isNotBlank()) {
        var start = text.indexOf(word)
        while (start >= 0) {
          val end = start + word.length
          addStyle(SpanStyle(color = GoldenAccent, textDecoration = TextDecoration.Underline), start, end)
          addStringAnnotation("WORD_MEANING", word, start, end)
          start = text.indexOf(word, startIndex = end)
        }
      }
    }
  }
}

private fun sharePoem(context: android.content.Context, poem: Poem, poetKannada: String, poetName: String) {
  val text = "${poem.titleKannada} | ${poem.title}\n\n${poem.verse}\n\n${poem.englishTranslation}\n\n— $poetKannada ($poetName)\n\nShared via ಕಾವ್ಯ-ಕಣಜ Kavya-Kanaja"
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, text)
  }
  context.startActivity(Intent.createChooser(intent, "ಹಂಚಿಕೊಳ್ಳಿ / Share"))
}
