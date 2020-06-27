package com.xck.BTree;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode<K, C> {

    //树的度
    private final int t;

    //两个结构，一个是维护关键字，一个是维护孩子节点的指针
    private final List<K> keyList;
    private final List<C> childList;

    public BTreeNode(int t){
        this.t = t;
        this.keyList = new ArrayList<K>(2*t-1);
        this.childList = new ArrayList<C>(2*t);
    }


}
