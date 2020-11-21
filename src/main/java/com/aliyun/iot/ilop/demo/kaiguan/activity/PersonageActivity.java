package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.fragment.MainFragment;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iot.ilop.demo.page.login3rd.OALoginActivity;
import com.aliyun.iot.ilop.startpage.list.main.countryselect.CountryListActivity;

import java.util.LinkedHashMap;
import java.util.Map;


public class PersonageActivity extends LoginActivity implements View.OnClickListener {


    private TextView talto_text;
    private RelativeLayout mima_layout,nicheng_layout,touxiang_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personage);
        initView();
    }

    private void initView() {
        talto_text=findViewById(R.id.talto_text);
        talto_text.setText("我的资料");
        mima_layout=findViewById(R.id.mima_layout);
        nicheng_layout=findViewById(R.id.nicheng_layout);
        nicheng_layout.setOnClickListener(this);
        mima_layout.setOnClickListener(this);
        touxiang_layout=findViewById(R.id.touxiang_layout);
        touxiang_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mima_layout:
                forgetPhonePassword(view);
                break;
            case R.id.nicheng_layout:
                Intent intent=new Intent(PersonageActivity.this,NiChengActivity.class);
                startActivity(intent);
                break;
            case R.id.touxiang_layout:
                choosePhoto();
                break;


        }
    }

    public void forgetPhonePassword(View view) {
        super.forgetPassword(view);
    }

    //选择头像
    private void choosePhoto() {
        Intent intentPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentPicture, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //banlap： 返回头像路径
        if(data != null) {
            if (data.getData() != null) {
                try {
                    Uri selectedImage = data.getData();//获取路径
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                    changeByAccountPhoto(bitmap, selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //更新账号的头像信息
    private void changeByAccountPhoto(Bitmap bitmap, Uri imageUri) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("avatarUrl", imageUri);
        OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
        oas.updateProfile(PersonageActivity.this, map, new LoginCallback() {
            @Override
            public void onSuccess(OpenAccountSession openAccountSession) {
                Toast.makeText(PersonageActivity.this,"修改头像成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(PersonageActivity.this,"修改头像失败"+s,Toast.LENGTH_SHORT).show();
            }
        });
    }
}