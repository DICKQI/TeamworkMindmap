package com.aqinn.mobilenetwork_teamworkmindmap.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Aqinn
 * @date 2020/3/25 2:45 下午
 */
public class MyHttpGet {

    private int connectTimeout = 2000;
    private int readTimeout = 5000;
    private String host;
    private int port = 80;
    private static String DEF_CHARSET = "UTF-8";
    private String resourcePath = "/";
    private String reqLine;
    private Map<String, String> headers = new HashMap<>();

    public MyHttpGet(String uri){
        initRequestHeader();
        parseRequestLine(uri);
    }

    private void parseRequestLine(String uri) {
        String url = uri;
        if (url == null || url.length() == 0)
            throw new NullPointerException("Uri can not be null");
        if (!url.startsWith("http"))
            url = "http://" + uri;
        String[] parts = url.split("//");
        String mainPart = parts[1];
        int ipFlag = mainPart.indexOf("/");
        if (ipFlag != -1){
            String ipPort = mainPart.substring(0, ipFlag);
            String[] ipParts = ipPort.split(":");
            if (ipParts.length > 1){
                host = ipParts[0];
                String portStr = ipParts[1];
                if (portStr != null && portStr.length() > 0)
                    port = Integer.parseInt(portStr);
            }
            else {
                host = ipPort;
            }
            String resourcePart = mainPart.substring(ipFlag);
            resourcePath = resourcePart;
        } else {
            host = mainPart;
        }
        String hosrVal = host;
        if(port != 80)
            hosrVal += ":" + port;
        headers.put("Host", hosrVal);
        reqLine = "GET " + resourcePath + " HTTP/1.1\r\n";
    }

    private void initRequestHeader() {
        headers.put("Connection", "keep-alive");
        headers.put("Upgrade-Insecure-Request", "2");
        headers.put("User-Agent", "Java client");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,p_w_picpath/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "utf-8");//gzip???
        headers.put("Accept-Language", "zh-CN,zh");
        headers.put("Content-Type", "text/html;charset=utf-8");
    }

    public void setHeader(String key, String value){
        headers.put(key, value);
    }

    public RespMsg req(){
        RespMsg msg = null;
        Socket socket = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            SocketAddress endPoint = new InetSocketAddress(host, port);
            socket = new Socket();
            socket.connect(endPoint, connectTimeout);
            socket.setSoTimeout(readTimeout);
            os = socket.getOutputStream();
            write(os);

            is = socket.getInputStream();
            msg = read(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    private void write(OutputStream os) throws IOException {
        String reqBody = reqLine;
        Iterator<String> itor = headers.keySet().iterator();
        while (itor.hasNext()){
            String key = itor.next();
            String val = headers.get(key);
            String header = key + ":" + val + "\r\n";
            reqBody += header;
        }
        reqBody += "\r\n";
        System.out.println(reqBody);
        os.write(reqBody.getBytes());
    }

    private RespMsg read(InputStream is) throws IOException{
        RespMsg respMsg = new RespMsg();
        byte[] heads = HttpStreamReader.readHeaders(is);
        String headStr = new String(heads);
        String[] lines = headStr.split("\r\n");
        RespHeader resp = new RespHeader();
        if (lines.length > 0)
            resp.setRespLine(lines[0]);
        for (int i = 1; i < lines.length; i++)
            resp.addHeader(lines[i]);
        String body = null;
        if (resp.isChunked()){
            body = readChunked(is);
        } else {
            int bodyLen = resp.getContentLength();
            byte[] bodyBts = new byte[bodyLen];
            is.read(bodyBts);
            body = new String(bodyBts, DEF_CHARSET);
        }
        respMsg.setRespBody(body);
        String respLine = resp.getRespLine();
        respMsg.setRespCodeMsg(respLine);
        return respMsg;
    }

    private static String readChunked(InputStream is) throws IOException{
        String content = "";
        String lenStr = "0";
        while (!(lenStr = new String(HttpStreamReader.readLine(is))).equals("0")){
            int len = Integer.valueOf(lenStr.toUpperCase(), 16);
            byte[] cnt = new byte[len];
            is.read(cnt);
            content += new String(cnt, "UTF-8");
            is.skip(2);
        }
        return content;
    }

}

















