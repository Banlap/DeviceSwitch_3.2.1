package com.aliyun.iot.ilop.page.mine.tripartite_platform.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.link.ui.component.nav.UIBarItem;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;

/**
 * @author sinyuk
 */
public class H5Activity extends MineBaseActivity {
    public static final int RESULT_CODE = 0X201;
    String mAppKey = "";
    private String mAuthCode;

    private WebView mWebView;
    private UINavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ilop_mine_h5_activity);
    }


    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setDisplayDividerEnable(false);
        mWebView = findViewById(R.id.mine_webview);
    }


    protected void initData() {
        initWebSetting();
        mWebView.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                navigationBar.setTitle(view.getTitle());
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isTokenUrl(url)) {
                    Intent intent = new Intent();
                    intent.putExtra("AuthCode", mAuthCode);
                    setResult(RESULT_CODE, intent);
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return false;
            }
        });
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    protected void initEvent() {
        navigationBar.setNavigationBackAction(new UIBarItem.Action() {
            @Override
            public void invoke(View view) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void initHandler() {

    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSetting() {
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    private boolean isTokenUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("code=")) {
                String[] urlArray = url.split("code=");
                if (urlArray.length > 1) {
                    String[] paramArray = urlArray[1].split("&");
                    if (paramArray.length > 1) {
                        mAuthCode = paramArray[0];
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!this.isFinishing()) {
            this.finish();
        }
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
