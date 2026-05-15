package com.kavyakanaja.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {
  private val dataStore = context.dataStore

  companion object {
    private val Context.dataStore by preferencesDataStore("kavya_prefs")
    val LAST_READ_POEM_ID = intPreferencesKey("last_read_poem_id")
    val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
  }

  val lastReadPoemId: Flow<Int?> = dataStore.data.map { it[LAST_READ_POEM_ID] }
  val preferredLanguage: Flow<String> = dataStore.data.map { it[PREFERRED_LANGUAGE] ?: "kannada" }

  suspend fun setLastReadPoemId(id: Int) {
    dataStore.edit { it[LAST_READ_POEM_ID] = id }
  }

  suspend fun setPreferredLanguage(lang: String) {
    dataStore.edit { it[PREFERRED_LANGUAGE] = lang }
  }
}
