package com.xck.sms.util;

import java.security.MessageDigest;

public class StringUtils {

    public static String md5Convert(String s) {
        char[] hexChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] bytes = s.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;

            for(int i = 0; i < bytes.length; ++i) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 15];
                chars[k++] = hexChars[b & 15];
            }

            return new String(chars);
        } catch (Exception var9) {
            return null;
        }
    }

    public static String md5ConvertByte(String src, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes(charset));
            return new String(md.digest(), charset);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static String readFromEndFlag(byte[] body, int start, int maxLen, int endFlag, String charset) throws Exception{
        byte[] b = new byte[maxLen];
        int i=start;
        for(; i<start+maxLen; i++){
            b[i-start] = body[i];
            if(b[i-start] == endFlag){
                i++;
                break;
            }
        }
        byte[] newByte = new byte[i-start];
        System.arraycopy(b, 0, newByte, 0, newByte.length);
        if (charset==null) {
            return new String(newByte).trim();
        } else {
            return new String(newByte, charset).trim();
        }
    }
}
