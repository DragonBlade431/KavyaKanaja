package com.kavyakanaja.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kavyakanaja.app.data.model.WordMeaning

class Converters {
  private val gson = Gson()

  @TypeConverter
  fun fromWordMeaningList(value: List<WordMeaning>): String = gson.toJson(value)

  @TypeConverter
  fun toWordMeaningList(value: String): List<WordMeaning> {
    val type = object : TypeToken<List<WordMeaning>>() {}.type
    return gson.fromJson(value, type) ?: emptyList()
  }

  @TypeConverter
  fun fromStringList(value: List<String>): String = gson.toJson(value)

  @TypeConverter
  fun toStringList(value: String): List<String> {
    val type = object : TypeToken<List<String>>() {}.type
    return gson.fromJson(value, type) ?: emptyList()
  }
}
