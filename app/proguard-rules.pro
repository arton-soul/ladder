# Proguard rules for LexiLadder
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
