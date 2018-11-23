package com.silita.biaodaa.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

public class MD5Utils {

    public static String sign(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }

    public static String sign(String text, String charset) {
        return DigestUtils.md5Hex(getContentBytes(text, charset));
    }

    public static String sign(String text) {
        return DigestUtils.md5Hex(getContentBytes(text, "UTF-8"));
    }

    public static String sign(String text, String key, String input_charset) {
        text = text + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        }

        return false;
    }

    private static byte[] getContentBytes(String content, String charset) {
        if ((charset == null) || ("".equals(charset)))
            return content.getBytes();
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
        }
        throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
    }
}
