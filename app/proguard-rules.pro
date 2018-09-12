# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\YiBin\eclipse\Android_SDK_windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class key to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn org.mozilla.javascript.**
-dontwarn com.jecelyin.editor.**
-dontwarn com.makeramen.**
-dontwarn org.junit.**
-dontwarn junit.**
-dontwarn jackpal.androidterm.**
-dontwarn com.iwebpp.nodeandroid.**
-dontwarn org.msgpack.core.**
-dontwarn com.pushtorefresh.storio.**
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

-keep class org.mozilla.javascript.** { *; }
-keep class com.jecelyin.editor.** { *; }
-keep class com.stardust.automator.** { *; }
-keep class com.stardust.autojs.** { *; }
-keep class org.greenrobot.eventbus.** { *; }
-keep class * extends c
-keepattributes *Annotation*
# Event bus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
# volley
-keepclassmembers class ** {
  @com.google.common.eventbus.Subscribe <methods>;
}
-keepclassmembers class ** {
  @com.some.package.server.JsonDeserializerWithOptions$FieldRequired public *;
}
-keep @interface com.some.package.server.JsonDeserializerWithOptions$FieldRequired
-keep class com.some.package.server.JsonDeserializerWithOptions
# autojs
-keepclassmembers class ** {
    @com.stardust.autojs.runtime.ScriptInterface <methods>;
}
# 920 editor
-keep class org.msgpack.** { *; }

# gson
-keep class * extends org.json.JSONObject {
    <fields>;
}

# JNI
-keepclasseswithmembernames class * {
    native <methods>;
}
# common
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keep class * extends android.support.v4.app.Fragment {
    <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes Signature
-keepattributes EnclosingMethod


# Required to preserve the Flurry SDK
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}
# Google Play Services library
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# AVLoadingView

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

# Bugly

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}