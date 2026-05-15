package com.kavyakanaja.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.app.data.repository.PoetRepository
import com.kavyakanaja.app.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PoetViewModel(application: Application) : AndroidViewModel(application) {
  private val repo = PoetRepository(application)
  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading
  private val _isOnline = MutableStateFlow(false)
  val isOnline: StateFlow<Boolean> = _isOnline

  val allPoets = repo.allPoets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val jnanpithPoets = repo.jnanpithPoets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    refresh()
  }

  fun refresh() {
    viewModelScope.launch {
      _isOnline.value = NetworkUtils.isOnline(getApplication())
      try {
        repo.syncPoets()
      } catch (_: Exception) {
      }
      _isLoading.value = false
    }
  }
}
