package com.xck.leetcode.algo;

import java.util.Stack;

/**
 * 介绍：全排列和所有子集合求解
 * 算法原文地址：https://blog.csdn.net/weixin_42220532/article/details/90900815
 * 时间：2021-02-12
 */
public class FullPermutationAndAllSubSet {

    public static void main(String[] args) {
        String[] s = new String[]{"1", "2", "3", "4"};
//        new DFSStack<String>(s).work();
//        new NoStackDFA<String>(s).work();

    }

    public static class DFSStack<T>{

        private T[] list;
        private Stack<T> stack = new Stack<>();

        public DFSStack(T[] list) {
            this.list = list;
        }

        public void work(){
            if(list == null || list.length == 0) return;

            workInner(list);
        }

        private void workInner(T[] array){

            if(array.length == 1){
                stack.push(array[0]);
                System.out.println(stack);
                stack.pop();
                return;
            }

            for(int i=0; i<array.length; i++){
                T[] tmp = (T[])new Object[array.length-1];
                //每次都取走i
                System.arraycopy(array, 0, tmp, 0, i);
                System.arraycopy(array, i+1, tmp, i, array.length-i-1);
                stack.push(array[i]);
                workInner(tmp);
                stack.pop();
            }
        }
    }

    public static class NoStackDFA<T>{
        private T[] list;

        public NoStackDFA(T[] list) {
            this.list = list;
        }

        public void work(){
            if(list == null || list.length == 0) return;

            workInner(list, 0, list.length-1);
        }

        private void workInner(T[] array, int start, int end){

            if(start == end){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    sb.append(array[i]).append(",");
                }
                sb.delete(sb.length()-1, sb.length());
                System.out.println(sb.toString());
                return;
            }

            for(int i=start; i<=end; i++){
                swap(array, start, i);
                workInner(array, start+1, end);
                swap(array, start, i);
            }
        }

        public void swap(T[] array,int i,int j) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
