package com.pwj.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by han on 2018/8/13.
 */

public class StreamUtilsNull {

    private static FileInputStream fileInputStream;
    private static String result="";



    public static byte[] StreamUtils(String path) throws Exception {
        File file=new File(path);
        FileInputStream fileInputStream=new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[]butter = new byte[1024];
        int length = 0;
        while ((length = fileInputStream.read(butter)) != -1) {
            byteArrayOutputStream.write(butter,0,length);
            byteArrayOutputStream.flush();
        }

//        result=new String(byteArrayOutputStream.toByteArray(),"gb2312");
        return byteArrayOutputStream.toByteArray();
    }

//    public static String StreamUtilsNull(String path) throws Exception {
//        File file=new File(path);
//        FileInputStream fileInputStream=new FileInputStream(file);
//        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//        byte[]butter = new byte[1024];
//        int length = 0;
//        while ((length = fileInputStream.read(butter)) != -1) {
//            byteArrayOutputStream.write(butter,0,length);
//            byteArrayOutputStream.flush();
//        }
//
//        result=new String(byteArrayOutputStream.toByteArray(),"gb2312");
//        return result;
//    }
}
