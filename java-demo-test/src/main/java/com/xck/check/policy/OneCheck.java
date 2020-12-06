package com.xck.check.policy;

/**
 * @Classname OneCheck
 * @Description TODO
 * @Date 2020/12/5 22:48
 * @Created by xck503c
 *
 * 1ms审核
 */
public class OneCheck implements ICheck{

    private static OneCheck check = null;
    private OneCheck() {}

    @Override
    public boolean doCheck(Item item) {

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static OneCheck getInstance(){
        synchronized (OneCheck.class) {
            if(check == null){
                check = new OneCheck();
            }
        }
        return check;
    }
}
