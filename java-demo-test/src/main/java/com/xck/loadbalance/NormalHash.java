package com.xck.loadbalance;

import java.util.*;

public class NormalHash {

    public static void main(String[] args) {
        int nodeClient = 20;
        TreeMap<Integer, WeightCalObj> treeMap = new TreeMap<>();
        Server server = new Server();
        for(WeightCalObj s : server.list){
            treeMap.put((s.ip + "" + s.toString()).hashCode(), s);
        }
        for(long i=15720604553L; i<15720604553L+100000L; i++){
            normalHash(treeMap, i*10+""+i);
        }
        System.out.println(treeMap);
        System.out.println(server.list);
    }

    public static void normalHash(TreeMap<Integer, WeightCalObj> treeMap, String req){
//        Map.Entry<Integer, WeightCalObj> entry = treeMap.ceilingEntry(req.hashCode());
//        if (entry != null) {
//            entry.getValue().hit();
//        }else {
//            treeMap.firstEntry().getValue().hit();
//        }
        SortedMap<Integer, WeightCalObj> subMap = treeMap.tailMap(req.hashCode());
        Integer firstHash;
        if (subMap.size() > 0) {
            firstHash = subMap.firstKey();
        } else {
            firstHash = treeMap.firstKey();
        }
        treeMap.get(firstHash).hit();
    }

    public static class Server{
        public List<WeightCalObj> list = new ArrayList<WeightCalObj>(){
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
