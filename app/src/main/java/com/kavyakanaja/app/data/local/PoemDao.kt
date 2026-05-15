package com.kavyakanaja.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kavyakanaja.app.data.model.Poem
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemDao {
  @Query("SELECT * FROM poems ORDER BY id ASC")
  fun getAllPoems(): Flow<List<Poem>>

  @Query("SELECT * FROM poems WHERE id = :id")
  suspend fun getPoemById(id: Int): Poem?

  @Query("SELECT * FROM poems WHERE isFavorite = 1 ORDER BY id ASC")
  fun getFavorites(): Flow<List<Poem>>

  @Query("SELECT * FROM poems WHERE poetId = :poetId ORDER BY id ASC")
  fun getPoemsByPoet(poetId: Int): Flow<List<Poem>>

  @Query("SELECT COUNT(*) FROM poems")
  suspend fun count(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(poems: List<Poem>)

  @Query("UPDATE poems SET isFavorite = :fav WHERE id = :id")
  suspend fun updateFavorite(id: Int, fav: Boolean)
}
