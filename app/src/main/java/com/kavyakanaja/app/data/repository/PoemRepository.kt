package com.kavyakanaja.app.data.repository

import android.content.Context
import com.kavyakanaja.app.data.local.AppDatabase
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.remote.FirestoreService
import com.kavyakanaja.app.utils.JsonLoader
import com.kavyakanaja.app.utils.NetworkUtils
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull

class PoemRepository(private val context: Context) {
  private val db = AppDatabase.getInstance(context)
  private val firestoreService = FirestoreService()
  private val dao = db.poemDao()

  val allPoems: Flow<List<Poem>> = dao.getAllPoems()
  val favorites: Flow<List<Poem>> = dao.getFavorites()

  suspend fun syncPoems() {
    val localSeed = JsonLoader.loadPoemsFromJson(context)
    if (dao.count() == 0) dao.insertAll(localSeed)
    if (NetworkUtils.isOnline(context)) {
      withTimeoutOrNull(8000) {
        if (firestoreService.isPoemsCollectionEmpty()) {
          firestoreService.seedPoems(localSeed)
        } else {
          val remotePoems = firestoreService.fetchAllPoems()
          if (remotePoems.isNotEmpty()) dao.insertAll(remotePoems)
        }
      }
    }
  }

  fun getPoemOfTheDay(poems: List<Poem>): Poem? {
    if (poems.isEmpty()) return null
    val cal = Calendar.getInstance()
    val seed = cal.get(Calendar.YEAR) * 1000L + cal.get(Calendar.DAY_OF_YEAR)
    return poems[(seed % poems.size).toInt()]
  }

  suspend fun getPoemById(id: Int): Poem? = dao.getPoemById(id)

  suspend fun toggleFavorite(poemId: Int, isFavorite: Boolean) {
    dao.updateFavorite(poemId, isFavorite)
  }

  fun getPoemsByPoet(poetId: Int): Flow<List<Poem>> = dao.getPoemsByPoet(poetId)
}
