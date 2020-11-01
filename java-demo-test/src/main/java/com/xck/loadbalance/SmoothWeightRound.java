package com.xck.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 功能：测试平滑加权随机的实现方案，我发现这种方案出来的数据非常非常的均匀
 * 时间：2020-11-01 22:15
 */
public class SmoothWeightRound {

    public static void main(String[] args) {
        Server server = new Server();

        for(int i=0; i<1000000; i++){
            round(server);
        }
        //[{a-200000-2}, {b-100000-1}, {c-400000-4}, {d-300000-3}]
        System.out.println(server.list);
    }

    public static void round(Server server){
        int allWeight = 0;
        TreeMap<Integer, WeightCalObj> treeMap = new TreeMap<>();
        for(WeightCalObj o : server.list){
            o.curWeight += o.weight;
            treeMap.put(o.curWeight, o);
            allWeight += o.curWeight;
        }

        Map.Entry<Integer, WeightCalObj> entry = treeMap.lastEntry();
        entry.getValue().hit();

        entry.getValue().curWeight -= allWeight;

    }

    public static class Server{
        public List<WeightCalObj> list = new ArrayList<WeightCalObj>()
        {
            {
                add(new WeightCalObj("a",2));
                add(new WeightCalObj("b", 1));
                add(new WeightCalObj("c", 4));
                add(new WeightCalObj("d", 3));
            }
        };
    }

    public static class WeightCalObj{
        public String ip;
        public int weight;
        public int curWeight;
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
