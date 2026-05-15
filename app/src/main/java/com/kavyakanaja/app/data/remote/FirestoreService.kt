package com.kavyakanaja.app.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.Poet
import kotlinx.coroutines.tasks.await

class FirestoreService {
  private val db = Firebase.firestore

  suspend fun fetchAllPoems(): List<Poem> = try {
    db.collection("poems").get().await().documents.mapNotNull { doc ->
      try {
        doc.toObject(Poem::class.java)
      } catch (_: Exception) {
        null
      }
    }.sortedBy { it.id }
  } catch (_: Exception) {
    emptyList()
  }

  suspend fun fetchAllPoets(): List<Poet> = try {
    db.collection("poets").get().await().documents.mapNotNull { doc ->
      try {
        doc.toObject(Poet::class.java)
      } catch (_: Exception) {
        null
      }
    }.sortedBy { it.id }
  } catch (_: Exception) {
    emptyList()
  }

  suspend fun isPoemsCollectionEmpty(): Boolean = try {
    db.collection("poems").limit(1).get().await().isEmpty
  } catch (_: Exception) {
    false
  }

  suspend fun isPoetsCollectionEmpty(): Boolean = try {
    db.collection("poets").limit(1).get().await().isEmpty
  } catch (_: Exception) {
    false
  }

  suspend fun seedPoems(poems: List<Poem>) {
    val batch = db.batch()
    poems.forEach { poem ->
      batch.set(db.collection("poems").document(poem.id.toString()), poem)
    }
    batch.commit().await()
  }

  suspend fun seedPoets(poets: List<Poet>) {
    val batch = db.batch()
    poets.forEach { poet ->
      batch.set(db.collection("poets").document(poet.id.toString()), poet)
    }
    batch.commit().await()
  }
}
