package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.content.Context;
import android.view.ViewGroup;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.adapter.BaseRecycleViewAdapter;
import com.aliyun.iot.ilop.demo.page.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.LocalDeviceFoundViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.LocalDeviceTitleViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.LocalScanDeviceTitleViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.NoSupportDeviceViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.SupportDeviceItemViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder.SupportDeviceTitleViewHolder;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDevice;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bean.LocalDevciceTitle;
import com.aliyun.iot.ilop.demo.page.device.bean.LocalScanDeviceTilte;
import com.aliyun.iot.ilop.demo.page.device.bean.NoSupportDeviceTitle;
import com.aliyun.iot.ilop.demo.page.device.bean.SupportDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bean.SupportDeviceTitle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceAdapter extends BaseRecycleViewAdapter<FoundDevice> {
    private static final String TAG = AddDeviceAdapter.class.getSimpleName();

    private static final int LOCAL_DEVICE = 0x10000002;
    private static final int SUPPORT_DEVICE_TITLE = 0x10000003;
    private static final int SUPPORT_DEVICE = 0x10000004;
    private static final int NOSUPPORT_DEVICE_TITLE = 0x10000005;
    private static final int SCAN_LOCAL_DEVICE_TITLE = 0x10000006;
    private static final int LOCAL_DEVICE_TITLE = 0x10000007;


    private LocalDevciceTitle localDevciceTitle;//本地设备title
    private LocalScanDeviceTilte localScanDeviceTilte;//扫描
    private SupportDeviceTitle supportDeviceTitle;//支持的设备title
    private NoSupportDeviceTitle noSupportDeviceTitle;//不支持设备title


    AddDeviceAdapter(Context context) {
        super(context);
        localDevciceTitle = new LocalDevciceTitle();
        localScanDeviceTilte = new LocalScanDeviceTilte();
        supportDeviceTitle = new SupportDeviceTitle();
        noSupportDeviceTitle = new NoSupportDeviceTitle();
        mDatas.add(0, localScanDeviceTilte);
        mDatas.add(1, localDevciceTitle);
        mDatas.add(2, supportDeviceTitle);
    }


    public void resetLocalDevice() {
        int supportIndex = mDatas.indexOf(supportDeviceTitle);

        List<FoundDevice> foundDevices = new ArrayList<>(mDatas.subList(2, supportIndex));
        mDatas.removeAll(foundDevices);
        notifyItemRangeRemoved(2, foundDevices.size());
    }

    public void addLocalDevice(List<FoundDeviceListItem> foundDeviceListItems) {
        if (foundDeviceListItems == null) {
            return;
        }
        int supportIndex = mDatas.indexOf(supportDeviceTitle);
        mDatas.addAll(supportIndex, foundDeviceListItems);
        notifyItemRangeInserted(supportIndex, foundDeviceListItems.size());
    }


    public void setSupportDevices(List<SupportDeviceListItem> data) {
        if (data == null) {
            return;
        }
        int supportIndex = mDatas.indexOf(supportDeviceTitle);
        if (data.size() == 0) {
            mDatas.add(supportIndex + 1, noSupportDeviceTitle);
            notifyItemInserted(supportIndex + 1);
        } else {
            mDatas.addAll(data);
            notifyItemRangeInserted(supportIndex + 1, data.size());
        }
    }


    @Override
    public int getItemViewType(int position) {
        FoundDevice foundDevice = mDatas.get(position);
        if (foundDevice instanceof FoundDeviceListItem) {
            return LOCAL_DEVICE;
        } else if (foundDevice instanceof SupportDeviceTitle) {
            return SUPPORT_DEVICE_TITLE;
        } else if (foundDevice instanceof SupportDeviceListItem) {
            return SUPPORT_DEVICE;
        } else if (foundDevice instanceof NoSupportDeviceTitle) {
            return NOSUPPORT_DEVICE_TITLE;
        } else if (foundDevice instanceof LocalScanDeviceTilte) {
            return SCAN_LOCAL_DEVICE_TITLE;
        } else if (foundDevice instanceof LocalDevciceTitle) {
            return LOCAL_DEVICE_TITLE;
        }
        return super.getItemViewType(position);
    }

    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case LOCAL_DEVICE:
                return new LocalDeviceFoundViewHolder(inflater.inflate(R.layout.device_listview_item, parent, false));
            case SUPPORT_DEVICE_TITLE:
                return new SupportDeviceTitleViewHolder(inflater.inflate(R.layout.deviceadd_support_device_title, parent, false));
            case SUPPORT_DEVICE:
                return new SupportDeviceItemViewHolder(inflater.inflate(R.layout.device_listview_item, parent, false));
            case NOSUPPORT_DEVICE_TITLE:
                return new NoSupportDeviceViewHolder(inflater.inflate(R.layout.deviceadd_no_support_device, parent, false));
            case SCAN_LOCAL_DEVICE_TITLE:
                return new LocalScanDeviceTitleViewHolder(inflater.inflate(R.layout.deviceadd_local_scan_title, parent, false));
            case LOCAL_DEVICE_TITLE:
                return new LocalDeviceTitleViewHolder(inflater.inflate(R.layout.deviceadd_local_device_title, parent, false));
        }
        throw new IllegalArgumentException("IllegalArgumentException no such viewType");
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //noinspection unchecked
        holder.onBind(mDatas.get(position), position);
    }
}
