package com.kavyakanaja.app.data.repository

import android.content.Context
import com.kavyakanaja.app.data.local.AppDatabase
import com.kavyakanaja.app.data.model.Poet
import com.kavyakanaja.app.data.remote.FirestoreService
import com.kavyakanaja.app.utils.JsonLoader
import com.kavyakanaja.app.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow

class PoetRepository(private val context: Context) {
  private val db = AppDatabase.getInstance(context)
  private val firestoreService = FirestoreService()
  private val dao = db.poetDao()

  val allPoets: Flow<List<Poet>> = dao.getAllPoets()
  val jnanpithPoets: Flow<List<Poet>> = dao.getJnanpithPoets()

  suspend fun syncPoets() {
    val localPoets = JsonLoader.loadPoetsFromJson(context)
    if (dao.count() == 0) dao.insertAll(localPoets)
    if (NetworkUtils.isOnline(context)) {
      if (firestoreService.isPoetsCollectionEmpty()) {
        firestoreService.seedPoets(localPoets)
      } else {
        val remotePoets = firestoreService.fetchAllPoets()
        if (remotePoets.isNotEmpty()) dao.insertAll(remotePoets)
      }
    }
  }

  suspend fun getPoetById(id: Int): Poet? = dao.getPoetById(id)
}
