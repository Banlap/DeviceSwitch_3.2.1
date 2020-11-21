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

#=========================
# Application 全局统一配置
#=========================

# ----不做混淆优化----
-dontoptimize

# ----混淆时不会产生形形色色的类名----
-dontusemixedcaseclassnames

# ----指定不忽略非公共的库类----
-dontskipnonpubliclibraryclasses

# ----混淆包路径----
-repackageclasses ''

# ----优化时允许访问并修改有修饰符的类和类的成员----
-allowaccessmodification

# ----保护类型----
-keepattributes *Annotation*,SourceFile,Signature,LineNumberTable,InnerClasses

# ----打印混淆信息----
-printmapping map.txt


-keepattributes InnerClasses

# ----保留R文件----
-keep class **.R
-keep class **.R$* {
    <fields>;
}

#=========================
# android system
#=========================
-dontwarn android.test.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




#=========================
# 以下混淆保护，在各个SDK配置之后会被删除
#=========================
-dontwarn java.awt.**
-dontwarn android.support.v4.**
-dontwarn com.ut.share.executor.MessageExecutor

-dontwarn com.aliyun.alink.linksdk.tools.tracker.AntSdkTracker.**
-dontwarn com.alibaba.fastjson.**
#-dontwarn com.alibaba.sdk.android.oauth.**
#-dontwarn com.alibaba.sdk.android.openaccount.**
-dontwarn com.aliyun.alink.linksdk.**
-dontwarn com.facebook.**
-dontwarn okhttp3.internal.**
-dontwarn okio.**


#=========================
# 各 SDK 的配置
#=========================

# account.begin
-keep class com.aliyun.iot.aep.sdk.login.**{*;}
# account.end


# account-oa.begin
-keep class com.aliyun.iot.aep.sdk.login.oa.**{*;}
# account-oa.end

# open-account.begin
-keep class com.alibaba.sdk.android.*
# open-account.end

# Wireless-Security.begin
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
# Wireless-Security.end

# API-Client.begin
-keep public class com.aliyun.iot.aep.sdk.apiclient.** {
    public <methods>;
    public <fields>;
}
# API-Client.end

# component-rncontainer.begin

# react-native.begin
# keep class not found with httpclient
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn android.util.FloatMath
-dontwarn okio.**
-dontwarn com.facebook.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.internal.huc.DelegatingHttpsURLConnection
-dontwarn okhttp3.internal.huc.OkHttpsURLConnection
-dontwarn android.support.v8.renderscript.**

-keep class com.facebook.**{*;}
# react-native.end
-keep  class * implements com.facebook.react.bridge.NativeModule {
    public <methods>;
    protected <methods>;
}

# keep view manager
-keep  class * extends com.facebook.react.uimanager.ViewManager {
    public <methods>;
    protected <methods>;
}

# keep js module
-keep  class * extends com.facebook.react.bridge.JavaScriptModule {
    public <methods>;
}

# keep shadow node
-keep class * extends com.facebook.react.uimanager.ReactShadowNode{
    public <methods>;
    protected <methods>;
}

# keep ReactPackage
-keep class * implements com.facebook.react.ReactPackage{
    public <methods>;
}

# keep interface class
-keep class com.aliyun.alink.alirn.RNContainer{
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.RNContainerConfig{
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.RNGlobalConfig{
    public <methods>;
    public <fields>;
}

# BoneDevHelper
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$RouterInfo{
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$BoneBundleInfo{
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.BoneDevHelper$OnBondBundleInfoGetListener{
   public <methods>;
}

# cache
-keep public class com.aliyun.alink.alirn.cache.**{
    public <methods>;
}

# launch
-keep class com.aliyun.alink.alirn.launch.LaunchOptionsFactory{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.launch.OnLoadingStatusChangedListener{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.launch.LoadingStatus{
    public <fields>;
}

#preload
-keep class com.aliyun.alink.alirn.preload.ReactInstanceManagerWrapperPool{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.preload.PreloadConfiguration{
    public <methods>;
}

-keep public class com.aliyun.alink.alirn.preload.sdk.**{
    public <methods>;
    public <fields>;
}

# biz package
-keep class com.aliyun.alink.alirn.rnpackage.biz.BizPackageHolder{
    public <methods>;
}

# ut
-keep class com.aliyun.alink.alirn.usertracker.IUserTracker{
    public <methods>;
}
-keep class com.aliyun.alink.alirn.usertracker.UserTrackerHolder{
    public <methods>;
}

#utils
-keep class com.aliyun.alink.alirn.utils.SimpleRNConfig{
    public <methods>;
}
# component-rncontainer.end


# BoneBridge.begin
# keep bone api
-keep  class * extends com.aliyun.alink.sdk.jsbridge.methodexport.BaseBonePlugin {
     @com.aliyun.alink.sdk.jsbridge.methodexport.MethodExported <methods>;
     public <fields>;
}
# BoneBridge.end


# BundleManager.begin
-dontwarn com.aliyun.iot.aep.component.bundlemanager.ocache.bean.**
-keep class com.aliyun.iot.aep.component.bundlemanager.ocache.bean.** {*;}
-keep class com.aliyun.iot.aep.component.bundlemanager.ocache.BundleManager{
    public <methods>;
}
-keep class com.aliyun.alink.page.rn.PluginConfigManager.PluginConfigData.** {}
# BundleManager.end

# for Router

 -keep class com.aliyun.iot.aep.routerexternal.** {
     public <methods>;
     public <fields>;
 }

# Push.begin
-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**
-dontwarn com.huawei.android.**
-keep class com.alibaba.sdk.android.push.** {
    public <methods>;
}
# Push.end


# SDK-Manager.begin
-keep class com.aliyun.iot.aep.demo.BundleManager.** {}

-keep class * extends com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp {
    *;
}

-keep class * implements com.aliyun.iot.aep.sdk.framework.sdk.ISDKDelegate  {
    *;
}
# SDK-Manager.end

# OA
-keep public class com.aliyun.iot.aep.sdk.login.data.**{*;}

# IOT-Credential
-keep public class com.aliyun.iot.aep.sdk.credential** {
    public <methods>;
    public <fields>;
}

# devicecenter & coap
-keep public class com.aliyun.alink.business.devicecenter.** {*;}
-keep public class com.aliyun.alink.linksdk.alcs.coap.**{*;}

# tmp sdk
-keep class com.aliyun.alink.linksdk.tmp.**{*;}
-keep class com.aliyun.alink.linksdk.cmp.**{*;}
-keep class com.aliyun.alink.linksdk.alcs.**{*;}

# data bean
-keep class com.aliyun.iot.ilop.demo.page.bean.**{*;}

#ota
-keep class com.aliyun.iot.ilop.demo.page.ota.bean.**{*;}


#移动端设备网关 SDK
-keep class com.aliyun.alink.linksdk.channel.gateway.api.**{*;}


-keep class com.aliyun.alink.linksdk.channel.**{*;}

#breeze 蓝牙
-keep class com.aliyun.iot.ble.**{*;}
-keep class com.aliyun.iot.breeze.**{*;}
-dontwarn com.aliyun.iot.breeze.**
-keep class org.mozilla.** {*;}
-dontwarn org.mozilla.**
#


# OkHttp3
-dontwarn okhttp3.OkHttpClient$Builder

#rn 路由混淆配置
-keep class com.aliyun.alink.page.rn.router.RouterManager$RouterData{
    public <fields>;
}

-keep class com.aliyun.alink.linksdk.logextra.** {*;}

# 小米通道
-keep class com.xiaomi.** {*;}
-dontwarn com.xiaomi.**
# 华为通道
-keep class com.huawei.** {*;}
-dontwarn com.huawei.**

-dontwarn com.aliyun.iot.debugcanary.**

# api-client-biz
-keep public class com.aliyun.alink.apiclient.biz.** {
    public <methods>;
    public <fields>;
}

-dontwarn com.aliyun.alink.apiclient.biz.**

-keep class com.aliyun.iot.aep.component.bundlemanager.bean.*{*;}

-keep class com.aliyun.iot.aep.sdk.bridge.base.BaseBoneService{
         *;
}

# keep bone api
-keep public class com.aliyun.iot.aep.sdk.bridge.base.BaseBoneService{
    private boolean isBoneInit;
}
# for BoneBridge @ End

# for BundleManager @ begin
-keep class com.aliyun.iot.aep.component.bundlemanager.BundleManager{*;}
# for BundleManager @ end

# for Router
-keep class com.aliyun.alink.page.rn.** {
    public <methods>;
    public <fields>;
}
-keep class com.aliyun.alink.alirn.dev.**{
    public <methods>;
    public <fields>;
}

-ignorewarnings -keep class * { public private *; }


#ipcview
-keep class com.aliyun.iot.demo.ipcview.**{*;}
-keep class com.google.android.exoplayer2.**{*;}
-keep class com.aliyun.iotx.linkvisual.**{*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#ipcview

# keep netty
-keepattributes Signature,InnerClasses
-keepclasseswithmembers class io.netty.** {
    *;
}
-dontwarn io.netty.**
-dontwarn sun.**
#

-keep class com.aliyun.iot.aep.sdk.IoTSmart{*;}

-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.**{*;}
