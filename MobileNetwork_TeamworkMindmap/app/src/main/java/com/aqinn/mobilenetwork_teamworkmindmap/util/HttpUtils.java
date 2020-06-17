package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络请求工具
 */
public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();

    /**
     * HTTP GET方法
     * @param urlstr 传入地址
     * @return 网页结果
     */
    public static String get(String urlstr) {

        try {
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream stream = conn.getInputStream();
                return dealResponseResult(stream);

            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP del方法
     * @param urlstr 传入地址
     * @return 网页结果
     */
    public static String doDelete(String urlstr) {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("DELETE");

            //获得一个输出流，向服务器写入数据
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(params.getBytes());

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream stream = conn.getInputStream();
                return dealResponseResult(stream);

            } else {
                Log.d(TAG, "<<<<<response=" + response);
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * HTTP POST方法
     * @param urlstr 传入地址
     * @return 网页结果
     */
    public static String post(final String urlstr) {

        try {
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");

            //设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //设置请求体的长度
//            conn.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));

            //获得一个输出流，向服务器写入数据
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(params.getBytes());

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream stream = conn.getInputStream();
                return dealResponseResult(stream);

            } else {
                Log.d(TAG, "<<<<<response=" + response);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * HTTP PUT方法
     * @param urlstr 传入地址
     * @return 网页结果
     */
    public static String put(final String urlstr) {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("PUT");

            //获得一个输出流，向服务器写入数据
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(params.getBytes());

            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream stream = conn.getInputStream();
                return dealResponseResult(stream);

            } else {
                Log.d(TAG, "<<<<<response=" + response);
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 返回结果转换为字符串
     * @param stream 接受流
     * @return 处理后的字符串
     * @throws IOException
     */
    private static String dealResponseResult(InputStream stream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }


}
