package com.pwj.jdbc;


import android.text.TextUtils;
import android.util.Log;

import com.pwj.utils.BitmapUtil;
import com.pwj.utils.IpConfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class Test2 {
    public static String uploadFile(String requestURL,String[] picPaths) {
        long time = System.currentTimeMillis();
        String urls = "";
        String boundary = UUID.randomUUID().toString(); // 边界标识 随机生成
        String prefix = "--", end = "\r\n";
        String content_type = "multipart/form-data"; // 内容类型
        String CHARSET = "utf-8"; // 设置编码
        int TIME_OUT = 10 * 10000000; // 超时时间
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", content_type + ";boundary=" + boundary);
            /**
             * 当文件不为空，把文件包装并且上传
             */
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(prefix);
            stringBuffer.append(boundary);
            stringBuffer.append(end);
            dos.write(stringBuffer.toString().getBytes());

            String name = "userName";
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + end);
            dos.writeBytes(end);
            dos.writeBytes("zhangSan");
            dos.writeBytes(end);


            for(int i = 0; i < picPaths.length; i++){
                if (TextUtils.isEmpty(picPaths[i])){
                    break;
                }
                File file = new File(BitmapUtil.compressImage(picPaths[i]));

                StringBuffer sb = new StringBuffer();
                sb.append(prefix);
                sb.append(boundary);
                sb.append(end);

                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                String filename=file.getName();
                String postfix=filename.substring(filename.lastIndexOf("."));
                String names= String.valueOf(time+i)+postfix;
                sb.append("Content-Disposition: form-data; name=\"" + i + "\"; filename=\"" + names + "\"" + end);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + end);
                sb.append(end);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[8192];//8k
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(end.getBytes());//一个文件结束标志
                urls = urls+ IpConfig.BASE_URL+names+",";
            }
            byte[] end_data = (prefix + boundary + prefix + end).getBytes();//结束 http 流
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();

            if (res == 200) {
                return urls;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}