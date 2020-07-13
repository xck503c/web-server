package com.xck.leetcode;

import java.util.ArrayList;
import java.util.List;

public class LongestCommonSeq {

    public String extractTeimplete(String c1, String c2){
        int[][] supportArray = new int[c1.length()+1][c2.length()+1];
        for(int i=0; i<c1.length()+1; i++){
            for(int j=0; j<c2.length()+1; j++){
                if(i == 0 || j == 0){
                    supportArray[i][j] = 0;
                    continue;
                }

                if(c1.charAt((i-1)) == c2. charAt(j-1)){
                    supportArray[i][j] = supportArray[i-1][j-1]+1;
                }else {
                    supportArray[i][j] = supportArray[i-1][j]>=supportArray[i][j-1]?
                            supportArray[i-1][j]:supportArray[i][j-1];
                }
            }
        }

        List<String> list = new ArrayList<>();
        int maxSameLen = 0;
        boolean isNoEquals = true;
        int i=c1.length(), j=c2.length();
        while (i>0 && j>0){
            if(c1.charAt((i-1)) == c2. charAt(j-1)){
                if(isNoEquals){
                    list.add("(.*)");
                    isNoEquals = false;
                }
                list.add(String.valueOf(c1.charAt(i-1)));
                i--;
                j--;
                maxSameLen++;
            }else {
                isNoEquals = true;
                if(supportArray[i-1][j]>=supportArray[i][j-1]){
                    i--;
                }else {
                    j--;
                }
            }
        }
        if(i>0 || j>0){
            list.add("(.*)");
        }

        StringBuilder sb = new StringBuilder();
        for(int k=list.size()-1; k>=0; k--){
            sb.append(list.get(k));
        }

        int len = c1.length()>c2.length()?c2.length():c1.length();
        double s = maxSameLen/(double)len;
        System.out.println(s + " " + maxSameLen + " " + len);
        if(s >= 0.6){
            return sb.toString();
        }
        return "";
    }



    public static void main(String[] args) {
        String s1 = "【顺丰快递】你好啊，亲爱的张三先生";
        String s2 = "【圆通快递】你好啊，亲爱的徐女士";
        System.out.println(new LongestCommonSeq().extractTeimplete(s1, s2));
    }
}
