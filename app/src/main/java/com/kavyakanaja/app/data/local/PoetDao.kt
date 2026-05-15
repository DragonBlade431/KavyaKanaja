package com.kavyakanaja.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kavyakanaja.app.data.model.Poet
import kotlinx.coroutines.flow.Flow

@Dao
interface PoetDao {
  @Query("SELECT * FROM poets ORDER BY id ASC")
  fun getAllPoets(): Flow<List<Poet>>

  @Query("SELECT * FROM poets WHERE id = :id")
  suspend fun getPoetById(id: Int): Poet?

  @Query("SELECT * FROM poets WHERE jnanpithYear IS NOT NULL ORDER BY jnanpithYear ASC")
  fun getJnanpithPoets(): Flow<List<Poet>>

  @Query("SELECT COUNT(*) FROM poets")
  suspend fun count(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(poets: List<Poet>)
}
