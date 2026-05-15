package com.kavyakanaja.app

import android.app.Application
import com.google.firebase.FirebaseApp

class KavyaKanajaApp : Application() {
  override fun onCreate() {
    super.onCreate()
    FirebaseApp.initializeApp(this)
  }
}
