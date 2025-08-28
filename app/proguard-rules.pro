# Add project specific ProGuard rules here.
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-dontwarn dagger.hilt.android.internal.builders.*
-dontwarn dagger.hilt.android.internal.managers.*
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.myandroidappenhanced.**$$serializer { *; }
-keepclassmembers class com.example.myandroidappenhanced.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.myandroidappenhanced.** {
    kotlinx.serialization.KSerializer serializer(...);
}
