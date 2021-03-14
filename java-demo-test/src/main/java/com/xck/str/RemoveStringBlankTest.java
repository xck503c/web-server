package com.xck.str;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class RemoveStringBlankTest {

    private static long half0;
    private static long half1;
    private static long half2;
    private static long half3;
    private static long half4;
    private static long half5;

    public static void main(String[] args) {

        for(int i=0; i<10; i++){
            test();
            System.out.println(i);
        }
        System.out.println(half0/10);
        System.out.println(half1/10);
        System.out.println(half2/10);
        System.out.println(half3/10);
//        System.out.println(half4/20);
//        System.out.println(half5/20);
    }

    public static void test(){
        String test = "";
        for(int i = 0; i<6; i++){
            test += (" " + 1);
        }

        int len = 400-2*test.length();

        for(long i=0; i<1000000; i++){
            String s = test+RandomStringUtils.random(len, 0x4e00, 0x9fa5, true,true)+test;
            long start = System.currentTimeMillis();
            half(s);
            half0+=System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            half1(s);
            half1+=System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            half2(s);
            half2+=System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            half3(s);
            half3+=System.currentTimeMillis() - start;
        }
    }

    public static String half(String mobile){
        return mobile.replaceAll(" ", "");
    }

    public static String half1(String content){
        StringBuilder sb = new StringBuilder();
        char[] contents = content.toCharArray();
        for(int i=0; i<contents.length; i++){
            if(" ".equals(contents[i])){
                continue;
            }else{
                sb.append(contents[i]);
            }
        }
        return sb.toString();
    }

    public static String half2(String mobile){
        return StringUtils.deleteWhitespace(mobile);
    }

    public static String half3(String content){
        boolean isresult = false;
        int count = 0;
        for(int i=0; i<content.length(); i++){
            if(' ' == content.charAt(i)){
                if(++count>12) {
                    isresult = true;
                    break;
                }
            }
        }
        if(isresult){
            return half2(content);
        }else {
            return half(content);
        }
    }
}
