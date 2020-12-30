package com.xck.str;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncodeTest {

    static CharsetEncoder charsetEncoder = Charset.forName(CharEncoding.ISO_8859_1).newEncoder();

    private static ThreadLocal<Cipher> encrypt = new ThreadLocal<Cipher>(){
        @Override
        protected Cipher initialValue() {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return cipher;
        }
    };

    private static ThreadLocal<Cipher> decrypt = new ThreadLocal<Cipher>(){
        @Override
        protected Cipher initialValue() {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return cipher;
        }
    };

    private static String KEY_STRING = "hskj[B@26df574d##$%$3%%545HSKJ5$%%5";

    static {
        String secretStr = KEY_STRING.replaceAll("\\s", "");
        String reverseStr = StringUtils.reverse(secretStr);
        byte[] bytes = null;
        try {
            bytes = reverseStr.getBytes("iso8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] cipher = new byte[bytes.length];
        for(int i=0; i<bytes.length; i++){
            cipher[i] = (byte)~bytes[i];
        }
        String cipherSrc = null;
        try {
            cipherSrc = new String(cipher, "iso8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(cipherSrc.length()<=6){
            KEY_STRING = cipherSrc;
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append(reverseStr.substring(2, 4));
            sb.append(reverseStr.substring(5, 7));
            sb.append(reverseStr.substring(8, 10));
            sb.append(reverseStr.substring(10, 12));
            sb.append(reverseStr.substring(12, 13));
            sb.append(reverseStr.substring(14, 15));
            KEY_STRING = sb.toString();
        }
    }

    //AES/ECB/PKCS5Padding
    public static SecretKeySpec getSecretKey() throws Exception{
        KeyGenerator kg = null;
        kg = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(KEY_STRING.getBytes());
        kg.init(128, secureRandom);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static void main(String[] args) {
        encryptData();
    }

    public static void test(){
        String s1 = "sdg35436534 sags%#@!~)({}[]|\\<>?,/..,";
        String s2 = "Fdsfsd干发高烧";

        System.out.println(charsetEncoder.canEncode(s1));
        System.out.println(charsetEncoder.canEncode(s2));
    }

    public static void encryptData(){
        String s1 = "18759270155";

        try {
            byte[] byteContent = s1.getBytes("utf-8");
            Cipher cipher = encrypt.get();
            byte[] result = cipher.doFinal(byteContent);
            String s2 = Base64.encodeBase64String(result);
            System.out.println(s2);
            String s = decryptData(s2);
            System.out.println(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String decryptData(String data){
        Cipher cipher = decrypt.get();

        try {
            byte[] temp = cipher.doFinal(Base64.decodeBase64(data));
            return new String(temp, "utf-8");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
