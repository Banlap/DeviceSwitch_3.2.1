package com.aliyun.iot.ilop.demo.page.device.scan;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.aliyun.alink.alirn.dev.BoneDevHelper;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.component.scan.IScanPlugin;
import com.aliyun.iot.demo.R;

import java.util.regex.Pattern;

/**
 * Created by guikong on 17/11/24.
 */
public class BoneMobileScanPlugin implements IScanPlugin {

    private final static String QUERY_KEY_BONE_DEBUG_HOST = "boneDebugHost";

    @Override
    public boolean executePlugin(Context context, String s) {
        boolean handle = false;

        if (TextUtils.isEmpty(s)) {
            return handle;
        }

        try {
            Uri uri = Uri.parse(s);
            String host = uri.getQueryParameter(QUERY_KEY_BONE_DEBUG_HOST);
            handle = !TextUtils.isEmpty(host);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return handle;
    }

    @Override
    public void dealCode(final Context context, String s) {

        if (TextUtils.isEmpty(s)) {
            return;
        }

        Uri uri = Uri.parse(s);
        String ip = uri.getQueryParameter(QUERY_KEY_BONE_DEBUG_HOST);

        if (TextUtils.isEmpty(ip)) {
            return;
        }

        // check ip
        boolean match = Pattern.matches("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))", ip);

        if (!match) {
            Toast.makeText(context, R.string.rncontainer_illeagel_ip_address, Toast.LENGTH_SHORT).show();
            return;
        }

        BoneDevHelper.saveBoneDebugServer(context, ip);

        new BoneDevHelper().getBundleInfoAsync(context, ip, new BoneDevHelper.OnBondBundleInfoGetListener() {
            @Override
            public void onSuccess(BoneDevHelper.BoneBundleInfo boneBundleInfo) {
                BoneDevHelper.RouterInfo info = new BoneDevHelper().handleBundleInfo(context, boneBundleInfo);

                if (null == info) {
                    return;
                }

                Router.getInstance().toUrl(context, info.url, info.bundle);
            }

            @Override
            public void onError(String message, Exception e) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                if (null != e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
