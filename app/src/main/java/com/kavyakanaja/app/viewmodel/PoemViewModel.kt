package com.kavyakanaja.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.preferences.UserPreferences
import com.kavyakanaja.app.data.repository.PoemRepository
import com.kavyakanaja.app.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoemViewModel(application: Application) : AndroidViewModel(application) {
  private val repo = PoemRepository(application)
  private val prefs = UserPreferences(application)
  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading
  private val _isOnline = MutableStateFlow(false)
  val isOnline: StateFlow<Boolean> = _isOnline
  private val _selectedTag = MutableStateFlow("All")
  val selectedTag: StateFlow<String> = _selectedTag
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery
  private val _selectedPoem = MutableStateFlow<Poem?>(null)
  val selectedPoem: StateFlow<Poem?> = _selectedPoem

  val allPoems: StateFlow<List<Poem>> = repo.allPoems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val poemOfTheDay: StateFlow<Poem?> = allPoems
    .map { repo.getPoemOfTheDay(it) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val filteredPoems: StateFlow<List<Poem>> = combine(allPoems, _selectedTag, _searchQuery) { poems, tag, query ->
    poems.filter { poem ->
      val matchesTag = tag == "All" || poem.tags.any { it.equals(tag, ignoreCase = true) }
      val matchesQuery = query.isBlank() ||
        poem.title.contains(query, ignoreCase = true) ||
        poem.titleKannada.contains(query) ||
        poem.transliteration.contains(query, ignoreCase = true)
      matchesTag && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val favorites: StateFlow<List<Poem>> = repo.favorites
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val lastReadPoemId: StateFlow<Int?> = prefs.lastReadPoemId
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  init {
    refresh()
  }

  fun refresh() {
    viewModelScope.launch {
      _isOnline.value = NetworkUtils.isOnline(getApplication())
      try {
        repo.syncPoems()
      } catch (_: Exception) {
      }
      _isLoading.value = false
    }
  }

  fun filterByTag(tag: String) {
    _selectedTag.value = tag
  }

  fun search(query: String) {
    _searchQuery.value = query
  }

  fun selectPoem(poem: Poem) {
    _selectedPoem.value = poem
  }

  fun getPoemsByPoet(poetId: Int) = repo.getPoemsByPoet(poetId)

  fun rememberLastRead(poemId: Int) {
    viewModelScope.launch { prefs.setLastReadPoemId(poemId) }
  }

  fun toggleFavorite(poem: Poem) {
    viewModelScope.launch { repo.toggleFavorite(poem.id, !poem.isFavorite) }
  }
}
