# Proguard rules for LexiLadder

# General Attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Room Database
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.dearyoti.lexiladder.data.local.entity.** { *; }
-keep class com.dearyoti.lexiladder.data.local.dao.** { *; }

# JSON Serialization / Gson Models
-keep class com.dearyoti.lexiladder.data.content.model.** { *; }
-keep class com.dearyoti.lexiladder.domain.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Google Play Billing
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.api.**

# Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }
-dontwarn com.google.android.gms.ads.**
