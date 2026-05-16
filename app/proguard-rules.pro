# Firebase
-keepattributes Signature
-keepattributes *Annotation*

# Firestore model classes
-keep class com.kavyakanaja.app.data.model.** { *; }

# Gson
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Coroutines
-dontwarn kotlinx.coroutines.**

# Media3 / ExoPlayer
-dontwarn androidx.media3.**

# Coil
-dontwarn coil.**
