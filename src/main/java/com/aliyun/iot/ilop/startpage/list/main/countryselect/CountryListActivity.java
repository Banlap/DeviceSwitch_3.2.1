package com.aliyun.iot.ilop.startpage.list.main.countryselect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailRegisterCallback;
import com.alibaba.sdk.android.openaccount.ui.impl.OpenAccountUIServiceImpl;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.alibaba.sdk.android.openaccount.ui.widget.SiderBar;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.aep.widget.OASiderBar;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.SDKInitHelper;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iot.ilop.demo.page.login3rd.AuthCodeFragment;
import com.aliyun.iot.ilop.demo.page.login3rd.OALoginActivity;
import com.aliyun.iot.ilop.startpage.list.main.utils.LocateHandler.OnLocationListener;
import com.aliyun.iot.ilop.startpage.list.main.utils.LocateTask;
import com.aliyun.iot.ilop.startpage.list.main.utils.LocationUtil;
import com.aliyun.iot.ilop.startpage.list.main.view.LineTextView;
import com.aliyun.iot.ilop.startpage.list.main.view.OATitleBar;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.aliyun.iot.link.ui.component.LoadingCompact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 国家列表
 */
public class CountryListActivity extends AppCompatActivity implements OnLocationListener, View.OnClickListener {
    @SuppressWarnings("unused")
    public static final String URL = "app://aliyun.iot.aep.demo/page/country/select";
    private BaseAdapter adapter;
    private ListView mCountryList;
    private TextView overlay;
    private OASiderBar siderBar;
    private OverlayThread overlayThread;

    private ArrayList<IoTSmart.Country> mCountries = new ArrayList<>();
    private IoTSmart.Country mSelectedCountry;
    private boolean isChinaFormer = false;

    private WindowManager windowManager;
    //显示顶部定位国家
    private View headerView;
    private static final int ACCESS_COARSE_LOCATION1 = 1;
    private TextView defaultCountryName;
    private LocateTask locateTask;
    private Button btnSelectCountry;

    private String type = "phone";
    private String accountCode = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_list);

        type = this.getIntent().getStringExtra("type");
        accountCode = this.getIntent().getStringExtra("accountCode");

        OATitleBar countryTitle = findViewById(R.id.country_title);
        btnSelectCountry = findViewById(R.id.btn_select_country);

        mCountryList = findViewById(R.id.country_list);
        siderBar = findViewById(R.id.countryLetterListView);

        countryTitle.setType(OATitleBar.TYPE_IMAGE);
        countryTitle.setTitle(getString(R.string.activity_title_choose_country));
        btnSelectCountry.setVisibility(View.VISIBLE);
        btnSelectCountry.setOnClickListener(this);
        countryTitle.setBackgroundColor(Color.WHITE);
        mCountryList.setBackgroundColor(Color.WHITE);
        countryTitle.setBackClickListener(v -> finish());

        mSelectedCountry = IoTSmart.getCountry();

        LoadingCompact.showLoading(this);

        IoTSmart.getCountryList(new IoTSmart.ICountryListGetCallBack() {
            @Override
            public void onSucess(List<IoTSmart.Country> countryList) {
                if (countryList != null) {
                    String veryLongString = JSON.toJSONString(countryList);
                    int maxLogSize = 600;
                    for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i + 1) * maxLogSize;
                        end = end > veryLongString.length() ? veryLongString.length() : end;
                        ALog.d(TAG, veryLongString.substring(start, end));
                    }
                    onCountryListLoaded(countryList);
                    LoadingCompact.dismissLoading(CountryListActivity.this);

                } else {
                    ALog.d(TAG, "load country list failed");
                    LoadingCompact.dismissLoading(CountryListActivity.this);
                    Toast.makeText(CountryListActivity.this, "Failed to load list data from remote", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFail(String s, int i, String s1) {
                ALog.d(TAG, "load country list failed");

                LoadingCompact.dismissLoading(CountryListActivity.this);
                Toast.makeText(CountryListActivity.this, s1, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoadingCompact.dismissLoading(CountryListActivity.this);
    }

    private void onCountryListLoaded(List<IoTSmart.Country> countryList) {
        if (isDestroyed() || isFinishing()) return;
        if (null == countryList) {
            return;
        }
        mCountries.clear();
        mCountries.addAll(countryList);
        Collections.sort(mCountries, (o1, o2) -> CountryExt.getComparatorFiled(o1).compareTo(CountryExt.getComparatorFiled(o2)));
        siderBar.setInterval(0);
        siderBar.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<>();
        overlayThread = new OverlayThread();
        try {
            initOverlay();
        } catch (WindowManager.BadTokenException ignored) {

        }
        adapter = new ListAdapter(this, mCountries);
        mCountryList.setAdapter(adapter);
        mCountryList.setOnItemClickListener(new CityListOnItemClick());

        initHeaderView();

        int index = -1;
        for (int i = 0; i < mCountries.size(); i++) {
            IoTSmart.Country item = mCountries.get(i);
            if (item != null && mSelectedCountry != null) {
                if (Objects.equals(mSelectedCountry.domainAbbreviation, item.domainAbbreviation)) {
                    index = i;
                    break;
                }
            }
        }

        final int finalIndex = index;
        if (finalIndex >= 0) {
            mCountryList.setSelection(finalIndex);
        }
    }

    private ImageView mSelectedView;

    private void initHeaderView() {
        //如果没有权限请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CountryListActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION1);

        }
        //判断定位服务是否打开
        if (!LocationUtil.isLocationEnabled(this)) {
            LocationUtil.remindStartLocateService(this);
        }

        startLocation();
    }

    @Override
    public void onContinuedLocate(String text) {
        defaultCountryName.setText(text);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onFailLocate() {
        mLocatedCountry = null;
        if (headerView != null) {
            mCountryList.removeHeaderView(headerView);
        }
        headerView = View.inflate(CountryListActivity.this, R.layout.header_view_location_fail, null);
        LineTextView tv_location_again = headerView.findViewById(R.id.tv_location_again);
        tv_location_again.setText(getString(R.string.location_failed) + getString(R.string.location_failed_again));
        LinearLayout ll_header_fail_back = headerView.findViewById(R.id.ll_header_fail_back);
        ll_header_fail_back.setBackgroundColor(getResources().getColor(android.R.color.white));
        TextView tv_location_fail = headerView.findViewById(R.id.tv_location_fail);
        tv_location_fail.setTextColor(Color.BLACK);
        tv_location_again.setTextColor(Color.BLACK);
        headerView.setOnClickListener(v -> {
            //如果没有权限请求权限
            if (ContextCompat.checkSelfPermission(CountryListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(CountryListActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_COARSE_LOCATION1);

            }
            //判断定位服务是否打开
            if (!LocationUtil.isLocationEnabled(CountryListActivity.this)) {
                LocationUtil.remindStartLocateService(CountryListActivity.this);
            }
            startLocation();

        });
        mCountryList.addHeaderView(headerView);
    }

    private IoTSmart.Country mLocatedCountry = null;

    @Override
    public void onSuccessLocate(final IoTSmart.Country country) {
        mLocatedCountry = country;
        if (headerView != null) {
            mCountryList.removeHeaderView(headerView);
        }
        headerView = View.inflate(CountryListActivity.this, R.layout.header_view, null);
        TextView textView = headerView.findViewById(R.id.ilop_pagestart_default_country_name);
        mSelectedView = headerView.findViewById(R.id.code);
        textView.setText(country.areaName);

        headerView.setOnClickListener(v -> {
            mSelectedView.setVisibility(View.VISIBLE);
            mSelectedCountry = country;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
        mCountryList.addHeaderView(headerView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_select_country) {
            btnSelectCountry.setEnabled(false);
            if (mSelectedCountry != null) {

                IoTSmart.setCountry(mSelectedCountry, needRestartApp -> {
                    if (needRestartApp) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("切换安全图片")
                                .setMessage("切换国内/海外环境意味着切换安全图片，请确保yw_1222_*_production.jpg已被替换成相应项目下的安全图片").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        killProcess();
                                    }
                                }).setCancelable(false);
                        builder.create().show();
                    } else {
                        SDKInitHelper.init(AApplication.getInstance());
                        if ("phone".equals(type)) {//手机注册
                            OpenAccountUIService openAccountUIService = OpenAccountSDK.getService(OpenAccountUIService.class);
                            openAccountUIService.showRegister(CountryListActivity.this, getRegisterLoginCallback());
                        } else if ("mail".equals(type)) {//邮箱注册
                            OpenAccountUIService openAccountUIService = OpenAccountSDK.getService(OpenAccountUIService.class);
                            openAccountUIService.showEmailRegister(CountryListActivity.this, getEmailRegisterCallback());
                        } else if ("auto".equals(type)) {
                            Intent intent = new Intent();
                            intent.putExtra("accountCode", accountCode);
                            CountryListActivity.this.setResult(201, intent);
                        } else if ("oauto_google".equals(type)) {
                            Intent intent = new Intent();
                            intent.putExtra("accountCode", accountCode);
                            CountryListActivity.this.setResult(202, intent);
                        }
                        finish();
                    }
                });
            } else {
                LinkToast.makeText(v.getContext(), "Plz select a country first").show();
            }
            btnSelectCountry.setEnabled(true);
        }
    }


    private void killProcess() {
        LinkToast.makeText(this, R.string.region_restart_confirm).show();
        //finish();
        ThreadPool.MainThreadHandler.getInstance().post(() -> Process.killProcess(Process.myPid()), 2000);
    }


    public static final String TAG = "sinyuk";

    class CityListOnItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
            if (pos <= 0) {
                return;
            }
            IoTSmart.Country hit = (IoTSmart.Country) mCountryList.getAdapter().getItem(pos);
            mSelectedCountry = hit;
            if (mSelectedView != null) {
                if (Objects.equals(mLocatedCountry, hit)) {
                    mSelectedView.setVisibility(View.VISIBLE);
                } else {
                    mSelectedView.setVisibility(View.GONE);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }


    private void startLogin() {
        //跳转到登录界面
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                MainActivity.start(CountryListActivity.this);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onLoginFailed(int i, String s) {
                LinkToast.makeText(getApplicationContext(), s).show();
            }
        });

    }


    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母

    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<IoTSmart.Country> list = new ArrayList<>();

        @Override
        public void notifyDataSetChanged() {
            updateIndex();
            super.notifyDataSetChanged();
        }

        void updateIndex() {
            if (alphaIndexer == null) {
                alphaIndexer = new HashMap<>();
            }
            alphaIndexer.clear();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null) {
                    continue;
                }
                String indexName = CountryExt.getNameSort(list.get(i));
                String previewStr = (i - 1) >= 0 ? CountryExt.getNameSort(list.get(i - 1)) : " ";
                if (!previewStr.equals(indexName)) {
                    alphaIndexer.put(indexName, i);
                    sections[i] = indexName;
                }
            }
        }

        ListAdapter(Context context, List<IoTSmart.Country> data) {
            this.inflater = LayoutInflater.from(context);
            if (data != null) {
                this.list.addAll(data);
            }
            updateIndex();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.alpha = convertView.findViewById(R.id.alpha);
                holder.name = convertView.findViewById(R.id.name);
                holder.code = convertView.findViewById(R.id.code);
                holder.view_item_top_line = convertView.findViewById(R.id.view_item_top_line);
                holder.rl_item = convertView.findViewById(R.id.rl_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.rl_item.setBackgroundColor(Color.WHITE);
            holder.alpha.setBackgroundColor(Color.parseColor("#FFF6F6F6"));
            holder.alpha.setTextColor(Color.parseColor("#FF999999"));
            holder.name.setTextColor(Color.BLACK);
            holder.view_item_top_line.setBackgroundColor(Color.parseColor("#FFF5F5F5"));

            IoTSmart.Country item = list.get(position);
            if (item == null) {
                return convertView;
            }

            holder.name.setText(item.areaName);

            if (mSelectedCountry != null && item.domainAbbreviation.equals(mSelectedCountry.domainAbbreviation)) {
                holder.code.setVisibility(View.VISIBLE);
            } else {
                holder.code.setVisibility(View.GONE);
            }

            String currentStr = CountryExt.getNameSort(item);
            String previewStr = (position - 1) >= 0 ? CountryExt.getNameSort(list.get(position - 1)) : " ";
            if (!previewStr.equals(currentStr)) {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
                holder.view_item_top_line.setVisibility(View.GONE);
            } else {
                holder.alpha.setVisibility(View.GONE);
                holder.view_item_top_line.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha;
            TextView name;
            ImageView code;
            View view_item_top_line;
            RelativeLayout rl_item;
        }

    }

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.addView(overlay, lp);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windowManager != null && overlay != null) {
            windowManager.removeViewImmediate(overlay);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (locateTask != null) {
            locateTask.stopLocation();
            locateTask = null;
        }
        LocationUtil.cancelLocating();
        LoadingCompact.dismissLoading(this);
    }

    private Handler handler = new Handler();

    private class LetterListViewListener implements SiderBar.OnTouchingLetterChangedListener {
        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                Integer position = alphaIndexer.get(s);
                if (position == null) {
                    return;
                }
                mCountryList.setSelection(position + 1);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 700);
            }
        }

    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }


    private void startLocation() {
        if (headerView != null) {
            mCountryList.removeHeaderView(headerView);
        }
        AbsListView.LayoutParams localLayoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerView = View.inflate(this, R.layout.header_view, null);

        defaultCountryName = headerView.findViewById(R.id.ilop_pagestart_default_country_name);
        defaultCountryName.setTextColor(Color.parseColor("#FF333333"));

        defaultCountryName.setText(R.string.locating);
        headerView.setLayoutParams(localLayoutParams);
        mCountryList.addHeaderView(headerView);
        //首先请求一次定位服务
        LocationUtil.requestLocation(this);
        if (mCountries != null) {
            locateTask = new LocateTask(this, mCountries, this);
            locateTask.startLocation();
        }
    }

    @SuppressWarnings("unused")
    static class CountryExt {
        private static boolean isChina() {
            try {
                return IoTSmart.getLanguage().equalsIgnoreCase("zh-CN");
            } catch (Exception ignored) {
                return false;
            }
        }

        static String getNameSort(@NonNull IoTSmart.Country country) {
            try {
                if (isChina()) {
                    return country.pinyin.substring(0, 1);
                } else {
                    return country.areaName.substring(0, 1);
                }
            } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                return "";
            }
        }

        static String getComparatorFiled(@NonNull IoTSmart.Country country) {
            try {
                if (isChina()) {
                    return country.pinyin;
                } else {
                    return country.areaName;
                }
            } catch (NullPointerException ignored) {
                return "";
            }
        }
    }

    public static void loadCountriesFromLocal(Context context, IoTSmart.ICountryListGetCallBack callback) {
        ThreadPool.DefaultThreadPool.getInstance().submit(() -> {
            try {
                String jsonCountryList = getJson("country_list.json", context.getApplicationContext());
                List<IoTSmart.Country> result = JSON.parseArray(jsonCountryList, IoTSmart.Country.class);
                ThreadPool.MainThreadHandler.getInstance().post(() -> callback.onSucess(result));
            } catch (Exception ignored) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> callback.onFail("server", -1, ignored.getMessage()));
            }
        });
    }

    private static String getJson(@SuppressWarnings("SameParameterValue") String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    private EmailRegisterCallback getEmailRegisterCallback() {
        return new EmailRegisterCallback() {

            @Override
            public void onSuccess(OpenAccountSession session) {
                LoginCallback callback = OpenAccountUIServiceImpl._loginCallback;
                if (callback != null) {
                    callback.onSuccess(session);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                LoginCallback callback = OpenAccountUIServiceImpl._loginCallback;
                if (callback != null) {
                    callback.onFailure(code, message);
                }
            }

            @Override
            public void onEmailSent(String email) {
                Toast.makeText(getApplicationContext(), email + " 已经发送了", Toast.LENGTH_LONG).show();
            }

        };
    }

    protected LoginCallback getRegisterLoginCallback() {
        return new LoginCallback() {
            public void onFailure(int code, String msg) {
                LoginCallback loginCallback = OpenAccountUIServiceImpl._loginCallback;
                if (loginCallback != null) {
                    loginCallback.onFailure(code, msg);
                }

            }

            public void onSuccess(OpenAccountSession session) {
                LoginCallback loginCallback = OpenAccountUIServiceImpl._loginCallback;
                if (loginCallback != null) {
                    loginCallback.onSuccess(session);
                }

            }
        };
    }


}
