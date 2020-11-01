package com.xck.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 功能：测试完全随机
 * 时间：2020-11-01 22:15
 */
public class RandomAlogrithm {

    public static void main(String[] args) {
        fullRandom();
    }

    public static void fullRandom(){
        Server server = new Server();

        for(int i=0; i<3000; i++){
            CalObj calObj = server.list.get((int)(System.nanoTime()%server.list.size()));
            calObj.hit();
        }

        //[{a-1006}, {b-974}, {c-1020}]
        System.out.println(server.list);
    }

    public static class Server{
        public List<CalObj> list = new ArrayList<CalObj>(){
            {
                add(new CalObj("a"));
                add(new CalObj("b"));
                add(new CalObj("c"));
            }
        };
    }

    public static class CalObj{
        public String ip;
        public int cal;

        public CalObj(String ip){
            this.ip = ip;
        }

        public void hit(){
            cal++;
        }

        @Override
        public String toString() {
            return "{" + ip + "-" + cal+"}";
        }
    }
}
