package com.xck.leetcode.MedianofTwoSortedArrays;

public class SolutionByBinarySearch {

    public static void main(String[] args) {
        findMedianSortedArrays(new int[]{1,3}, new int[]{2});
    }

    //1,2,3,4,5,6,7
    //5,6,7,8,9
    //
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {

        IndexArray a = new IndexArray(nums1);
        IndexArray b = new IndexArray(nums2);

        int low = -1;
        int high = -1;
        boolean lowisstop = false;
        boolean highisstop = false;
        while (!a.isStop() || !a.isStop()){
            int lowTmp = -1;
            if (!lowisstop) {
                lowTmp = IndexArray.compareLow(a, b);
                if(lowTmp == -1){
                    lowisstop = true;
                }else {
                    low = lowTmp;
                }
                System.out.println("low="+low);
            }
            int highTmp = -1;
            if (!highisstop) {
                highTmp = IndexArray.compareHigh(a, b);
                if(highTmp == -1){
                    highisstop = true;
                }else {
                    high = highTmp;
                }
                System.out.println("high="+high);
            }
        }


        System.out.println(low + " " + high);

        return 0;
    }

    public static class IndexArray{
        private int indexh;
        private int indexl;

        private int[] array;

        public IndexArray(int[] array){
            this.array = array;
            indexl = 0;
            indexh = array.length-1;
        }

        public int lowValue(){
            return array[indexl];
        }

        public int highValue(){
            return array[indexh];
        }

        public boolean incLowIndex(){
            if (indexl > indexh){
                return false;
            }
            indexl++;
            return true;
        }

        public boolean decHighIndex(){
            if (indexl >= indexh){
                return false;
            }
            indexh--;
            return true;
        }

        public boolean isStop(){
            if (indexl >= indexh){
                return true;
            }
            return false;
        }

        //1,2,3,4,5,6,7
        //5,6,7,8,9
        public static int compareLow(IndexArray a, IndexArray b){
            int low = -1;
            if(a.lowValue() >= b.lowValue()){
                low = b.lowValue();
                if(!b.incLowIndex()){
                    return -1;
                }
            }else {
                low = a.lowValue();
                if(!a.incLowIndex()){
                    return -1;
                }
            }

            return low;
        }

        public static int compareHigh(IndexArray a, IndexArray b){
            int high = -1;
            if(a.highValue() >= b.highValue()){
                high = a.highValue();
                if(!a.decHighIndex()){
                    return -1;
                }
            }else {
                high = b.highValue();
                if(!b.decHighIndex()){
                    return -1;
                }
            }

            return high;
        }
    }
}
