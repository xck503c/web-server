package com.xck.check.policy;

/**
 * @Classname OneCheck
 * @Description TODO
 * @Date 2020/12/5 22:48
 * @Created by xck503c
 *
 * 2ms审核
 */
public class TwoCheck implements ICheck{

    private static TwoCheck check = null;
    private TwoCheck() {}

    @Override
    public boolean doCheck(Item item) {

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static TwoCheck getInstance(){
        synchronized (TwoCheck.class) {
            if(check == null){
                check = new TwoCheck();
            }
        }
        return check;
    }
}
