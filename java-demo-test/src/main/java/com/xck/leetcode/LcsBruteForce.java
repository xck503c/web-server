package com.xck.leetcode;

public class LcsBruteForce {
    //String y = "ABCBDAB";
//    String x = "13456778";
    //String x = "BDCABA";
//    String y = "357486782";

    String x = "【顺丰快递】你好啊，亲爱的张三先生";
    String y = "【圆通快递】你好啊，亲爱的徐女士";

    boolean isOne(int m, int i) { //看整数m的第i位是不是1
        i = i - 1;
        int k = m & (1 << i);
        return k > 0;
    }

    String getSubString(int m) { //通过m构造出x的一个子串
        String s = "";
        int len = x.length();
        for (int i = 1; i <= len; i++) {
            if (isOne(m, i)) { //看m的第i位是不是1
                s = x.charAt(len - i) + s;
            }
        }
        return s;
    }

    String getLCS() {
        int len = x.length();
        int m = (1 << len) - 1;
        int maxLen = 0;
        String longest = null;
        for (int i = m; i > 0; i--) {
            //每个m代表x中的一个子串序列
            String sub = getSubString(i);
            boolean isSeq = isSubSeq(sub);
            if (isSeq) {
                if (sub.length() > maxLen) {
                    longest = sub;
                    maxLen = sub.length();
                }
            }
        }
        return longest;
    }

    boolean isSubSeq(String sub) {
        int si = 0;
        for (int i = 0; i < y.length(); i++) {
            if (sub.charAt(si) == y.charAt(i)) {
                si++;
                if (si == sub.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        LcsBruteForce lbfa = new LcsBruteForce();
        String result = lbfa.getLCS();
        System.out.println("result : " + result);
    }
}
