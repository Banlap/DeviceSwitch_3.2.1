package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.aliyun.iot.aep.sdk.scan.utils.AlbumPathUtils.getDataColumn;
import static com.aliyun.iot.aep.sdk.scan.utils.AlbumPathUtils.isDownloadsDocument;
import static com.aliyun.iot.aep.sdk.scan.utils.AlbumPathUtils.isExternalStorageDocument;
import static com.aliyun.iot.aep.sdk.scan.utils.AlbumPathUtils.isMediaDocument;

public class DataGliActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_file_output, rl_file_input;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_fl_activity);
        initView();

        if (ContextCompat.checkSelfPermission(DataGliActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager. PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }
    }

    private void initView() {
        rl_file_output=findViewById(R.id.rl_file_output);
        rl_file_output.setOnClickListener(this);
        rl_file_input=findViewById(R.id.rl_file_input);
        rl_file_input.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_file_output:

                String temp = "This is a test file？";
                File file = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                FileUtils.writeTxtToFile(temp, file.getPath(), "deviceData.txt");

                Toast.makeText(DataGliActivity.this, "内容导出成功： deviceData.txt已保存在download文件中", Toast.LENGTH_LONG).show();
              /*  String content = "保存数据";
                //创建一个文件输入对象
                FileOutputStream file = null;
                try {
                    //打开一个文件
                    file = openFileOutput("deviceData.txt", MODE_WORLD_READABLE);
                    //存入内容
                    file.write(content.getBytes());
                    //清理缓存
                    file.flush();
                    Toast.makeText(DataGliActivity.this, "内容导出成功", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
                break;
            case R.id.rl_file_input:
                //Uri uri = Uri.parse("content://downloads");
                //Intent intentFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
                intentFile.addCategory(Intent.CATEGORY_OPENABLE);
                intentFile.setType("text/plain");
                startActivityForResult(intentFile, 1);

                /*//读取文件内的数据
                try {
                    FileInputStream readfile = openFileInput("deviceData.txt");

                    //实例化 byte对象
                    byte []butter = new byte[readfile.available()];
                    //数据的读取
                    readfile.read(butter);
                    //关闭文件
                    readfile.close();
                    //将byte类型的数据进行转换
                    String content1 = new String(butter);
                    Toast.makeText(DataGliActivity.this, "读取内容成功:" + content1, Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String path = getPath(this, uri);
            Toast.makeText(DataGliActivity.this, "导入内容：", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            //一些三方的文件浏览器会进入到这个方法中，例如ES
            //QQ文件管理器不在此列

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File

        }
        return null;
    }



}
