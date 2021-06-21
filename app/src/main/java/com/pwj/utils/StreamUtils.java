package com.pwj.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by han13688 on 2018/4/24.
 */

public class StreamUtils {

    private static ByteArrayOutputStream out;

    public static String streamToString(InputStream inputStream){
        String result="";
        try {
            out = new ByteArrayOutputStream();
        byte[] butter=new  byte[1024];
        int length=0;
            while ((length=inputStream.read(butter))!=-1){
                out.write(butter,0,length);
                out.flush();
            }
            result=new String(out.toByteArray(),"gbk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;

    }
}
