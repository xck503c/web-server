package com.xck.check.policy;

/**
 * @Classname SubmitReapetCheck
 * @Description TODO
 * @Date 2020/12/5 22:43
 * @Created by xck503c
 *
 * 10ms审核
 */
public class SubmitReapetCheck implements ICheck{

    private static SubmitReapetCheck check = null;
    private SubmitReapetCheck() {}

    @Override
    public boolean doCheck(Item item) {

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static SubmitReapetCheck getInstance(){
        synchronized (SubmitReapetCheck.class) {
            if(check == null){
                check = new SubmitReapetCheck();
            }
        }
        return check;
    }
}
