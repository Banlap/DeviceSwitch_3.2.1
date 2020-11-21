package com.aliyun.iot.ilop.demo.page.device.adddevice;

import com.aliyun.iot.ilop.demo.page.device.bean.FoundDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bean.SupportDeviceListItem;

import java.util.List;

public interface OnDeviceAddListener {


    void showToast(String message);


    void onSupportDeviceSuccess(List<SupportDeviceListItem> mSupportDeviceListItems);


    void onFilterComplete(List<FoundDeviceListItem> foundDeviceListItems);


}
