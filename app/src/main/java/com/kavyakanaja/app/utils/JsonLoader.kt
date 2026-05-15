package com.kavyakanaja.app.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kavyakanaja.app.R
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.Poet

object JsonLoader {
  fun loadPoemsFromJson(context: Context): List<Poem> {
    val json = context.resources.openRawResource(R.raw.poems).bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Poem>>() {}.type
    return Gson().fromJson(json, type) ?: emptyList()
  }

  fun loadPoetsFromJson(context: Context): List<Poet> {
    val json = context.resources.openRawResource(R.raw.poets).bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Poet>>() {}.type
    return Gson().fromJson(json, type) ?: emptyList()
  }
}
