package com.xck;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://192.168.1.105:3306/xck_tmp?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("xck123");
        druidDataSource.setMaxActive(8);
        druidDataSource.setMaxActive(10);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinIdle(5);

        TableRuleConfiguration submitTableRuleConfig = new TableRuleConfiguration();
        submitTableRuleConfig.setLogicTable("submit_message_send_history");

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(submitTableRuleConfig);

        DataSource dataSource = ShardingDataSourceFactory
                .createDataSource(dataSourceMap, shardingRuleConfig
                        , new HashMap<String, Object>(), new Properties());
    }
}
