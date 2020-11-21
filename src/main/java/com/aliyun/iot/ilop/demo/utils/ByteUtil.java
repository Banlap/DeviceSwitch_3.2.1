package com.aliyun.iot.ilop.demo.utils;

public class ByteUtil {

    /**
     * 字符串转asc
     *
     * @param s
     */
    public static byte[] stringToAsc(String s) {
        char[] chars = s.toCharArray(); // 把字符中转换为字符数组
        byte[] result = new byte[s.length()];
        for (int i = 0; i < chars.length; i++) {// 输出结果
            result[i] = (byte) chars[i];
        }
        return result;
    }

    /**
     * byte转16进制
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
