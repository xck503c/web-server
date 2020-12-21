package com.xck.str;

public class MaskingMobileTest {

    private static long half0;
    private static long half1;
    private static long half2;
    private static long half3;
    private static long half4;
    private static long half5;

    public static void main(String[] args) {

        String mobile="15720604553";
        for(int i=0; i<mobile.length(); i++){
            System.out.println(half5(mobile.substring(i)));
        }

        for(int i=0; i<20; i++){
            test();
            System.out.println(i);
        }
        System.out.println(half0/20);
        System.out.println(half1/20);
        System.out.println(half2/20);
        System.out.println(half3/20);
        System.out.println(half4/20);
        System.out.println(half5/20);
    }

    public static void test(){
        long start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half(i+"");
        }
        half0+=System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half1(i+"");
        }
        half1+=System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half2(i+"");
        }
        half2+=System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half3(i+"");
        }
        half3+=System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half4(i+"");
        }
        half4+=System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for(long i=15720000000L; i<15720000000L+1000000; i++){
            half5(i+"");
        }
        half5+=System.currentTimeMillis() - start;
    }

    public static String half(String mobile){
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String half1(String mobile){
        StringBuilder sb = new StringBuilder();
        char[] mobiles = mobile.toCharArray();
        for(int i=0; i<mobiles.length; i++){
            if(i >= 3 && i<=7){
                sb.append("*");
            }else{
                sb.append(mobiles[i]);
            }
        }
        return sb.toString();
    }

    public static String half2(String mobile){
        StringBuilder sb = new StringBuilder();
        char[] mobiles = mobile.toCharArray();
        for(int i=0; i<mobiles.length; i++){
            sb.append(mobiles[i]);
        }
        return sb.replace(3, 7, "****").toString();
    }

    public static String half3(String mobile){
        StringBuilder sb = new StringBuilder(mobile);
        return sb.replace(3, 7, "****").toString();
    }

    public static String[] tmp = new String[]{"*", "**", "***", "****", "*****","******", "*******"};
    public static String half4(String mobile){
        int start = (int)(mobile.length()*0.3);
        int end = (int)(mobile.length()*0.7);
        if(start == end){
            return mobile;
        }
        StringBuilder sb = new StringBuilder(mobile);
        return sb.replace(start, end, tmp[end-start-1]).toString();
    }

    public static char[] tmp1 = new char[]{'*','*','*','*','*','*','*','*','*','*','*','*','*','*'};
    public static String half5(String mobile){
        int start = (int)(mobile.length()*0.3);
        int end = (int)(mobile.length()*0.7);
        if(start == end){
            return mobile;
        }
        char[] c = new char[mobile.length()];
        mobile.getChars(0, start, c, 0);
        mobile.getChars(end, mobile.length(), c, end);
        System.arraycopy(tmp1, start, c, start, end-start);
        return new String(c);
    }
}
