package com.aqinn.mobilenetwork_teamworkmindmap.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/6/26 4:57 PM
 */
public class MyHttpUtil {

    /**
     * Post请求
     *
     * @param address
     * @param data
     * @param listener
     */
    public static void post(final String address, Map<String, String> header, final String data, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                InputStream isError = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    // 向服务器发送数据
                    connection.setRequestMethod("POST");
                    for (Map.Entry<String, String> h : header.entrySet()) {
                        connection.setRequestProperty(h.getKey(), h.getValue());
                    }
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    isError = connection.getErrorStream();
                    if (isError==null)
                        is = connection.getInputStream();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(is));
                        }
                    } else if (responseCode == 401) {
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(isError));
                        }
                    } else {
                        //请求失败
                        throw new Exception("POST请求失败,响应码:" + responseCode);
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
                        listener.onError(e, inputStream2String(is));
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
     * Get请求
     *
     * @param address
     * @param listener
     */
    public static void get(final String address, Map<String, String> header, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                InputStream isError = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    for (Map.Entry<String, String> h : header.entrySet()) {
                        connection.setRequestProperty(h.getKey(), h.getValue());
                    }
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    String rm = connection.getResponseMessage();
                    isError = connection.getErrorStream();
                    if (isError==null)
                        is = connection.getInputStream();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(is));
                        }
                    } else if (responseCode == 401) {
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(isError));
                        }
                    } else {
                        //请求失败
                        throw new Exception("GET请求失败,状态码:" + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e, inputStream2String(is));
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
     * Put请求
     *
     * @param address
     * @param data
     * @param listener
     */
    public static void put(final String address, Map<String, String> header, final String data, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                InputStream isError = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    for (Map.Entry<String, String> h : header.entrySet()) {
                        connection.setRequestProperty(h.getKey(), h.getValue());
                    }
                    // 向服务器发送数据
                    connection.setRequestMethod("PUT");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    isError = connection.getErrorStream();
                    if (isError==null)
                        is = connection.getInputStream();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(is));
                        }
                    } else if (responseCode == 401) {
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(isError));
                        }
                    } else {
                        //请求失败
                        Log.d("xxx", "状态码:" + responseCode);
                        throw new Exception("PUT请求失败,状态码:" + responseCode);
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
                        listener.onError(e, inputStream2String(is));
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
     * Delete请求
     *
     * @param address
     * @param listener
     */
    public static void delete(final String address, Map<String, String> header, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                InputStream is = null;
                InputStream isError = null;
                try {
                    url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    for (Map.Entry<String, String> h : header.entrySet()) {
                        connection.setRequestProperty(h.getKey(), h.getValue());
                    }
                    // 得到服务器返回数据
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    isError = connection.getErrorStream();
                    if (isError==null)
                        is = connection.getInputStream();
                    if (responseCode == 200) {
                        //请求成功 获得返回的流
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(is));
                        }
                    } else if (responseCode == 401) {
                        if (listener != null) {
                            listener.beforeFinish(connection);
                            listener.onFinish(inputStream2String(isError));
                        }
                    } else {
                        //请求失败
                        Log.d("xxx", "状态码:" + responseCode);
                        throw new Exception("DELETE请求失败,状态码:" + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e, inputStream2String(is));
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

        void beforeFinish(HttpURLConnection connection);

        void onFinish(String response);

        void onError(Exception e, String response);
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

    //读取请求头
    private static String getReqeustHeader(HttpURLConnection conn) {
        //https://github.com/square/okhttp/blob/master/okhttp-urlconnection/src/main/java/okhttp3/internal/huc/HttpURLConnectionImpl.java#L236
        Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
        Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
        StringBuilder sbRequestHeader = new StringBuilder();
        while (requestHeaderIterator.hasNext()) {
            String requestHeaderKey = requestHeaderIterator.next();
            String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
            sbRequestHeader.append(requestHeaderKey);
            sbRequestHeader.append(":");
            sbRequestHeader.append(requestHeaderValue);
            sbRequestHeader.append("\n");
        }
        return sbRequestHeader.toString();
    }

    //读取响应头
    private static String getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder sbResponseHeader = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            sbResponseHeader.append(responseHeaderKey);
            sbResponseHeader.append(":");
            sbResponseHeader.append(responseHeaderValue);
            sbResponseHeader.append("\n");
        }
        return sbResponseHeader.toString();
    }

}
