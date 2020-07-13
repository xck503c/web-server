package com.xck.leetcode;

public class Dungeon147 {
    public static void main(String[] args) {

        //7
//        int[] a[] = new int[][]{
//            {-2, -3, 3},
//            {-5, -10, 1},
//            {10, 30, -5}
//        };
        //3
        int[] a[] = new int[][]{
                {1, -3, 3},
                {0, -2, 0},
                {-3, -3, -3}
        };
        //1
//        int[] a[] = new int[][]{
//                {1, 2, 1},
//                {-2, -3, -3},
//                {3, 2, -2}
//        };

        System.out.println(calculateMinimumHP(a));
    }

    public static int calculateMinimumHP(int[][] dungeon) {
        Knight[][] knights = new Knight[dungeon.length][dungeon[0].length];

        knights[0][0] = new Knight(dungeon[0][0]);
        for(int i=1; i<dungeon.length; i++){
            knights[i][0] = knights[i-1][0].explore(dungeon[i][0]);
        }
        for(int i=1; i<dungeon[0].length; i++){
            knights[0][i] = knights[0][i-1].explore(dungeon[0][i]);
        }

        for(int i=1; i<dungeon.length; i++){
            for(int j=1; j<dungeon[0].length; j++){
                boolean isEnd = i==dungeon.length-1 && j==dungeon[0].length-1;
                knights[i][j] = minHealth(knights[i-1][j]
                        , knights[i][j-1], dungeon[i][j], isEnd);
            }
        }

        return knights[dungeon.length-1][dungeon[0].length-1].getHealth()+1;
    }

    public static Knight minHealth(Knight knight1, Knight knight2, int unknown, boolean isEnd){
        Knight k1 = knight1.explore(unknown);
        Knight k2 = knight2.explore(unknown);

//        if(k1.getHealth() < k2.getHealth()){
//            return k1;
//        }
//
//        if(k1.getHealth() == k2.getHealth()){
//            if(k1.getHealthSlave() > k2.getHealthSlave()){
//                return k1;
//            }
//        }

//        if (isEnd){
//            if(k1.getHealth() < k2.getHealth()) return k1;
//            else return k2;
//        }
//
//        if((k1.getHealth()-k1.getHealthSlave()) < (k2.getHealth()-k2.getHealthSlave())){
//            return k1;
//        }
//
//        if((k1.getHealth()-k1.getHealthSlave()) == (k2.getHealth()-k2.getHealthSlave())){
//            if(k1.getHealth() < k2.getHealth()){
//                return k1;
//            }
//
//            if(k1.getHealth() == k2.getHealth()){
//                if(k1.getHealthSlave() > k2.getHealthSlave()){
//                    return k1;
//                }
//            }
//        }

        return k2;
    }

    public static class Knight{
        private int health = 0;
        private int healthSlave = 0;

        public Knight(int health, int healthSlave) {
            this.health = health;
            this.healthSlave = healthSlave;
        }

        public Knight(int unknown){
            exploreSub(unknown);
        }

        public Knight explore(int unknown){
            Knight knight = new Knight(health, healthSlave);
            knight.exploreSub(unknown);
            return knight;
        }

        private void exploreSub(int unknown){
            if(unknown > 0){ //血瓶，可以加血
                healthSlave+=unknown;
            }else if(unknown < 0){ //战斗
                int remainDamage = unknown;
                if(healthSlave > 0){
                    remainDamage += healthSlave;
                }
                if (remainDamage < 0) {
                    health-=remainDamage;
                    healthSlave = 0;
                }else {
                    healthSlave = remainDamage;
                }
            }
        }

        public int getHealth() {
            return health;
        }

        public int getHealthSlave() {
            return healthSlave;
        }
    }
}
