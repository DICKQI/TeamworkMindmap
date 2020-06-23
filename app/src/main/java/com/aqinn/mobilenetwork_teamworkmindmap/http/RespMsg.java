package com.aqinn.mobilenetwork_teamworkmindmap.http;

/**
 * @author Aqinn
 * @date 2020/3/25 3:33 下午
 */
public class RespMsg {

    private String respCodeMsg;
    private String respBody;

    public String getRespCodeMsg() {
        return respCodeMsg;
    }

    public void setRespCodeMsg(String respCodeMsg) {
        this.respCodeMsg = respCodeMsg;
    }

    public String getRespBody() {
        return respBody;
    }

    public void setRespBody(String respBody) {
        this.respBody = respBody;
    }

    @Override
    public String toString() {
        return "RespMsg{" +
                "respCodeMsg='" + respCodeMsg + '\'' +
                ", respBody='" + respBody + '\'' +
                '}';
    }
}
