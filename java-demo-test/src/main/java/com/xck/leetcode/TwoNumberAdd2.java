package com.xck.leetcode;

import java.util.List;

public class TwoNumberAdd2 {

    public static void main(String[] args) {
        long a = 465416541L, b=98768478969L;
        ListNode node = addTwoNumbers1(createNode(a), createNode(b));
        printListNode(node);
        System.out.println(a+b);
    }

    /**
     * 123456+456
     * 654321和654
     * @param l1
     * @param l2
     * @return
     */
    public static ListNode addTwoNumbers1(ListNode l1, ListNode l2) {

        Result r = new Result();
        while (l1!=null && l2!=null){
            int result = l1.val+l2.val+r.val;
            addList(result, r);
            l1 = l1.next;
            l2 = l2.next;
        }

        while (l1!=null){
            int result = l1.val+r.val;
            addList(result, r);
            l1 = l1.next;
        }

        while (l2!=null){
            int result = l2.val+r.val;
            addList(result, r);
            l2 = l2.next;
        }

        if(r.val>0){
            addList(r.val, r);
        }

        return r.root;
    }

    public static void addList(int i, Result result){
        ListNode node = null;
        if (i > 9) {
            result.val = i/10; //进位
            node = new ListNode(i%10);
        }else {
            result.val = 0;
            node = new ListNode(i);
        }
        if(result.root == null){
            result.root = node;
            result.tmp = node;
        }else {
            result.tmp.next = node;
            result.tmp = node;
        }
    }

    public static ListNode createNode(List<Integer> list){

        ListNode root = null, rootTmp = null;
        for(int i=0; i<list.size(); i++){
            int tmpNum = list.get(i);
            if(root == null){
                root = new ListNode(tmpNum);
                rootTmp = root;
            }else {
                ListNode tmp = new ListNode(tmpNum);
                rootTmp.next = tmp;
                rootTmp = tmp;
            }
        }
        return root;
    }

    public static ListNode createNode(long num){
        String str = num+"";

        ListNode root = null, rootTmp = null;
        for(int i=str.length()-1; i>=0; i--){
            int tmpNum = Integer.valueOf(str.charAt(i)-'0');
            if(root == null){
                root = new ListNode(tmpNum);
                rootTmp = root;
            }else {
                ListNode tmp = new ListNode(tmpNum);
                rootTmp.next = tmp;
                rootTmp = tmp;
            }
        }
        return root;
    }

    public static void printListNode(ListNode node){

        StringBuilder sb = new StringBuilder();

        while (node!=null){
            sb.append(node.val);
            node = node.next;
        }
        sb.reverse();
        System.out.println(sb.toString());
    }

    public static class ListNode {
        int val;
        ListNode next = null;

        ListNode(int x) {
            val = x;
        }
    }

    public static class Result {
        int val;
        ListNode root = null;
        ListNode tmp = null;
    }
}
