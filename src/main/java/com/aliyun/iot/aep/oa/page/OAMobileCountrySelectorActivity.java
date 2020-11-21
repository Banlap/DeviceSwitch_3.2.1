package com.aliyun.iot.aep.oa.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.openaccount.ui.RequestCode;
import com.alibaba.sdk.android.openaccount.ui.model.CountrySort;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.oa.OALanguageUtils;
import com.aliyun.iot.aep.oa.page.adapter.CountryCodeAdapter;
import com.aliyun.iot.aep.oa.page.data.GetCountryNameSort;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.aep.widget.OASiderBar;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.startpage.list.main.view.OATitleBar;
import com.aliyun.iot.link.ui.component.LoadingCompact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * Created by feijie.xfj on 18/4/11.
 * 此代码基本复制MobileCountrySelectorActivity，只做UI定制，逻辑不做修改
 */

public class OAMobileCountrySelectorActivity extends AppCompatActivity {
    public static final String TAG = "CountrySelector";

    public static void start(Activity context) {
        Intent starter = new Intent(context, OAMobileCountrySelectorActivity.class);
        context.startActivityForResult(starter, RequestCode.MOBILE_COUNTRY_SELECTOR_REQUEST);
    }

    private ListView mCountryListView;
    private CountryCodeAdapter adapter;

    protected OASiderBar mSiderBar;

    private CountryComparator countryComparator = new CountryComparator();

    protected GetCountryNameSort countryChangeUtil;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ali_sdk_openaccount_mobile_country_selector2);
        sp = getSharedPreferences("ilop_sp", Context.MODE_PRIVATE);
        this.mCountryListView = this.findViewById(R.id.country_list);
        this.mSiderBar = this.findViewById(R.id.country_sidebar);
        this.countryChangeUtil = new GetCountryNameSort();
        this.mCountryListView.setOnItemClickListener((adapterView, view, position, arg3) -> {
            CountrySort selectedCountrySort = (CountrySort) adapter.getItem(position);
            sp.edit().putString("phoneCode", selectedCountrySort.code).apply();
            Intent intent = new Intent();
            intent.putExtra("countryCode", selectedCountrySort.code);
            setResult(-1, intent);
            finish();
        });
        LinearLayout mRootView = findViewById(R.id.main);


        OATitleBar titleBar = findViewById(R.id.oat_title);

        //设置全面屏
        titleBar.setType(OATitleBar.TYPE_IMAGE);
        titleBar.setTitle(getString(R.string.account_select_country_code));
        mRootView.setBackgroundColor(Color.WHITE);
        titleBar.setBackClickListener(v -> finish());
        mSiderBar.setInterval(0);
        mSiderBar.setOnTouchingLetterChangedListener(s -> {
            if (adapter != null) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mCountryListView.setSelection(position);
                }
            }
        });

        LoadingCompact.showLoading(this);


        IoTSmart.getCountryList(new IoTSmart.ICountryListGetCallBack() {
            @Override
            public void onSucess(List<IoTSmart.Country> list) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    LoadingCompact.dismissLoading(OAMobileCountrySelectorActivity.this);
                    if (list != null && !list.isEmpty()) {
                        asyncCountryList(list);
                    } else {
                        ALog.d(TAG, "load country list failed");
                        LoadingCompact.dismissLoading(OAMobileCountrySelectorActivity.this);
                        Toast.makeText(OAMobileCountrySelectorActivity.this, "Failed to load list data from remote", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFail(String s, int i, String s1) {
                ALog.d(TAG, "load country list failed");
                LoadingCompact.dismissLoading(OAMobileCountrySelectorActivity.this);
                Toast.makeText(OAMobileCountrySelectorActivity.this, s1, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(OALanguageUtils.attachBaseContext(newBase));
    }


    private class CountryComparator implements Comparator<CountrySort> {
        CountryComparator() {
        }

        public int compare(CountrySort o1, CountrySort o2) {
            return !o1.sortLetters.equals("@") && !o2.sortLetters.equals("#") ? (!o1.sortLetters.equals("#") && !o2.sortLetters.equals("@") ? o1.sortLetters.compareTo(o2.sortLetters) : 1) : -1;
        }
    }


    protected List<CountrySort> getHot(List<CountrySort> listToAdd) {
        //热门国家8个
        List<CountrySort> newList = new ArrayList<>();
        try {
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("CHN", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("FRA", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("DEU", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("JPN", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("KOR", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("RUS", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("ESP", listToAdd))));
            newList.add(CountrySort.getCountryHot(Objects.requireNonNull(getCountryForCode("GBR", listToAdd))));
        } catch (Exception ignored) {

        }
        return newList;
    }

    private CountrySort getCountryForCode(String isoCode, List<CountrySort> listToAdd) {
        for (int i = 0; i < listToAdd.size(); i++) {
            if (listToAdd.get(i).englishName.equals(isoCode)) {
                return listToAdd.get(i);
            }
        }
        return null;
    }


    private void asyncCountryList(List<IoTSmart.Country> mCountryList) {
        List<CountrySort> countryList = new ArrayList<>();
        boolean isChina = false;
        try {
            isChina = IoTSmart.getLanguage().equalsIgnoreCase("zh-CN");
        } catch (Exception ignored) {

        }
        for (int i = 0; i < mCountryList.size(); i++) {
            IoTSmart.Country countryBean = mCountryList.get(i);
            CountrySort country = new CountrySort();
            country.code = countryBean.code;
            if (isChina) {
                String sortLetter = this.countryChangeUtil.getSortLetterBySortKey(countryBean.pinyin);
                if (sortLetter == null) {
                    sortLetter = this.countryChangeUtil.getSortLetter(countryBean.areaName);
                }
                country.sortLetters = sortLetter;
                country.displayName = countryBean.areaName;
            } else {
                country.sortLetters = this.countryChangeUtil.getSortLetterBySortKey(countryBean.areaName);
                country.displayName = countryBean.areaName;
            }
            //将isoCode赋值到englishName中
            country.englishName = countryBean.isoCode;
            countryList.add(country);
        }
        List<CountrySort> hot = getHot(countryList);
        Collections.sort(countryList, countryComparator);
        countryList.addAll(0, hot);
        adapter = new CountryCodeAdapter(OAMobileCountrySelectorActivity.this, countryList);
        mCountryListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingCompact.dismissLoading(this);
    }
}
