package com.xck.leetcode;


import java.util.*;

/**
 * 两数之和
 */
public class TowNumAdd1 {

    public static void main(String[] args) {
//        int[] a = new int[48];
//
//        for(int i=47,j=0; i>=0; i--,j++){
//            a[j] = i;
//        }
//
//        Arrays.sort(a);
//        int[] a = new int[]{-1, 1, 1, 2, 4, 7, 7, 7, 8};
//        int[] a = new int[]{2,7,11,15};
//        int[] a = new int[]{3,2,3};
        int[] a = new int[]{3,3};
//        int[] a = new int[]{-1, -2, -3, -4, -5};

//        int[] b = twoSum(a, -8);
//        int[] b = find(a, -8);
        int[] b = find2(a, 6);
        System.out.println(b[0] + " " + b[1]);
    }


    public static int[] twoSum(int[] nums, int target) {
        int[] a = new int[nums.length];
        for(int i=0; i<nums.length; i++){
            a[i] = nums[i];
        }
        Arrays.sort(nums); //排序

        int[] towArr = new int[]{-1,-1};
        int targetIndex = binaryFind(nums, target, 0, nums.length-1);
        int target2Index = binaryFind(nums, target/2, 0, nums.length-1);
        for(int i=0; i<=target2Index; i++){
            int num = target - nums[i];
            int index = -1;
            if(nums[i] < 0){ //若是负数去targetIndex后面找
                index = binaryFind(nums, num, targetIndex, nums.length-1);
            }else{
                index = binaryFind(nums, num, i+1, target2Index);
            }
            //因为题目说一个元素不能重复用两次
            if(index >=0 && index!=i && nums[index] + nums[i] == target){
                towArr[0] = i;
                towArr[1] = index;
                break;
            }
        }

        //因为题目说一个元素不能重复用两次，所以赋值也只能一次
        boolean set1 = false;
        boolean set2 = false;
        for(int i=0; i<a.length; i++){
            if(!set1 && nums[towArr[0]] == a[i]){
                set1 = true;
                towArr[0] = i;
            }else if(!set2 && nums[towArr[1]] == a[i]){
                set2 = true;
                towArr[1] = i;
            }
            if(set1 && set2){
                break;
            }
        }

        return towArr;
    }

    //找到>=target的最小那个数字的索引
    public static int binaryFind(int[] a, int target, int left, int right){

        while(left < right){
            int mid = (left + right) / 2;
            if(a[mid] < target){
                left = mid+1;
            }else if(a[mid] > target){
                right = mid-1;
            }else{
                int i = mid;
                while(i >= left){
                    if(a[i] == target){
                        mid = i;
                    }
                    i--;
                }
                return mid;
            }
        }

        //通过右移的方式来获取>=的那个最小数值
        while (left < a.length -1 && a[left] < target){
            left++;
        }

        return left;
    }

    public static int[] find(int[] nums, int target){
        HashMap<Integer, List<Integer>> indexMap = new HashMap<>();

        //构建映射
        for(int i=0; i<nums.length; i++){
            List<Integer> indexArr = indexMap.get(nums[i]);
            if(indexArr == null){
                indexMap.put(nums[i], indexArr = new ArrayList<>());
            }
            indexArr.add(i);
        }

        int[] towArr = new int[2];
        for(Integer i : indexMap.keySet()){
            int num = target - i;
            List<Integer> indexArr = indexMap.get(num);
            if(indexArr == null) continue;
            if(num == i && indexArr.size() >= 2){
                towArr[0] = indexArr.get(0);
                towArr[1] = indexArr.get(1);
            }else{
                towArr[0] = indexMap.get(i).get(0);
                towArr[1] = indexArr.get(0);
            }
        }

        return towArr;
    }

    public static int[] find2(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            Integer index = map.get(complement);
            if (index!=null && index != i) {
                return new int[] {i, index};
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    public static int[] find3(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            Integer index = map.get(complement);
            if (index!=null && index != i) {
                return new int[] {i, index};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
