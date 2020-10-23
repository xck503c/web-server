package com.xck.problem;

import java.util.*;

public class CommonProblem {

    public static void main(String[] args) {
//        System.out.println("123456".substring(1, 5));

        Map<String, HashSet<Integer>> map1 = new HashMap<>();
        Map<String, List<Integer>> map2 = new HashMap<>();
        random(map1);
        random(map2);
    }

    public static void circle(){
        int j = 0;
        for(int i=0; i<10000;){
            i++;
            if(i%10 == 0){
                j++;
            }
        }

        for(int i=0; i<10000;){
            ++i;
            if(i%10 == 0){
                ++j;
            }
        }

        System.out.println(j);
    }

    public static void incAndDecAndSlot(String[] args){
        System.out.println(args[0]);

        int i = 0;
        printInt(++i);
        printInt(i++);
        i = i++;
        printInt(i);
        i = ++i;
        printInt(i);
//        int b=1,c=2;
//        int a = b+c;
//        printInt(a);
    }

    public static void printInt(int i){
        System.out.println(i);
    }

    public static <T> T random(Map<String, ? extends Collection<T>> c){

        Collection<T> collections = (Collection<T>)c.values();
        Iterator<Collection<T>> iterator = (Iterator<Collection<T>>)collections.iterator();
        while (iterator.hasNext()){
            System.out.println("ff");
        }
        System.out.println("ff");

        return null;
    }
}
