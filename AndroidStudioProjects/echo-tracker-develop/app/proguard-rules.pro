# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# enum classes
-keep class com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications { *; }
-keep class com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState { *; }
-keep class com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper$ApiVersion { *; }
-keep class com.tmobile.mytmobile.echolocate.lte.utils.LteEventType { *; }
-keep class com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener$Type { *; }
-keep class com.tmobile.mytmobile.echolocate.network.model.NetworkRequestType { *; }
-keep class com.tmobile.mytmobile.echolocate.network.model.RetryMechanism { *; }
-keep class com.tmobile.mytmobile.echolocate.network.model.RequestStatus { *; }
-keep class com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.core.utils.ApplicationState { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper$ApiVersion { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEventType { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEventType { *; }
-keep class com.tmobile.mytmobile.echolocate.voice.utils.CallState { *; }
-keep class com.tmobile.mytmobile.echolocate.voice.utils.VoiceEventType { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.CellsMonitor$CELL_INFO_TYPE { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.CoverageTelephonyDataCollector$SIM_STATE { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.CoverageTelephonyDataCollector$NETWORK_TYPE { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.CoverageTelephonyDataCollector$SERVICE_STATE { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.VolteStateEnum { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.utils.CoverageEventType { *; }
-keep class com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsEventType { *; }

# model classes
-keep class com.tmobile.mytmobile.echolocate.analytics.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.lte.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.network.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.voice.model.* { *; }
-keep class com.tmobile.mytmobile.echolocate.coverage.model.* { *; }


# dsdk handshake classes
-keepclassmembers class com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel.DsdkHandshakeParametersModel { *; }

-dontwarn java.util.concurrent.Flow$Subscriber
-dontwarn java.util.concurrent.Flow$Subscription
-dontwarn java.util.concurrent.Flow$Processor
-dontwarn java.util.concurrent.Flow$Publisher

-dontwarn com.google.android.gms.dynamic.zzf
-dontwarn com.google.android.gms.internal.zzl
-dontwarn com.google.android.gms.internal.zzac
-dontwarn com.google.android.gms.internal.zzbxn
-dontwarn com.google.android.gms.common.api.zzc
-dontwarn com.google.android.gms.common.api.Api$zza
-dontwarn com.google.android.gms.common.internal.zzf$zzb
-dontwarn com.google.android.gms.common.internal.zzf$zzc

-dontwarn com.google.android.gms.common.internal.safeparcel.zza
-dontwarn com.google.android.gms.internal.zzaad$zza
-dontwarn com.google.android.gms.internal.zzn$zza
-dontwarn com.google.android.gms.internal.zzn$zzb

-dontwarn android.test.**

