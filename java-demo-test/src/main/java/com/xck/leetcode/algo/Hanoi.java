package com.xck.leetcode.algo;

/**
 * 介绍：汉诺塔
 * 时间：2021-02-15
 */
public class Hanoi {

    public static void main(String[] args) {
        hanoi(4, 'X', 'Y', 'Z');
    }

    public static void hanoi(int n,char from,char tmp,char to){
        if (n>0) {
            hanoi(n - 1, from, to, tmp);
            System.out.println("take " + n + " from " + from + " to " + to);
            hanoi(n - 1, tmp, from, to);
        }
    }
}
