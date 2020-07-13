package com.xck.leetcode.MedianofTwoSortedArrays;

public class SolutionByBinarySearch {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<70; i++){
            sb.append("哈");
        }
        String str = sb.toString();

        long start = System.nanoTime();
        for(int i=0; i<10000; i++){
            for(int j=0; j<70; j++){
                char c = str.charAt(j);
            }
        }
        System.out.println((System.nanoTime()-start)/10000); //1046
//        char[] c1 = str.toCharArray();
//
//        long start = System.nanoTime();
//        for(int i=0; i<10000; i++){
//            for(int j=0; j<70; j++){
//                char c = c1[j];
//            }
//        }
//        System.out.println((System.nanoTime()-start)/10000); //1046
    }
}
