# This is a configuration file for R8

-verbose
-allowaccessmodification
-repackageclasses


# -renamesourcefileattribute SourceFile
-keepparameternames
-keepattributes
                Exceptions,
                InnerClasses,
                EnclosingMethod,
                Signature,
                Deprecated,
                SourceFile,
                LineNumberTable,
                EnclosingMethod,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                AnnotationDefault

-allowaccessmodification
-dontoptimize
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses

-keepattributes *Annotation*


# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# We only need to keep ComposeView + FragmentContainerView
-keep public class androidx.compose.ui.platform.ComposeView {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#
## AndroidX + support library contains references to newer platform versions.
## Don't warn about those in case this com is linking against an older
## platform version.  We know about them, and they are safe.
#-dontwarn android.support.**
#-dontwarn androidx.**
#
#-dontwarn org.conscrypt.**
#
## Dagger
#-dontwarn com.google.errorprone.annotations.*
#
## Ignore annotation used for build tooling.
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
## Ignore JSR 305 annotations for embedding nullability information.
#-dontwarn javax.annotation.**
#
## Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
#-dontwarn kotlin.Unit
#
## Top-level functions that can only be used by Kotlin.
#-dontwarn retrofit2.KotlinExtensions
#-dontwarn retrofit2.KotlinExtensions$*
#
#
## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
## OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
#-dontwarn okhttp3.internal.platform.**
#-dontwarn org.conscrypt.**
#-dontwarn org.bouncycastle.**
#-dontwarn org.openjsse.**
#
#
#
## BUG(70852369): Surpress additional warnings after changing from Proguard to R8
#-dontwarn android.app.**
#-dontwarn android.graphics.**
#-dontwarn android.os.**
#-dontwarn android.view.**
#-dontwarn android.window.**
#
## Ignore warnings for hidden utility classes referenced from the shared lib
#-dontwarn com.android.internal.util.**
#
## Silence warnings from Compose tooling
#-dontwarn org.jetbrains.kotlin.**
#-dontwarn androidx.compose.animation.tooling.ComposeAnimation
#-dontwarn sun.misc.Unsafe
#
#-dontwarn com.google.firebase.messaging.TopicOperation$TopicOperations
#
## Silence warnings about classes that are available at runtime
#-dontwarn android.provider.DeviceConfig
#-dontwarn com.android.internal.colorextraction.ColorExtractor$GradientColors
#-dontwarn com.android.internal.logging.MetricsLogger
#-dontwarn com.android.internal.os.SomeArgs
#-dontwarn android.content.pm.ParceledListSlice
#-dontwarn com.android.internal.policy.ScreenDecorationsUtils
#-dontwarn android.util.StatsEvent
#-dontwarn android.service.wallpaper.IWallpaperEngine
#-dontwarn android.content.pm.UserInfo
#-dontwarn com.android.internal.app.IVoiceInteractionManagerService$Stub
#-dontwarn com.android.internal.app.IVoiceInteractionManagerService
#-dontwarn com.android.internal.annotations.VisibleForTesting
#-dontwarn android.provider.DeviceConfig$OnPropertiesChangedListener
#-dontwarn android.util.StatsEvent$Builder
#-dontwarn com.android.internal.colorextraction.types.Tonal
#-dontwarn android.content.pm.LauncherApps$AppUsageLimit
#-dontwarn android.provider.SearchIndexablesContract
#-dontwarn android.provider.SearchIndexablesProvider
#-dontwarn android.content.pm.IPackageManager
#
## The support library contains references to newer platform versions.
## Don't warn about those in case this app is linking against an older
## platform version.  We know about them, and they are safe.
#-dontwarn android.support.**


# Retain the generic signature of retrofit2.Call until added to Retrofit.
# Issue: https://github.com/square/retrofit/issues/3580.
# Pull request: https://github.com/square/retrofit/pull/3579.
-keep,allowobfuscation,allowshrinking class retrofit2.Call

# Retain annotation default values for all annotations.
# Required until R8 version >= 3.1.12+ (in AGP 7.1.0+).
-keep,allowobfuscation,allowshrinking @interface *

-keepclassmembers,allowobfuscation class * {
 @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class kotlin.coroutines.Continuation

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class com.jimmy.mhome.ui.home.** { *; }
-keep class com.jimmy.mhome.ui.details.** { *; }

-keep class kotlin.Metadata { *; }


-keep,allowshrinking,allowoptimization class com.jimmy.mhome.** {
  *;
}

# Proguard will strip methods required for talkback to properly scroll to
# next row when focus is on the last item of last row when using a RecyclerView
# Keep optimized and shrunk proguard to prevent issues like this when using
# support jar.
-keep class androidx.recyclerview.widget.RecyclerView { *; }

# Fragments
-keep class ** extends androidx.fragment.app.Fragment {
    public <init>(...);
}
-keep class ** extends android.app.Fragment {
    public <init>(...);
}

################ Do not optimize recents lib #############
-keep class com.android.systemui.** {
  *;
}

-keep class com.android.quickstep.** {
  *;
}

-keep class com.android.** {
  *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final ** CREATOR;
}

-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}


##---------------Begin: proguard configuration for Gson  ----------

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.jimmy.datasource.entities.** { <fields>; }
-keep class com.jimmy.datasource.model.** { <fields>; }
# -keep class com.jimmy.datasource.model.** { *; }
-keepclassmembers class vn.mobifoneglobal.mhome.datasource.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.LiveCameraModel
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.AuthResponse
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.CameraModel
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.CameraModel.*
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.Data
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.ForgetPassData
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.LoginData
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.LoginResponse
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.RegisterResponse
-keepclassmembers,allowobfuscation,allowshrinking class com.jimmy.datasource.model.PlaybackModel

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-dontwarn android.support.**
-keep class android.media.AudioTrack
-dontwarn org.webrtc.voiceengine.WebRtcAudioTrack



-keep class org.webrtc.**  { *; }

-keep class org.appspot.apprtc.**  { *; }

-keep class de.tavendo.autobahn.**  { *; }

-keep class io.antmedia.webrtcandroidframework.**  { *; }
-keep class io.antmedia.webrtcandroidframework.apprtc.**  { *; }

-keepclasseswithmembernames class * { native <methods>; }

#-keepnames class com.path.to.your.ParcelableArg
#-keepnames class com.path.to.your.SerializableArg
#-keepnames class com.path.to.your.EnumArg