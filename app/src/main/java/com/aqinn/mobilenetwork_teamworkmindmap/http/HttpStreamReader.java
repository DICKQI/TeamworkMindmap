package com.aqinn.mobilenetwork_teamworkmindmap.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Aqinn
 * @date 2020/3/25 5:04 下午
 */
public class HttpStreamReader {

    public static final byte[] LINE_END = {13, 10};
    public static final byte[] ALL_END = {13, 10, 13, 10};

    public static byte[] getBytes(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        int ind = 0;
        while ((len = is.read(buffer, ind, buffer.length - ind)) > 0) {
            if (len == buffer.length - ind) {
                int l = buffer.length;
                byte[] newBuffer = new byte[l * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
                ind = l;
            } else {
                ind += len;
            }
        }
        byte[] result = new byte[ind];
        System.arraycopy(buffer, 0, result, 0, ind);
        return result;
    }

    public static byte[] readHeaders(InputStream is) throws IOException {
        return read(is, ALL_END);
    }

    public static byte[] readLine(InputStream is) throws IOException {
        return read(is, LINE_END);
    }

    public static byte[] read(InputStream is, byte[] endFlag) throws IOException {
        byte[] buffer = new byte[1024];
        int ind = 0;
        int bt = 0;
        while ((bt = is.read()) != -1) {
            buffer[ind] = (byte) bt;
            if (isTailEqual(buffer, ind, endFlag))
                break;
            ind++;
        }
        int newLen = ind + 1 - endFlag.length;
        byte[] result = new byte[newLen];
        System.arraycopy(buffer, 0, result, 0, newLen);
        return result;
    }

    public static boolean isTailEqual(byte[] bts, int endIndex, byte[] ends) {
        int btsLen = endIndex + 1;
        int endLen = ends.length;
        if (btsLen < endLen) return false;

        int tailFrom = btsLen - endLen;
        int tailTo = btsLen;

        byte[] tail = Arrays.copyOfRange(bts, tailFrom, tailTo);

        for (int i = 0; i < endLen; i++)
            if (tail[i] != ends[i])
                return false;
        return true;
    }
}
