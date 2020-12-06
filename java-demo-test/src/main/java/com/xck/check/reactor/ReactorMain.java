package com.xck.check.reactor;

import com.xck.check.policy.Item;

/**
 * @Classname ReactorMain
 * @Description TODO
 * @Date 2020/12/6 11:51
 * @Created by xck503c
 */
public class ReactorMain {

    public static void main(String[] args) throws Exception{

        CheckDispatcher dispatcher = new CheckDispatcher();
        dispatcher.start();

        long diff = 0L;
        for(int j=0; j<10; j++){
            long start = System.currentTimeMillis();
            for(int i=0; i<20000; i++){
                final boolean is = i%2==0;
                dispatcher.putEvent(assembleAndCheck(new Item(), is));
            }
            long time = (System.currentTimeMillis()-start);
            diff+=time;
            while (CheckDispatcher.atomicInteger.get() < 20000){
                Thread.sleep(1);
            }
            CheckDispatcher.atomicInteger.set(0);
            System.out.println("耗时: " + time);
        }
        System.out.println("均值: " + (diff/10)); //5600ms
    }

    private static CheckEvent assembleAndCheck(Item item, boolean isTakeUpTime){
        CheckEvent checkEvent = new CheckEvent(item);
        if (isTakeUpTime){
            checkEvent.addEventTypeChain(EventType.one);
            checkEvent.addEventTypeChain(EventType.two);
            checkEvent.addEventTypeChain(EventType.submitRepeatFilter);
            checkEvent.addEventTypeChain(EventType.none);
        }else {
            checkEvent.addEventTypeChain(EventType.one);
            checkEvent.addEventTypeChain(EventType.two);
            checkEvent.addEventTypeChain(EventType.none);
        }

        return checkEvent;
    }
}
