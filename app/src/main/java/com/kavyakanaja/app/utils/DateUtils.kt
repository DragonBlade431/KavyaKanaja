package com.kavyakanaja.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
  fun getTodayFormatted(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d yyyy", Locale.ENGLISH)
    return sdf.format(Date())
  }
}
