
# GENERAL ANDROID / KOTLIN
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keepattributes SourceFile, LineNumberTable

# --- Kotlin Serialization ---
# Keep metadata for all @Serializable classes
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.json.** { *; }

# Keep classes annotated with @Serializable
-keep @kotlinx.serialization.Serializable class * { *; }

# Keep serializer companions automatically generated
-keepclassmembers class * {
    public static ** Companion;
    public static ** serializer(...);
}

# Keep @SerialName and @Transient annotated fields
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
    @kotlinx.serialization.Transient <fields>;
}


# RETROFIT + OKHTTP
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep interface com.iamashad.ashad_swipe.data.remote.SwipeApi
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.squareup.retrofit2.converter.kotlinx.serialization.** { *; }

# ROOM (KSP)
-keep class androidx.room.** { *; }
-keep class com.iamashad.ashad_swipe.data.db.** { *; }
-dontwarn androidx.room.**

# KOIN (Dependency Injection)
-keep class org.koin.** { *; }
-keep class com.iamashad.ashad_swipe.di.** { *; }
-dontwarn org.koin.**

# WORKMANAGER
# Keep only your custom workers
-keep class com.iamashad.ashad_swipe.work.UploadPendingWorker { *; }

# Keep Koin's WorkerFactory
-keep class org.koin.androidx.workmanager.factory.KoinWorkerFactory { *; }

# Optional: keep any class extending Worker (future safe)
-keep class * extends androidx.work.ListenableWorker { *; }

-dontwarn androidx.work.**

# COIL 3
-keep class coil.** { *; }
-keep class io.coil.** { *; }
-dontwarn coil.**
-dontwarn io.coil.**

# LOTTIE
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# JETPACK COMPOSE
-keep class androidx.compose.** { *; }
-keep class androidx.activity.compose.** { *; }
-dontwarn androidx.compose.**

# DATASTORE
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# PALETTE
-keep class androidx.palette.graphics.** { *; }
-dontwarn androidx.palette.graphics.**

# NOTIFICATION UTILITIES
-keep class com.iamashad.ashad_swipe.util.NotificationHelper { *; }
-keep class com.iamashad.ashad_swipe.util.NotificationPrefs { *; }

# VIEWMODELS
-keep class com.iamashad.ashad_swipe.**ViewModel { *; }

# MISC / ANNOTATION SAFETY
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
