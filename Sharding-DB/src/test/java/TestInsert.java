import com.alibaba.druid.pool.DruidDataSource;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestInsert {

    @Test
    public void insert() throws Exception{
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://192.168.130.21:30521/xck_tmp1?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true");
        druidDataSource.setUsername("sms");
        druidDataSource.setPassword("hstest@2014");
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

        String sql = "insert into user_info (name, age, insert_time) values()";
        Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        statement.execute("");
    }
}
