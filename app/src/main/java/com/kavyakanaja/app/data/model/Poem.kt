package com.kavyakanaja.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kavyakanaja.app.data.local.Converters

@Entity(tableName = "poems")
data class Poem(
  @PrimaryKey val id: Int = 0,
  val title: String = "",
  val titleKannada: String = "",
  val verse: String = "",
  val transliteration: String = "",
  val englishTranslation: String = "",
  val bhavartha: String = "",
  val bhavarthaKannada: String = "",
  val poetId: Int = 0,
  val era: String = "",
  val audioUrl: String = "",
  @TypeConverters(Converters::class)
  val wordMeanings: List<WordMeaning> = emptyList(),
  @TypeConverters(Converters::class)
  val tags: List<String> = emptyList(),
  val isFavorite: Boolean = false
)
