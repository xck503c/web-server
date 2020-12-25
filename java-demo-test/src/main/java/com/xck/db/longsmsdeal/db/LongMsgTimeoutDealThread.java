//package com.xck.db.longsmsdeal.db;
//
//import org.apache.log4j.Logger;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.Map;
//
//@Scope("prototype")
//@Service
//public class LongMsgTimeoutDealThread extends Thread {
//    public static Logger infoLog = Logger.getLogger("info");
//    public static Logger errLog = Logger.getLogger("ERROR");
//
//    private volatile boolean isRunnable = true;
//
//    private int redisKeyTimeout = 605;
//    private long timeoutInterval = 86400;
//
//    @Resource
//    private SpliceDAO spliceDAO;
//
//    private int i;
//    private long time;
//    private int times = 0;
//
//    public void run(){
//        infoLog.info("LongMsgTimeoutDealThread start");
//        while (isRunnable){
//            try{
//                long start = System.currentTimeMillis();
//                Map<String, Object> resultMap = spliceDAO.dealTimeout1();
//                time += (System.currentTimeMillis() - start);
//                times++;
//                infoLog.info("整轮耗时: " + (System.currentTimeMillis() - start));
//                if(resultMap!=null && resultMap.size() > 0){
//
//                }
//            }catch (Exception e){
//                errLog.error("LongMsgTimeoutDealThread fail", e);
//                try{
//                    sleep(1000);
//                }catch (Exception e1){
//                }
//            }
//        }
//    }
//}
