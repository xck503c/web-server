package com.xck.problem;

public class CommonProblem {

    public static void main(String[] args) {
        System.out.println("123456".substring(1, 5));
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
}
