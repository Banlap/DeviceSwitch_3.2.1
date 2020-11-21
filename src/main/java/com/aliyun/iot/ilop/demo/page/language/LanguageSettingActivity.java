package com.aliyun.iot.ilop.demo.page.language;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.LocaleList;

import android.os.Bundle;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.main.StartActivity;
import com.aliyun.iot.link.ui.component.LinkAlertDialog;
import com.aliyun.iot.link.ui.component.LoadingCompact;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LanguageSettingActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, LanguageSettingActivity.class);
        context.startActivity(starter);
    }

    private LanguageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);
        UINavigationBar navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setNavigationBackAction(view -> onBackPressed());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new LanguageAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        LoadingCompact.showLoading(this);
        loadData();
        ThreadPool.MainThreadHandler.getInstance().post(() -> LoadingCompact.dismissLoading(LanguageSettingActivity.this), 1000);
    }

    private int savedSelectedIndex = -1;

    private void loadData() {
        List<LanguageModel> languageModelList = new ArrayList<>();
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_simplified_chinese), Locale.SIMPLIFIED_CHINESE));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_english), Locale.US));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_simplified_french), Locale.FRANCE));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_simplified_German), Locale.GERMANY));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_japanese), Locale.JAPAN));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_korean), Locale.KOREA));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_spain), new Locale("es", "ES")));
        languageModelList.add(new LanguageModel(getString(R.string.mine_language_russian), new Locale("ru", "RU")));
        adapter.setList(languageModelList);
        String savedLanguage = IoTSmart.getLanguage();
        int index = -1;
        for (int i = 0; i < languageModelList.size(); i++) {
            LanguageModel item = languageModelList.get(i);
            String string = item.locale.getLanguage() + "-" + item.locale.getCountry();
            if (Objects.equals(savedLanguage, string)) {
                index = i;
                break;
            }
        }
        savedSelectedIndex = index;
        if (index != -1) adapter.setSelected(index);
    }

    @Override
    public void onBackPressed() {
        if (adapter.getSelected() == RecyclerView.NO_POSITION || adapter.getSelected() == savedSelectedIndex) {
            super.onBackPressed();
        } else {
            LanguageModel item = adapter.getList().get(adapter.getSelected());
            String language = item.locale.getLanguage() + "-" + item.locale.getCountry();
            new LinkAlertDialog.Builder(this)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.action_cancel), a -> {
                        a.dismiss();
                        finish();
                    })
                    .setNegativeButton(getString(R.string.action_confirm), a -> {
                        a.dismiss();
                        logout(language);
                    })
                    .setTitle(getString(R.string.action_switch_language, language))
                    .setMessage(getString(R.string.action_log_in_again))
                    .create()
                    .show();
        }
    }

    private void logout(String language) {
        if (isDestroyed() || isFinishing()) return;
        IoTSmart.setLanguage(language);
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                exit();
            }

            @Override
            public void onLogoutFailed(int i, String s) {
                exit();
            }
        });
    }

    private void exit() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingCompact.dismissLoading(this);
    }
}
