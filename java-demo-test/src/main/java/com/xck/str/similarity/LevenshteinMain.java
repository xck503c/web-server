//package com.xck.str.similarity;
//
//import org.apache.commons.collections4.SetUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class LevenshteinMain {
//
//    public static void main(String[] args) {
//        /**
//         * 0.6296296
//         * 0.64705884
//         * 0.77272725
//         * 0.7800135
//         */
////        String a = "【小么么么范德萨发的放的放快递】验证码为****，请在**分钟内使用";
////        String b = "【南通快递】验证码为****，请在**分钟内使用";
//        /**
//         * 0.6666667
//         * 0.84615386
//         * 0.8
//         * 0.8
//         */
//        String a = "【古灵精怪快递】尊敬的xxxx女士";
//        String b = "【快递】尊敬的xxxx先生";
//        System.out.println(jaccard(a, b));
//        System.out.println(Levenshtein(a, b));
//        System.out.println(SorensenDice(a, b));
//        System.out.println(cos(a, b));
//    }
//
//    public static float jaccard(String a, String b) {
//        if (a == null && b == null) {
//            return 1f;
//        }
//        // 都为空相似度为 1
//        if (a == null || b == null) {
//            return 0f;
//        }
//        Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
//        Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());
//        // 交集数量
//        int intersection = SetUtils.intersection(aChar, bChar).size();
//        if (intersection == 0) return 0;
//        // 并集数量
//        int union = SetUtils.union(aChar, bChar).size();
//        return ((float) intersection) / (float)union;
//    }
//
//    public static float Levenshtein(String a, String b) {
//        if (a == null && b == null) {
//            return 1f;
//        }
//        if (a == null || b == null) {
//            return 0F;
//        }
//        int editDistance = editDis(a, b);
//        return 1 - ((float) editDistance / Math.max(a.length(), b.length()));
//    }
//
//    private static int editDis(String a, String b) {
//
//        int aLen = a.length();
//        int bLen = b.length();
//
//        if (aLen == 0) return aLen;
//        if (bLen == 0) return bLen;
//
//        int[][] v = new int[aLen + 1][bLen + 1];
//        for (int i = 0; i <= aLen; ++i) {
//            for (int j = 0; j <= bLen; ++j) {
//                if (i == 0) {
//                    v[i][j] = j;
//                } else if (j == 0) {
//                    v[i][j] = i;
//                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
//                    v[i][j] = v[i - 1][j - 1];
//                } else {
//                    v[i][j] = 1 + Math.min(v[i - 1][j - 1], Math.min(v[i][j - 1], v[i - 1][j]));
//                }
//            }
//        }
//        return v[aLen][bLen];
//    }
//
//    public static float SorensenDice(String a, String b) {
//        if (a == null && b == null) {
//            return 1f;
//        }
//        if (a == null || b == null) {
//            return 0F;
//        }
//        Set<Integer> aChars = a.chars().boxed().collect(Collectors.toSet());
//        Set<Integer> bChars = b.chars().boxed().collect(Collectors.toSet());
//        // 求交集数量
//        int intersect = SetUtils.intersection(aChars, bChars).size();
//        if (intersect == 0) {
//            return 0F;
//        }
//        // 全集，两个集合直接加起来
//        int aSize = aChars.size();
//        int bSize = bChars.size();
//        return (2 * (float) intersect) / ((float) (aSize + bSize));
//    }
//
//    public static float cos(String a, String b) {
//        if (a == null || b == null) {
//            return 0F;
//        }
//        Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
//        Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());
//
//        // 统计字频
//        Map<Integer, Integer> aMap = new HashMap<>();
//        Map<Integer, Integer> bMap = new HashMap<>();
//        for (Integer a1 : aChar) {
//            aMap.put(a1, aMap.getOrDefault(a1, 0) + 1);
//        }
//        for (Integer b1 : bChar) {
//            bMap.put(b1, bMap.getOrDefault(b1, 0) + 1);
//        }
//
//        // 向量化
//        Set<Integer> union = SetUtils.union(aChar, bChar);
//        int[] aVec = new int[union.size()];
//        int[] bVec = new int[union.size()];
//        List<Integer> collect = new ArrayList<>(union);
//        for (int i = 0; i < collect.size(); i++) {
//            aVec[i] = aMap.getOrDefault(collect.get(i), 0);
//            bVec[i] = bMap.getOrDefault(collect.get(i), 0);
//        }
//
//        // 分别计算三个参数
//        int p1 = 0;
//        for (int i = 0; i < aVec.length; i++) {
//            p1 += (aVec[i] * bVec[i]);
//        }
//
//        float p2 = 0f;
//        for (int i : aVec) {
//            p2 += (i * i);
//        }
//        p2 = (float) Math.sqrt(p2);
//
//        float p3 = 0f;
//        for (int i : bVec) {
//            p3 += (i * i);
//        }
//        p3 = (float) Math.sqrt(p3);
//
//        return ((float) p1) / (p2 * p3);
//    }
//}
