package com.kavyakanaja.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kavyakanaja.app.data.model.Poem
import com.kavyakanaja.app.data.model.Poet

@Database(entities = [Poem::class, Poet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun poemDao(): PoemDao
  abstract fun poetDao(): PoetDao

  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
      Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "kavyakanaja.db")
        .fallbackToDestructiveMigration()
        .build()
        .also { INSTANCE = it }
    }
  }
}
