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
import java.util.List;

public class ReveiveReportDataEn {

    public static ApplicationContext apx;

    public static void main(String[] args) throws Exception{
//        apx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//
//        exec();
        System.out.println();
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

    public static void exec() throws Exception{
        int size = 0;
        long maxSn = 0;
        while (true) {
            List<UpdateInfo> list = select(maxSn);
            if(list.isEmpty()){
                System.out.println(maxSn);
                System.out.println(size);
                break;
            }
            maxSn = list.get(list.size()-1).getSn();

            for(UpdateInfo info : list){
                size++;
                String mobile = DigestUtil.decryptData(info.getMobile());
                if(mobile.startsWith("86")){
                    mobile = mobile.substring(2);
                }

                StringBuilder sb = new StringBuilder();
                sb.append("sn=").append(info.getSn()).append(", mobile=").append(mobile);
                info.setMobile(DigestUtil.encryptData(mobile));

                String masking = info.getMaskingMobile();
                if(masking.startsWith("86")){
                    masking = masking.substring(2);
                }
                sb.append(", masking=").append(masking);
                info.setMaskingMobile(masking);

                System.out.println(sb.toString());
            }

            System.out.println("update ======================================================");

            update(list);
        }
    }

    public static List<UpdateInfo> select(long maxSn){
        String sql = "select sn, mobile, masking_mobile from receive_report_info_20210226 where sn>"+maxSn+" order by sn limit 1000";

        List<UpdateInfo> list = new ArrayList<>(1000);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getDB().getConnection();
            ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                long sn = rs.getLong("sn");
                String mobile = rs.getString("mobile");
                String masking = rs.getString("masking_mobile");
                list.add(new UpdateInfo(sn, mobile, masking));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(conn);
        }

        return list;
    }

    public static void update(List<UpdateInfo> list){
        String sql = "update receive_report_info_20210226 set mobile=?, masking_mobile=? where sn=?";


        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getDB().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);

            for (UpdateInfo info : list){
                ps.setString(1, info.getMobile());
                ps.setString(2, info.getMaskingMobile());
                ps.setLong(3, info.getSn());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(conn);
        }
    }

    public static class UpdateInfo{
        private long sn;
        private String mobile;
        private String maskingMobile;

        public UpdateInfo(long sn, String mobile, String maskingMobile) {
            this.sn = sn;
            this.mobile = mobile;
            this.maskingMobile = maskingMobile;
        }

        public long getSn() {
            return sn;
        }

        public void setSn(long sn) {
            this.sn = sn;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMaskingMobile() {
            return maskingMobile;
        }

        public void setMaskingMobile(String maskingMobile) {
            this.maskingMobile = maskingMobile;
        }
    }
}
