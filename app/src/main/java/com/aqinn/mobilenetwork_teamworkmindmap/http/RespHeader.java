package com.aqinn.mobilenetwork_teamworkmindmap.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Aqinn
 * @date 2020/3/25 5:32 下午
 */
public class RespHeader {

    private String respLine;
    private Map<String, String> headers = new HashMap<>();

    public void addHeader(String head){
        if (head == null || head.length() < 1)
            return;
        String[] nameVal = head.split(":");
        String name = nameVal[0], val = null;
        if (nameVal.length > 1)
            val = nameVal[1].replaceFirst(" ", "");
        headers.put(name, val);
    }

    public String getHeader(String header){
        return headers.get(header);
    }

    public int getContentLength(){
        int len = 0;
        String lenStr = headers.get("Content-Length");
        if (lenStr == null || lenStr.length() < 1)
            return len;
        try {
            len = Integer.parseInt(lenStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return len;
    }

    public String getSessionId(){
        String sessionId = null;
        String val = headers.get("Set-Cookie");
        if (val != null && val.length() > 0){
            String[] array=  val.split(";");
            String sessStr = array[0];
            if (sessStr != null && sessStr.length() > 1){
                array = sessStr.split("=");
                if (array.length > 1){
                    sessionId = array[1];
                }
            }
        }
        return sessionId;
    }

    public boolean isChunked(){
        String val = headers.get("Transfer-Encoding");
        if (val == null || val.length() < 1)
            return false;
        return "chunked".equals(val);
    }

    public String getRespLine(){
        return respLine;
    }

    public void setRespLine(String respLine){
        this.respLine = respLine;
    }

    public Map<String,  String> getHeaders(){
        return headers;
    }

    public void setHeaders(Map<String, String> headers){
        this.headers = headers;
    }

    @Override
    public String toString() {
        String str = this.respLine + "\r\n";
        Set<String> keys = this.headers.keySet();
        for (String key : keys){
            str += (key + ":" + this.headers.get(key) + "\r\n");
        }
        return str;
    }
}
