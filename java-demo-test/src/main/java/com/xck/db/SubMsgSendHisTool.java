package com.xck.db;

import com.hskj.DigestUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubMsgSendHisTool {

    public static ApplicationContext apx;

    public static void main(String[] args) {
        apx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        diffLogAndSubmitHis();
    }

    public static DataSource getDB(){
        return (DataSource)apx.getBean("dataSource");
    }

    public static void returnConnection(Connection connection){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对比日志文件中的数据和数据库数据不一致的地方
     */
    public static void diffLogAndSubmitHis(){
        Map<String, Integer> map = new HashMap<String, Integer>(100000, 0.9f);

        String srcPath = "D:/tmp.txt";
        File file = new File(srcPath);
        BufferedReader fr = null;
        try {
            FileReader fis = new FileReader(file);
            fr = new BufferedReader(fis);

            String line = null;
            while ((line = fr.readLine()) != null){
                int start = line.indexOf("mobile=");
                int end = line.indexOf(",", start);
                String mobile = line.substring(start+"mobile=".length(), end);

                int msgstart = line.indexOf("msg_id=");
                int msgend = line.indexOf(",", msgstart);
                String msgId = line.substring(msgstart+"msg_id=".length(), msgend);

                String key = msgId+mobile;

                Integer times = map.get(key);
                if(times == null){
                    map.put(key, 1);
                }else {
                    map.put(key, ++times);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
            }
        }

        try {
            System.out.println(DigestUtil.encryptData("15226483621"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(map.get("202103180952439778515225638460"));

        int count = 0;
        try {
            long sn = 0;
            while (true){
                List<SubHisInfo> list = select(sn);
                if(list == null || list.isEmpty()){
                    break;
                }
                sn = list.get(list.size()-1).sn;

                for (SubHisInfo tmp : list){
                    Integer times = map.get(tmp.msgId+tmp.mobile);
                    if(times == null){
                        System.out.println(tmp.msgId + " " + tmp.mobile + " " + DigestUtil.encryptData(tmp.mobile));
                    }else {
                        if(--times < 0){
                            System.out.println(tmp.msgId + " " + tmp.mobile + " " + DigestUtil.encryptData(tmp.mobile));
                        }else {
                            map.put(tmp.msgId+tmp.mobile, times);
                        }
                    }
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(count);
    }

    public static List<SubHisInfo> select(long maxSn){
        String sql = "select sn, mobile, msg_id from submit_message_send_history_20210318 where sn>"+maxSn+" and user_id='lpx111' and substring(msg_deal_time, 1, 16)>'2021-03-18 00:00' order by sn limit 1000";

        List<SubHisInfo> list = new ArrayList<SubHisInfo>(1000);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getDB().getConnection();
            ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                long sn = rs.getLong("sn");
                String mobile = DigestUtil.decryptData(rs.getString("mobile"));
                String msgId = rs.getString("msg_id");
                list.add(new SubHisInfo(sn, mobile, msgId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(conn);
        }

        return list;
    }

    public static class SubHisInfo{
        long sn;
        private String mobile;
        private String msgId;

        public SubHisInfo(long sn, String mobile, String msgId) {
            this.sn = sn;
            this.mobile = mobile;
            this.msgId = msgId;
        }
    }
}
