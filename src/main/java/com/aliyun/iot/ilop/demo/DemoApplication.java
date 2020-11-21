package com.aliyun.iot.ilop.demo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.alibaba.sdk.android.openaccount.ConfigManager;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import com.aliyun.iot.aep.component.router.IUrlHandler;
import com.aliyun.iot.aep.component.scan.ScanManager;
import com.aliyun.iot.aep.routerexternal.RouterExternal;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.bundle.BundleManager;
import com.aliyun.iot.aep.sdk.framework.bundle.PageConfigure;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.device.scan.BoneMobileScanPlugin;
import com.aliyun.iot.ilop.page.scan.ScanPageInitHelper;

import java.util.ArrayList;

/**
 * Created by wuwang on 2017/10/30.
 */

public class DemoApplication extends AApplication {

    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();


        // 其他 SDK, 仅在 主进程上初始化
        String packageName = this.getPackageName();
        if (!packageName.equals(ThreadTools.getProcessName(this, android.os.Process.myPid()))) {
            return;
        }

        ConfigManager.getInstance().setBundleName("com.aliyun.iot.demo");
        SDKInitHelper.init(this);
        
        /* 加载Native页面 */
        BundleManager.init(this, (application, configure) -> {
            if (null == configure || null == configure.navigationConfigures)
                return;

            ArrayList<String> nativeUrls = new ArrayList<>();
            ArrayList<PageConfigure.NavigationConfigure> configures = new ArrayList<>();

            PageConfigure.NavigationConfigure deepCopyItem = null;
            for (PageConfigure.NavigationConfigure item : configure.navigationConfigures) {
                if (null == item.navigationCode || item.navigationCode.isEmpty() || null == item.navigationIntentUrl || item.navigationIntentUrl.isEmpty())
                    continue;

                deepCopyItem = new PageConfigure.NavigationConfigure();
                deepCopyItem.navigationCode = item.navigationCode;
                deepCopyItem.navigationIntentUrl = item.navigationIntentUrl;
                deepCopyItem.navigationIntentAction = item.navigationIntentAction;
                deepCopyItem.navigationIntentCategory = item.navigationIntentCategory;

                configures.add(deepCopyItem);

                nativeUrls.add(deepCopyItem.navigationIntentUrl);

                ALog.d("BundleManager", "register-native-page: " + item.navigationCode + ", " + item.navigationIntentUrl);

                RouterExternal.getInstance().registerNativeCodeUrl(deepCopyItem.navigationCode, deepCopyItem.navigationIntentUrl);
                RouterExternal.getInstance().registerNativePages(nativeUrls, new NativeUrlHandler(deepCopyItem));
            }
        });

        // 支持扫码调试
        ScanManager.getInstance().registerPlugin("boneMobile", new BoneMobileScanPlugin());

        //初始化pagescan页面的router配置
        ScanPageInitHelper.initPageScanRouterConfig();

        //初始化之后，可以改变显示语言,目前支持中文“zh-CN”, 英文"en-US"，法文"fr-FR",德文"de-DE",日文"ja-JP",韩文"ko-KR",西班牙文"es-ES",俄文"ru-RU"，八种语言
        //switchLanguage("zh-CN");
        //修改OA多语言 目前支持Locale.US 英文、Locale.SIMPLIFIED_CHINESE 中文、Locale.FRANCE 法语、Locale.JAPAN 日语、Locale.GERMANY 德语、Locale.KOREA 韩语  、new Locale("ru","RU") 俄语、new Locale("es","ES") 西班牙语
        //OALanguageHelper.setLanguageCode(Locale.SIMPLIFIED_CHINESE);

        /** FixMe
         * LinkVisual摄像头界面初始化
         */
        //IPCViewHelper.getInstance().init(this,"2.0.0");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
    }


    /**
     * help class
     */
    static final private class NativeUrlHandler implements IUrlHandler {

        private final String TAG = "NativeUrlHandler";

        private final PageConfigure.NavigationConfigure navigationConfigure;

        NativeUrlHandler(PageConfigure.NavigationConfigure configures) {
            this.navigationConfigure = configures;
        }

        @Override
        public void onUrlHandle(Context context, String url, Bundle bundle, boolean startActForResult, int reqCode) {
            ALog.d(TAG, "onUrlHandle: url: " + url);
            if (null == context || null == url || url.isEmpty())
                return;

            /* prepare the intent */
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));

            if (null != this.navigationConfigure.navigationIntentAction)
                intent.setAction(this.navigationConfigure.navigationIntentAction);
            if (null != this.navigationConfigure.navigationIntentCategory)
                intent.addCategory(this.navigationConfigure.navigationIntentCategory);

            if (Build.VERSION.SDK_INT >= 26) {//解决android8.0路由冲突问题，将intent行为限制在本应用内
                intent.setPackage(context.getPackageName());
            }

            /* start the navigated activity */
            ALog.d(TAG, "startActivity(): url: " + this.navigationConfigure.navigationIntentUrl + ", startActForResult: " + startActForResult + ", reqCode: " + reqCode);
            this.startActivity(context, intent, bundle, startActForResult, reqCode);
        }

        private void startActivity(Context context, Intent intent, Bundle bundle, boolean startActForResult, int reqCode) {
            if (null == context || null == intent)
                return;


            if (null != bundle) {
                intent.putExtras(bundle);
            }
            /* startActivityForResult() 场景，只能被 Activity 调用 */
            if (startActForResult) {
                if (false == (context instanceof Activity))
                    return;
                ((Activity) context).startActivityForResult(intent, reqCode);
                return;
            }

            /* startActivity 被 Application 调用时的处理 */
            if (context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            /* startActivity 被 Activity、Service 调用时的处理 */
            else if (context instanceof Activity || context instanceof Service) {
                context.startActivity(intent);
            }
            /* startActivity 被其他组件调用时的处理 */
            else {
                // 暂不支持
            }
        }
    }



}
