package com.aliyun.iot.ilop.demo.utils;

import com.aliyun.iot.ble.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtils {

    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {


        makeFilePath(filePath, fileName);



        String strFilePath = filePath + File.separator+ fileName;


        String strContent = strcontent + "\r\n";

        try {

            File file = new File(strFilePath);

            if (!file.exists()) {

                Log.d("TestFile", "Create the file:" + strFilePath);

                File parent_file =  file.getParentFile();
                if(parent_file != null){
                    if(parent_file.mkdirs())
                    {
                        Log.d("TestFile", "parent file path:" + parent_file.getPath());
                    }
                }


                if(!file.createNewFile())
                {
                    Log.d("TestFile", "文件已经存在,不需要创建");
                }

            }

            RandomAccessFile raf = new RandomAccessFile(file, "rwd");

            raf.seek(file.length());

            raf.write(strContent.getBytes());

            raf.close();

        } catch (Exception e) {

            Log.e("TestFile", "Error on write File:" + e);

        }

    }



    //生成文件

    public static File makeFilePath(String filePath, String fileName) {

        File file = null;

        makeRootDirectory(filePath);

        try {

            file = new File(filePath + fileName);

            if (!file.exists()) {

                if(!file.createNewFile())
                {
                    Log.d("TestFile", "文件已经存在,不需要创建");
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return file;

    }



    //生成文件夹

    public static void makeRootDirectory(String filePath) {

        File file = null;

        try {

            file = new File(filePath);

            if (!file.exists()) {

                if(file.mkdir()){
                    Log.d("TestFile", "file path:" + file.getPath());
                }

            }

        } catch (Exception e) {

            Log.i("error:", e + "");

        }

    }



    //读取指定目录下的所有TXT文件的文件内容

    public static String getFileContent(File file) {

        String content = "";

        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)

            if (file.getName().endsWith("txt")) {//文件格式为""文件

                try {

                    InputStream instream = new FileInputStream(file);



                    InputStreamReader inputreader

                            = new InputStreamReader(instream, StandardCharsets.UTF_8);

                    BufferedReader buffreader = new BufferedReader(inputreader);

                    String line = "";

                    //分行读取

                    while ((line = buffreader.readLine()) != null) {

                        content += line + "\n";

                    }

                    instream.close();//关闭输入流



                } catch (java.io.FileNotFoundException e) {

                    Log.d("TestFile", "The File doesn't not exist.");

                } catch (IOException e) {

                    Log.d("TestFile", Objects.requireNonNull(e.getMessage()));

                }

            }

        }

        return content;

    }
}

