package com.xck;

import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class InsertSubmitThread extends Thread{

    private volatile boolean isRunning = false;
    private final static BlockingQueue<Runnable> insertWorkQueue = new ArrayBlockingQueue<Runnable>(100);
    private final static ExecutorService insertPool = new ThreadPoolExecutor(SysConstants.CPUS
            , SysConstants.CPUS, 60, TimeUnit.SECONDS, insertWorkQueue, new ThreadPoolExecutor.CallerRunsPolicy());

    public static AtomicLong mobileSed = new AtomicLong(15700000000L);
    public volatile static long maxSn = 0L;
    public AtomicBoolean setMaxSn = new AtomicBoolean(true);

    public static AtomicLong insertNum = new AtomicLong();

    public InsertSubmitThread(){
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning){
            if (insertNum.get() <= 100000) {
                if(!insertPool.isShutdown()){
                    insertPool.submit(new InsertTask());
                }
            }else {
                insertPool.shutdown();
                break;
            }
        }
        try {
            while(!insertPool.awaitTermination(3, TimeUnit.SECONDS)){
                System.out.println("await");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(insertNum.get());
        System.out.println(maxSn);
    }

    public long insert(int count){
        String sql = "INSERT INTO `submit_message_send_history`(" +
                "`submit_sn`, `user_sn`, `user_id`, `service_code`, `ext_code`" +
                ", `user_ext_code`, `td_code`, `sp_number`, `filter_flag`, `mobile`" +
                ", `msg_content`, `msg_id`, `insert_time`, `update_time`, `status`" +
                ", `response`, `fail_desc`, `tmp_msg_id`, `pknumber`, `pktotal`" +
                ", `sub_msg_id`, `price`, `country_cn`, `ori_mobile`, `charge_count`" +
                ", `msg_format`, `err`, `dest_flag`, `msg_receive_time`, `msg_deal_time`" +
                ", `msg_scan_time`, `msg_send_time`, `msg_report_time`, `check_user`, `cache_sn`" +
                ", `complete_content`, `type`) VALUES (" +
                "0, 1, 'xck', '1216541205', '43424'" +
                ", '241234', '165464154', '514651465', 0, ?" +
                ", '范德萨发了但是反垄断是范德萨的肾功能戛纳', ?, now(), now(), 0" +
                ", 0, 'DELIVER', 'sdfgfgs', 0, 1" +
                ", 123456, 0.01000, '中国', ?, 1" +
                ", 0, '0', '1', ?, ?" +
                ", ?, ?, ?, 'xck', 0" +
                ",'的树干上官方官方的；好地方看后面见风使舵；dmjgfdgjfdkgnd', 0)";
        Connection conn = null;
        PreparedStatement ps = null;
        long maxSnCopy = maxSn;
        try {
            conn = DataSourceUtil.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            long mobile = mobileSed.getAndAdd(count);
            String time = currentTimeToMS();
            for(int i=0; i<count; i++){
                ps.setString(1, (mobile)+"");
                ps.setString(2, UUID.randomUUID().toString());
                ps.setString(3, (mobile++)+"");
                ps.setString(4, time);
                ps.setString(5, time);
                ps.setString(6, time);
                ps.setString(7, time);
                ps.setString(8, time);
                ps.addBatch();
            }
            ps.executeBatch();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()){
                long snTmp = rs.getLong(1);
                maxSnCopy = maxSnCopy<snTmp?snTmp:maxSnCopy;
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
        }
        return maxSnCopy;
    }

    public static String currentTimeToMS(){
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
    }

    public class InsertTask implements Runnable{

        public void run() {
            boolean isAdd = false;
            try {
                long sn = insert(1000);
                insertNum.getAndAdd(1000);
                while (!setMaxSn.compareAndSet(true, false)){}
                maxSn = maxSn>sn?maxSn:sn;
                System.out.println(Thread.currentThread().getName()+" 插入" + insertNum.get() + ", 最大sn: " + maxSn);
                setMaxSn.set(true);
                isAdd = true;
            } finally {
                if(isAdd){
                    insertWorkQueue.offer(this);
                }
            }
        }
    }

    public static long getMaxSn(){
        return maxSn;
    }
}
