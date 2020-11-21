package com.aliyun.iot.ilop.demo.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.adapter.PalettesDialogListviewAdapter;
import com.aliyun.iot.ilop.demo.page.bean.PalettesDialogBean;

import java.util.List;

public class HSVDialog {

    private ASlideDialog menuDialog;
    private ListView listView;
    private PalettesDialogListviewAdapter adapter;
    private static HSVDialog hsvDialog;
    private ImageView cancleIv;

    private HSVDialog() {
    }

    public static HSVDialog getInstance() {
        if (hsvDialog == null) {
            hsvDialog = new HSVDialog();
        }
        return hsvDialog;
    }

    /**
     * 显示调色板dialog
     */
    public void showMenuDialog(Context context, String color, OnHSVItemClickListener onHSVItemClickListener) {
//        if (TextUtils.isEmpty(color)) return;
        menuDialog = ASlideDialog.newInstance(context, ASlideDialog.Gravity.Bottom, R.layout.hsv_palettes_dialog);
        menuDialog.setCanceledOnTouchOutside(true);
        listView = menuDialog.findViewById(R.id.hsv_palettes_lv);
        cancleIv = menuDialog.findViewById(R.id.hsv_palettes_cancle_iv);
        adapter = new PalettesDialogListviewAdapter(context);
        listView.setAdapter(adapter);

        adapter.setSelectPos(color);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectPos(adapter.getArr().get(position).getColor());
            if (onHSVItemClickListener != null) {
                onHSVItemClickListener.onItemClick(adapter.getArr(), position);
            }
            hideMenuDialog();
        });
        cancleIv.setOnClickListener(v -> hideMenuDialog());
        menuDialog.show();

    }

    private void hideMenuDialog() {
        if (menuDialog != null) {
            menuDialog.hide();
        }
    }


    public interface OnHSVItemClickListener {
        void onItemClick(List<PalettesDialogBean> arr, int position);
    }
}
