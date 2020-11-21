package com.aliyun.iot.ilop.startpage.list.main.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.link.ui.component.LinkAlertDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    private static final String TAG = "LocationUtil";

    /**
     * 获取国家信息
     */
    static Address getCountryCode(Context context) {
        Location location = getLocationInfo(context);
        if (location != null) {
            try {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                List<Address> locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (locationList.size() > 0) {
                    // String countryCode = address.getCountryCode();//得到国家名称，比方：中国
                    //  Log.i(TAG, "解析获取国家码：" + countryCode);
                    return locationList.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "解析获取国家码失败");
        return null;
    }


    /**
     * 使用系统定位方式
     *
     * @return
     */
    private static Location getLocationInfo(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "提示无法定位,请先打开位置权限");
        } else {
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //设置垂直方向精确度
                //从可用的位置提供器中，匹配以上标准的最佳提供器
                // String locationProvider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    //不为空,显示地理位置经纬度
                    Log.i(TAG, "获取定位信息成功");
                    return location;
                }
            } catch (Exception ex) {
                Log.i(TAG, "获取定位信息出错" + ex.getMessage());
            }
        }
        Log.i(TAG, "获取定位信息失败");
        return null;
    }

    private static LocationManager locationManager = null;
    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 请求定位
     *
     * @param context
     */
    public static void requestLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "提示无法定位,请先打开位置权限");
        } else {
            try {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, (float) 1.0, locationListener);
            } catch (Exception ex) {
                Log.i(TAG, "请求定位信息出错" + ex.getMessage());
            }
        }

    }

    //取消定位
    public static void cancelLocating() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }

    //判断定位服务是否开启
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    //提醒打开定位服务
    public static void remindStartLocateService(final Context context) {
        String title = AApplication.getInstance().getString(R.string.component_unopened_location_service);
        String message = AApplication.getInstance().getString(R.string.component_unopened_location_service_des);
        LinkAlertDialog dialog = new LinkAlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(AApplication.getInstance().getString(R.string.component_set_up), new LinkAlertDialog.OnClickListener() {
            @Override
            public void onClick(LinkAlertDialog linkAlertDialog) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                linkAlertDialog.dismiss();
            }
        }).setNegativeButton(AApplication.getInstance().getString(R.string.component_cancel), LinkAlertDialog::dismiss).create();
        dialog.show();
    }


}