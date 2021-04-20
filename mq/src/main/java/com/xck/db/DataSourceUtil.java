package com.xck.db;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceUtil {
    private static String url = "jdbc:mysql://192.168.130.21:30521/cluster_server_xck?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";
    private static String username = "sms";
    private static String pwd = "hstest@2014";

    private static DruidDataSource dataSource;

    static {
        initDataSouce();
    }

    public static void initDataSouce(){
        dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(pwd);
        dataSource.setInitialSize(20);
        dataSource.setMinIdle(20);
        dataSource.setMaxActive(50);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setTestWhileIdle(true);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(180);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void freeConnection(Connection conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void freeConnection(Connection conn, Statement statement){
        try {
            if(statement!=null){
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        freeConnection(conn);
    }

    public static void freeConnection(Connection conn, Statement statement, ResultSet rs){
        try {
            if(rs!=null){
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        freeConnection(conn, statement);
    }
}
