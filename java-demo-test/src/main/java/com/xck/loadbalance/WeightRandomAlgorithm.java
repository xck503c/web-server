package com.xck.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 功能：测试加权随机的两种实现方案
 * 时间：2020-11-01 22:15
 */
public class WeightRandomAlgorithm {

    public static void main(String[] args) {
        Server server = new Server();
        server.list.add(new WeightCalObj("a",2));
        server.list.add(new WeightCalObj("b", 1));
        server.list.add(new WeightCalObj("c", 4));
        server.list.add(new WeightCalObj("d", 3));

        int allWeight = 0;
        for(int j=0; j<server.list.size(); j++){
            allWeight+=server.list.get(j).weight;
        }
        System.out.println("weight: " + allWeight);
        for(int i=0; i<3000000; i++){
            System.out.println(); //若没有这行循环速度太快了，导致纳米级别的也不能均匀随机出随机数
            randomWeight(server, allWeight);
        }
        System.out.println(server.list);
    }

    public static void randomWeight(Server server, int allWeight){
        int random = (int)(System.nanoTime()%allWeight+1);
//        Random r = new Random();
//        int random = r.nextInt(allWeight)+1;
        for(int j=0; j<server.list.size(); j++){
            if(random <= server.list.get(j).weight){
                server.list.get(j).hit();
                return;
            }
            random -= server.list.get(j).weight;
        }
//        if(random<=2){
//            server.list.get(0).hit();
//        }else if(random<=3){
//            server.list.get(1).hit();
//        }else if(random<=7){
//            server.list.get(2).hit();
//        }else if(random<=10){
//            server.list.get(3).hit();
//        }
    }

    public static void randomWeightaddList(){
        Server server = new Server();
        List<WeightCalObj> list = new ArrayList<>();
        for(WeightCalObj o : server.list){
            int weight = o.weight;
            for(int i=0; i<weight; i++){
                list.add(o);
            }
        }
        for(int i=0; i<1000000; i++){
            int random = (int)(System.nanoTime()%list.size());
            list.get(random).hit();
        }
        System.out.println(server.list);
    }

    public static class Server{
        public List<WeightCalObj> list = new ArrayList<WeightCalObj>()
//        {
//            {
//                add(new WeightCalObj("a",2));
//                add(new WeightCalObj("b", 1));
//                add(new WeightCalObj("c", 4));
//                add(new WeightCalObj("d", 3));
//            }
//        }
        ;
    }

    public static class WeightCalObj{
        public String ip;
        public int weight;
        public int cal;

        public WeightCalObj(String ip, int weight){
            this.ip = ip;
            this.weight = weight;
        }

        public void hit(){
            cal++;
        }

        @Override
        public String toString() {
            return "{" + ip + "-" + cal + "-" + weight +"}";
        }
    }
}
