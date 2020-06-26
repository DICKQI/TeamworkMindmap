package com.aqinn.mobilenetwork_teamworkmindmap.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Aqinn
 * @date 2020/6/26 4:57 PM
 */
public class MyHttpUtil {

    /**
     * POST请求
     * @param address
     * @param data
     * @param listener
     */
    public static void post(final String address, final String data, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    // 向服务器发送数据
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        InputStream is = connection.getInputStream();
                        if (listener != null) {
                            listener.onFinish(inputStream2String(is));
                        }
                    } else {
                        //请求失败
                        throw new Exception("POST请求失败");
                    }
//                    // 得到服务器返回数据
//                    InputStream inputStream = connection.getInputStream();
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    if (listener != null) {
//                        listener.onFinish(response.toString());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    /**
     * GET请求
     * @param address
     * @param listener
     */
    public static void get(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        InputStream is = connection.getInputStream();
                        if (listener != null) {
                            listener.onFinish(inputStream2String(is));
                        }
                    } else {
                        //请求失败
                        throw new Exception("GET请求失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public interface HttpCallbackListener {
        void onFinish(String response);

        void onError(Exception e);
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    private static String inputStream2String(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
