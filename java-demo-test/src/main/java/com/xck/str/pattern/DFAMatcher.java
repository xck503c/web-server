package com.xck.str.pattern;

/**
 *
 * 确定型有限自动机匹配关键的字符串，利用状态转移矩阵来实现
 * @author xck
 * @date 2020-01-06 23:26
 */
public class DFAMatcher {

    String patternStr = null;
    int[][] statusTransferMatrix = null;

    public DFAMatcher(String pattern) {
        patternStr = pattern;
        statusTransferMatrix = new int[pattern.length()][pattern.length()];
        for(int i = 0; i<pattern.length(); i++){
            for(int j = 0; j<pattern.length(); j++){
                if()
            }
        }
    }

    public static void main(String[] args) {
        /**
         *   x c  k 5 0 3 c
         * 0 1 -1
         * 1
         */
        DFAMatcher dfaMatcher = new DFAMatcher("xck503c");
    }
}
