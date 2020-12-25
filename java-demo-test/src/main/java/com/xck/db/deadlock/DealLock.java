package com.xck.db.deadlock;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DealLock {

    public static int cpus = Runtime.getRuntime().availableProcessors();

    public static ApplicationContext apx;

    public static void main(String[] args) {
        apx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        int threadSize = 2;

        for(int i=0; i<threadSize; i++){
            Thread t = new Thread(new UpdatePriTask());
            t.start();
        }

        for(int i=0; i<threadSize; i++){
            Thread t = new Thread(new UpdateNorTask());
            t.start();
        }
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

    public static String updatePriSQL = "update dead_lock set status=0 where id=1";
    public static String updateNorSQL = "update dead_lock set status=1 where md5='3'";

    public static String insertOrupdateSQL1 = "insert into dead_lock (id,md5,status) values (null,'3', 1) "
            + " on duplicate key update status=0";
    public static String insertOrupdateSQL2 = "insert into dead_lock (id,md5,status) values (null,'5', 1) "
            + " on duplicate key update status=1";

    public static class UpdatePriTask implements Runnable{

        @Override
        public void run() {
            for(int i=0; i<100000; i++){
                updatePri();
            }
        }
    }

    public static class UpdateNorTask implements Runnable{

        @Override
        public void run() {
            for(int i=0; i<100000; i++){
                updateNor();
            }
        }
    }

    public static void updatePri(){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getDB().getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(insertOrupdateSQL1);
            ps.executeUpdate();

            ps = connection.prepareStatement(insertOrupdateSQL2);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            returnConnection(connection);
        }
    }

    public static void updateNor(){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getDB().getConnection();
            connection.setAutoCommit(false);

//            ps = connection.prepareStatement(updateNorSQL);
//            ps.executeUpdate();

            ps = connection.prepareStatement(insertOrupdateSQL2);
            ps.executeUpdate();

            ps = connection.prepareStatement(insertOrupdateSQL1);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            returnConnection(connection);
        }
    }
}
