package com.xck.leetcode;

public class TwoNumberAdd2 {

    public static void main(String[] args) {
//        addTwoNumbers();
        System.out.println((Integer.MAX_VALUE+"").length());
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        StringBuilder sb1 = new StringBuilder(l1.val);
        while (l1.next!=null){
            l1 = l1.next;
            sb1.append(l1.val);
        }

        StringBuilder sb2 = new StringBuilder(l2.val);
        while (l2.next!=null){
            l2 = l2.next;
            sb2.append(l2.val);
        }

        sb1.reverse();
        sb2.reverse();

        int resultInt = Integer.valueOf(sb1.toString()) + Integer.valueOf(sb2.toString());

        StringBuilder sb3 = new StringBuilder(resultInt);
        sb3.reverse();

        ListNode listNode = new ListNode(sb3.charAt(0));
        ListNode root = listNode;
        for(int i=1; i<sb3.length(); i++){
            ListNode tmp = new ListNode(sb3.charAt(0));
            root.next = tmp;
            root = tmp;
        }
        root.next = null;

        return listNode;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

//    public ListNode
}
