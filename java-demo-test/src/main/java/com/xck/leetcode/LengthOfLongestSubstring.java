package com.xck.leetcode;

public class LengthOfLongestSubstring {

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("au"));
    }

    public static int lengthOfLongestSubstring(String s) {

        if(s.length() == 0){
            return 0;
        }

        int maxLen = 1;

        int start=0,end=1;
        int[] charLocations = new int[128];

        int index, location, len;
        for(int i=0; i<s.length(); i++){
            end = i;
            index = (int)s.charAt(i);
            location = charLocations[index];
            if(location >= 0 && start <= location) { //说明重复
                start = location+1;
            }
            charLocations[index] = i;

            len = end-start+1;
            maxLen = maxLen < len ? len : maxLen;
        }

        return maxLen;
    }
}
