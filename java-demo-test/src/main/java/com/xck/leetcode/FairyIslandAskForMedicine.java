package com.xck.leetcode;

import java.util.Iterator;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

//仙岛求药：求@到*的最短路径
//分别用dfs和bfs实现

/**
 * . @ # # . . . #
 * # . . . . # . #
 * # . # . # # . .
 * . . # . # # # .
 * # . # . . . # .
 * . . # # # . # .
 * . . . # . * . .
 * . # . . . # # #
 * 找到了路径，路径的长度为栈的长度: 12, 路径为:
 * 开 下 下 下 下 下 下 右 下 右 右 上 右
 * 找到了路径，路径的长度为栈的长度: 14, 路径为:
 * 开 下 下 下 下 下 左 下 右 右 下 右 右 上 右
 * 找到了路径，路径的长度为栈的长度: 10, 路径为:
 * 开 下 右 右 下 下 下 右 右 下 下
 * 找到了路径，路径的长度为栈的长度: 16, 路径为:
 * 开 下 右 右 右 上 右 右 下 下 右 下 下 下 下 左 左
 */
public class FairyIslandAskForMedicine {

    public static void main(String[] args) {
        //无路可走
//        char[] map[] = new char[][]{
//                {'.','#','.','.','#','.'},
//                {'.','#','.','*','.','#'},
//                {'.','#','#','#','#','.'},
//                {'.','.','#','.','.','.'},
//                {'.','.','#','.','.','.'},
//                {'.','.','#','.','.','.'},
//                {'.','.','#','.','.','.'},
//                {'#','.','@','.','#','#'},
//                {'.','#','.','.','#','.'}
//        };

//        char[] mapStr = ".@##...##....#.##.#.##....#.###.#.#...#...###.#....#.*...#...###".toCharArray();
//        int m = 8, n=8;

        int m = 20, n = 20;
        char[] mapStr = ("@..................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................."
                + "...................*").toCharArray();
        int k = 0;
        char[] map[] = new char[m][n];
        for(int i=0; i<m; i++){
            for(int j=0; j<n; j++){
                map[i][j] = mapStr[k++];
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }

        long start = System.currentTimeMillis();
        dfsFind(map); //我感觉运行不出来
//        bfsFind(map); //38 用时78ms
        System.out.println("用时:" + (System.currentTimeMillis()-start));
    }

    public static int[] px = {-1, 1, 0, 0};
    public static int[] py = {0, 0, -1, 1};
    public static FairyIslandPos startPos, endPos; //李逍遥和药的位置

    public static void dfsFind(char[][] map){
        Stack<FairyIslandPos> stack = new Stack<>();
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                if(map[i][j] == '@'){
                    //因为我们不是通过控制台输入，所以要初始化起始位置
                    startPos = new FairyIslandPos(i, j, -1);
                }else if(map[i][j] == '*'){
                    endPos = new FairyIslandPos(i, j, 4);
                }
            }
        }
        stack.push(startPos); //首先将起点压栈
        dfsFindSub(map, stack);
    }

    public static void dfsFindSub(char[][] map, Stack<FairyIslandPos> stack){
        if(stack.isEmpty()){
            return;
        }

        FairyIslandPos pos = stack.peek();
        if(pos.equals(endPos)){
            System.out.printf("找到了路径，路径的长度为栈的长度: %d, 路径为:\n", stack.size()-1);
            Iterator<FairyIslandPos> it = stack.iterator();
            while (it.hasNext()){
                System.out.printf(it.next().getDir() + " ");
            }
            System.out.println();
            return;
        }

        //这里要设置为墙，防止来回走动
        map[pos.getX()][pos.getY()] = '#';

        //利用一个循环数组遍历四个方向，这样我们就可以不用记录走了哪些没有走哪些
        for(int i=0; i<4; i++){
            int newX = pos.getX()+px[i];
            int newY = pos.getY()+py[i];
            if(newX>=0 && newX<map.length && newY>=0 && newY<map[0].length
                    && map[newX][newY] != '#'){
                stack.push(new FairyIslandPos(newX, newY, i));
                dfsFindSub(map, stack);
                stack.pop(); //退回来，要弹出
            }
        }
        map[pos.getX()][pos.getY()] = '.'; //退回来之后，要置换回去
    }

    public static void bfsFind(char[][] map){
        //用于计算步数
        int[] step[] = new int[map.length][map[0].length];
        //初始化数组，便于比较长度
        for(int i=0; i<step.length; i++){
            for(int j=0; j<step[0].length; j++){
                step[i][j] = Integer.MAX_VALUE;
            }
        }

        LinkedBlockingQueue<FairyIslandPos> queue = new LinkedBlockingQueue<>(1000);

        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                if(map[i][j] == '@'){
                    //因为我们不是通过控制台输入，所以要初始化起始位置
                    startPos = new FairyIslandPos(i, j, -1);
                }else if(map[i][j] == '*'){
                    endPos = new FairyIslandPos(i, j, 4);
                }
            }
        }
        step[startPos.getX()][startPos.getY()] = 0;
        queue.offer(startPos);

        bfsFindSub(map, step, queue);
    }

    public static void bfsFindSub(char[][] map, int[][] step, LinkedBlockingQueue<FairyIslandPos> queue){
        FairyIslandPos pos = null;
        while ((pos = queue.poll()) != null) {
            map[pos.getX()][pos.getY()] = '#';


            int stepValue = step[pos.getX()][pos.getY()]+1;
            for(int i=0; i<4; i++){
                int newX = pos.getX()+px[i], newY = pos.getY()+py[i];
                if(newX>=0 && newX<map.length && newY>=0 && newY<map[0].length
                        && map[newX][newY] != '#'){
                    step[newX][newY] = stepValue < step[newX][newY] ? stepValue : step[newX][newY];
                    queue.offer(new FairyIslandPos(newX, newY, i));
                }
            }
        }

        System.out.println(step[endPos.getX()][endPos.getY()]);
    }

    //栈帧，标识位置和方向
    public static class FairyIslandPos{
        private int x;
        private int y;
        private char dir; //方向

        public FairyIslandPos(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            switch (dir){
                case -1: this.dir = '开'; break;
                case 0: this.dir = '上'; break;
                case 1: this.dir = '下'; break;
                case 2: this.dir = '左'; break;
                case 3: this.dir = '右'; break;
                default:
                    this.dir = '终';
            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public char getDir() {
            return dir;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof FairyIslandPos){
                FairyIslandPos pos = (FairyIslandPos)obj;
                return pos.getX()==getX() && pos.getY()==getY();
            }

            return false;
        }
    }
}
