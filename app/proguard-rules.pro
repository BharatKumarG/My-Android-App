# Add project specific ProGuard rules here.
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
