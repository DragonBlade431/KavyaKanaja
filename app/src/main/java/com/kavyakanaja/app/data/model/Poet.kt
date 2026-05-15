package com.kavyakanaja.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kavyakanaja.app.data.local.Converters

@Entity(tableName = "poets")
data class Poet(
  @PrimaryKey val id: Int = 0,
  val name: String = "",
  val nameKannada: String = "",
  val period: String = "",
  val bio: String = "",
  val bioKannada: String = "",
  val jnanpithYear: Int? = null,
  @TypeConverters(Converters::class)
  val famousWorks: List<String> = emptyList(),
  val photoUrl: String = "",
  val photoPlaceholderColor: String = "#B5451B"
)
