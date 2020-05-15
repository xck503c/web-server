package com.xck;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolTransferThread extends Thread {
    private MissionConfig mission;

    private static BlockingQueue<Runnable> transferWorkQueue = new ArrayBlockingQueue<Runnable>(200);
    private static ExecutorService transferPool = new ThreadPoolExecutor(SysConstants.CPUS
            , SysConstants.CPUS + 2, 60, TimeUnit.SECONDS, transferWorkQueue, new ThreadPoolExecutor.CallerRunsPolicy());

    //用于获取当前正在运行的任务，锁定的区间最小值
    private BlockingQueue<Long> taskMinPrimaryKeyQueue = new ArrayBlockingQueue<Long>(50);

    //当前表中的主键最大最小值
    private volatile long searchMinPrimaryKey = 0L;

    //偏移量标识，标识锁定过的主键最大值
    private volatile long currentAllocateMaxPrimary = 0L;

    public static Lock updatePrimaryLock = new ReentrantLock();
    public volatile static boolean isSubmitTask = true;

    private List<ThreadPoolTransferTask> emptyTaskList = new ArrayList<ThreadPoolTransferTask>(1);

    private long idleCheck = 3000;

    private String getMinPrimaryKeySQL;

    public ThreadPoolTransferThread(MissionConfig mission) {
        this.mission = mission;
        this.getMinPrimaryKeySQL = "select " + mission + " from " + mission.getSourceTable()
                + " order by " + mission.getPrimaryKey() + " limit 1";
        idleCheck = Long.parseLong(mission.getIdealCheck());
    }

    @Override
    public void run() {
        setName("ThreadPoolTransferThread-" + mission.getSourceTable());
        System.out.println(getName() + " start");
        try {
            while (mission.isRunnable()) {
                produceTask();
            }
        } catch (InterruptedException e) {
        } finally {
            System.out.println(getName() + " end");
        }
    }

    public void produceTask() throws InterruptedException{
        List<TransferTaskInfo> taskInfos = selectTask(currentAllocateMaxPrimary);
        if(taskInfos.isEmpty()){
            //可能没有数据了
            searchMinPrimaryKey = executePrimaryKeyQuery(getMinPrimaryKeySQL);
            if(searchMinPrimaryKey <= 0){
                Thread.sleep(idleCheck);
                return;
            }

            currentAllocateMaxPrimary = searchMinPrimaryKey;
            Thread.sleep(100);
            return;
        }

        Long curPoolLockMinPri = getCurPoolLockMinPri();
        curPoolLockMinPri = curPoolLockMinPri == null ? -1 : curPoolLockMinPri;

        if(isMaxTaskLockSn(taskInfos.get(taskInfos.size()-1).getSn()
                , curPoolLockMinPri)){
            Thread.sleep(100);
            return;
        }

        currentAllocateMaxPrimary = taskInfos.get(taskInfos.size()-1).getSn()+1;
        if(!transferPool.isShutdown()){
            taskMinPrimaryKeyQueue.put(taskInfos.get(0).getSn());
            transferPool.submit(new ThreadPoolTransferTask(taskInfos));
        }else {
            return;
        }

        int remainTakNum = transferWorkQueue.remainingCapacity();
        int i = 0;
        if(remainTakNum > 0){
            while (i++ < remainTakNum){
                List<TransferTaskInfo> tmp = selectTask(currentAllocateMaxPrimary);
                if(tmp.isEmpty()) break;
                if(isMaxTaskLockSn(tmp.get(tmp.size()-1).getSn()
                        , curPoolLockMinPri)){
                    Thread.sleep(100);
                    return;
                }
                currentAllocateMaxPrimary = tmp.get(tmp.size()-1).getSn()+1;
                if(!transferPool.isShutdown()){
                    taskMinPrimaryKeyQueue.put(taskInfos.get(0).getSn());
                    transferPool.submit(new ThreadPoolTransferTask(taskInfos));
                }else {
                    return;
                }
                if((i+1) % 10 == 0){
                    Thread.sleep(100);
                }
            }
        }
        if(i == 0){
            Thread.sleep(100);
        }
    }

    private boolean isMaxTaskLockSn(long selectMax, long lockMinSn){
        if(lockMinSn <= 0) return false;

        return selectMax > lockMinSn;
    }

    public Long getCurPoolLockMinPri(){
        Object[] lockMinPrimaryKeyArr = taskMinPrimaryKeyQueue.toArray();
        if(lockMinPrimaryKeyArr == null){
            return null;
        }
        if(lockMinPrimaryKeyArr.length == 0){
            return null;
        }
        if(lockMinPrimaryKeyArr.length == 1){
            return (Long)lockMinPrimaryKeyArr[0];
        }
        Arrays.sort(lockMinPrimaryKeyArr);
        return (Long)lockMinPrimaryKeyArr[0];
    }

    public class TransferTaskInfo implements Comparable<TransferTaskInfo>{
        private final long sn;
        private final String timeColumn;

        public TransferTaskInfo(long start, String timeColumn) {
            this.sn = start;
            this.timeColumn = timeColumn;
        }

        public long getSn() {
            return sn;
        }

        public String getTimeColumn() {
            return timeColumn;
        }

        public int compareTo(TransferTaskInfo o) {
            return (int)(sn-o.getSn());
        }
    }

    public class ThreadPoolTransferTask implements Runnable {

        private long primaryKeyStart;
        private List<TransferTaskInfo> range;

        public ThreadPoolTransferTask(List<TransferTaskInfo> range) {
            this.range = range;
        }

        public void run() {
            boolean isAdd = false;
            long start = System.currentTimeMillis();
            int count = 0;
            try {
                if(range.isEmpty()) return;

                primaryKeyStart = range.get(0).getSn();

                Map<String, List<Long>> timeMap = new HashMap<String, List<Long>>();
                for (TransferTaskInfo tmp : range) {
                    String targetTable = getTargetTable(tmp.getTimeColumn());
                    List<Long> tmpList = timeMap.get(targetTable);
                    if(tmpList == null){
                        timeMap.put(targetTable, tmpList = new ArrayList<Long>());
                    }
                    tmpList.add(tmp.getSn());
                }
                System.out.println(Thread.currentThread().getName() + " start range " + range.get(0).getSn()
                        + " " + range.get(range.size()-1).getSn());
                boolean result;
                for (String time : timeMap.keySet()) {
                    List<String> snsStrs = new ArrayList<String>();
                    List<Long> snsList = timeMap.get(time);
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<snsList.size(); i++){
                        long sn = snsList.get(i);
                        sb.append(sn).append(",");
                        if((i+1)%1000 == 0){
                            snsStrs.add(sb.delete(sb.length()-1, sb.length()).toString());
                            sb = new StringBuilder();
                        }
                    }
                    if(sb.length() > 0){
                        snsStrs.add(sb.delete(sb.length()-1, sb.length()).toString());
                    }
                    do {
                        result = transferData(time, snsStrs);
                        if (!result) {
                            Thread.sleep(1000);
                            count++;
                        }else {
                            delete(snsStrs);
                        }
                    } while (!result && count<3);
                    if(!result && count==3){
                        System.out.println(mission.getSourceTable() + " - 转存数据失败，主键范围:"
                                + snsList.get(0) + " ~ " + snsList.get(snsList.size()-1));
                    }
                }
                isAdd = true;
                System.out.println(Thread.currentThread().getName() + " end range " + range.get(0).getSn()
                        + " " + range.get(range.size()-1).getSn() + " 用时: " + (System.currentTimeMillis() - start));
            }catch (InterruptedException e){
                isAdd = false;
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                System.out.println("任务队列:" + transferWorkQueue.size()
                        + ", 分片范围: " + range.get(0).getSn() + " ~ "
                        + range.get(range.size()-1).getSn()
                        + ", 耗时: " + (System.currentTimeMillis() - start));
                taskMinPrimaryKeyQueue.remove(primaryKeyStart);
                if (isAdd) {
                    transferWorkQueue.offer(this);
                }
            }
        }
    }

    public String getTargetTable(String time){
        time = time.replaceAll(" ", "");
        time = time.replaceAll(":", "");
        time = time.replaceAll("-", "");
        if (mission.getDestTable().endsWith("YYYYMMDDHH")) {
            if(time.length()>10){
                time = time.substring(0, 10);
            }
        }
        String sourceTable = mission.getSourceTable();
        if(sourceTable.endsWith("cache")){
            sourceTable = sourceTable.substring(0, sourceTable.length() - "cache".length());
        }
        return sourceTable+time;
    }

    public boolean transferData(String targetTable, List<String> snsList) {
        boolean result = false;
        long start = System.currentTimeMillis();
        Connection conn = null;
        Statement ps = null;
        boolean isNeedCreate = false;
        try {
            conn = DataSourceUtil.getConnection();
            conn.setAutoCommit(false);
            ps = conn.createStatement();
            for(String sns : snsList){
                String sql = "insert ignore into "+targetTable+" select * from " + mission.getSourceTable() +
                        "where sn in (" + sns + ")";
                ps.addBatch(sql);
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            String errMsg = e.getMessage();
            if(errMsg.contains("doesn't exist")){
                isNeedCreate = true;
            }else{
                System.out.println(errMsg);
            }
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            result = false;
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
            if(isNeedCreate){
                createTable(mission.getSourceTable(), targetTable);
            }
            System.out.println("transferData time: " + (System.currentTimeMillis()-start));
        }
        return result;
    }

    public void delete(List<String> sns) {
        Connection conn = null;
        Statement ps = null;
        try {
            conn = DataSourceUtil.getConnection();
            ps = conn.createStatement();
            for(String sn : sns){
                String sql = "delete from submit_message_send_history where sn in (" + sns + ")";
                ps.addBatch(sql);
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
        }
    }

    public List<TransferTaskInfo> selectTask(long startSn) {
        String sql = "select " + mission.getPrimaryKey() + ", " + mission.getTimeColumn()
                + " from " + mission.getSourceTable() + " where " + mission.getWhereCondition()
                + " and " + mission.getPrimaryKey() + ">=" + startSn + " order by "
                + mission.getPrimaryKey() + " limit " + mission.getLimit();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TransferTaskInfo> list = new ArrayList<TransferTaskInfo>();
        try {
            conn = DataSourceUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                list.add(new TransferTaskInfo(rs.getLong(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
            if(list.size()>0){
                Collections.sort(list);
            }
        }
        return list;
    }

    private void createTable(String sourceTable, String tableName){
        String sql = "create table if not exists " + tableName + " like " + sourceTable;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DataSourceUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
        }
    }

    public long executePrimaryKeyQuery(String sql){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DataSourceUtil.freeConnection(conn, ps);
        }
        return 0L;
    }

    public class TaskPutBlockPolicy implements RejectedExecutionHandler{

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if(!executor.isShutdown()){
                try {
                    transferWorkQueue.put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
