package com.jasper.lang;

public class StrUtil {
    private static final char[] HEX_ARRAY;

    static {
        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    }

    /**
     * 字符串去掉空格
     *
     * @param str string
     * @return 去掉空格的字符串
     */
    public static String stringTrim(String str) {
        if (str == null) {
            return null;
        } else {
            return str.trim();
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return true: 为空 false: 不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || stringTrim(str).isEmpty();
    }

    /**
     * Convert byte array to hex string.
     *
     * @param src byte array
     * @return hex string
     */
    public static String bytes2string(byte[] src) {
        //        数组是字节数组长度的俩倍。 一个字节(8个位)   每个字节会被转换成两个十六进制字符   1010 1111 -> AF
        char[] hexChars = new char[src.length * 2];
        for (int j = 0; j < src.length; j++) {
            //            0xff = 1111 1111 = 255  将byte类型的值转换为无符号整数0-255
            int v = src[j] & 0xFF;
            //            v = 0xAB = 10101011
            //            v >>> 4 = 00001010 = 0x0A = 10 无符号右移 保留高四位
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            //            v = 0xAB = 10101011
            //            v & 0x0F = 00001011 = 0x0B = 11  &0x0f 取低四位
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
