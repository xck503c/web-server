package com.xck.check.policy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname ItemCheckChain
 * @Description TODO
 * @Date 2020/12/5 22:54
 * @Created by xck503c
 *
 * 数据审核链
 */
public class ItemCheckChain {

    private Item item;
    private List<ICheck> list = new ArrayList<>();

    private int checkIndex = 0;

    public ItemCheckChain(Item item) {
        this.item = item;
    }

    public void add(ICheck check){
        if(check != null){
            list.add(check);
        }
    }

    public ICheck getChecker(){
        if(!isLast()){
            ICheck iCheck = list.get(checkIndex);
            ++checkIndex;
            return iCheck;
        }
        return null;
    }

    public boolean isLast(){
        if(checkIndex >= list.size()){
            return true;
        }
        return false;
    }

    public Item getItem() {
        return item;
    }
}
