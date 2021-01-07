package com.xck.str.pattern;

/**
 *
 * 确定型有限自动机匹配关键的字符串：
 * 2020-01-06 23:26 利用状态转移矩阵来实现(没什么思路)；
 * 2020-01-07 11:28 改为节点递推实现关键字的搜索
 * @author xck
 * @date 2020-01-06 23:26
 */
public class DFAMatcher {

    private String patternStr = null;
    private final Node errorNode = new Node(-2, null);
    private final Node finNode = new Node(-1, null);
    private Node startNode = null;

    /**
     * 构造DFA链
     * @param pattern
     */
    public DFAMatcher(String pattern) {
        patternStr = pattern;
        startNode = new Node(0, patternStr.charAt(0)); //初始状态节点
        Node prev = startNode;
        for(int i=1; i<patternStr.length(); i++){
            char c = patternStr.charAt(i);
            Node newNode = new Node(i, c);
            prev.setNextNode(newNode);
            prev = newNode;
        }
        prev.setNextNode(finNode); //链接到最终状态
    }

    public boolean find(String content){
        Node curNode = startNode;
        Node nextNode;
        for(int i=0; i<content.length();){
            char c = content.charAt(i);
            nextNode = curNode.isMatch(c); //一一判断
            if(finNode.equals(nextNode)){ //已经识别完成
                return true;
            }else if(errorNode.equals(nextNode)){ //不能识别
                if(startNode.equals(curNode)){ //处于初始状态
                    i++;
                }else{
                    curNode = startNode; //处于中间状态
                }
            }else{
                curNode = nextNode; //识别
                i++;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        DFAMatcher dfaMatcher = new DFAMatcher("xck503c");
        System.out.println(dfaMatcher.find("hello xckxck503c, i am roobot!!!"));
        dfaMatcher = new DFAMatcher("xck.*503c");
        System.out.println(dfaMatcher.find("xck503c503c503c503c"));;
    }

    /**
     * DFA节点，节点包含当前状态和可以识别的字符已经下一个节点
     */
    private class Node{
        private Integer status;
        private Character c;
        private Node nextNode = null;

        public Node(Integer status, Character c) {
            this.status = status;
            this.c = c;
        }

        public Node isMatch(Character c){
            if(this.c.charValue() == c){
                return nextNode;
            }
            return errorNode;
        }

        public void setNextNode(Node nextNode) {
            this.nextNode = nextNode;
        }
    }
}
