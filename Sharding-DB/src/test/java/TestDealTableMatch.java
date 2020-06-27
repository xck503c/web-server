import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDealTableMatch {
    DruidDataSource dataSource;
    long receive_sn;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int MIN_NUMBER = 100;

    /**
     * #db.url=jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=159.1.39.177)(PORT=1521)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=sms)))
     * db.url=jdbc:oracle:thin:@159.1.33.70:1521:sms
     * db.driverclass=oracle.jdbc.driver.OracleDriver
     * db.maxActive=100
     * db.initialSize=20
     * db.minIdle=10
     * db.maxWait=60000
     * db.query= SELECT 'X' FROM DUAL
     * db.type=oracle
     */
    @Before
    public void before(){
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@49.235.32.249:1521:HELOWIN");
        dataSource.setUsername("SMS_NJYH");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setValidationQuery("SELECT 'X' FROM DUAL");
        dataSource.setMaxActive(5);
        dataSource.setInitialSize(2);
        dataSource.setMinIdle(2);
        dataSource.setMaxWait(600000);
    }

    @Test
    public void test(){
        receive_sn = getReceiveReportSn().get("min");
        Map<ReportBean, SmsMessage> matchMap = fetchReceiveReport(receive_sn, 1000);//limit
        Map<String, Integer> map = getReceiveReportSn();
        int max = map.get("max");
        int min = map.get("min");
        List<ReportBean> recReport = new ArrayList<ReportBean>(matchMap.size());
        List<Long> receive_sn_list = new ArrayList<Long>(matchMap.size());//receive
        List<Long> catch_sn_list = new ArrayList<Long>(matchMap.size());//catch

        for(ReportBean eachReport : matchMap.keySet()){
            setNextStartSn(eachReport);
            SmsMessage sms = matchMap.get(eachReport);
            if(sms.getSubmit_sn() != 0){
                System.out.println(sms + "" + eachReport);
                recReport.add(eachReport);
                receive_sn_list.add(eachReport.getSn());
                catch_sn_list.add(sms.getSubmit_sn());
            }else {
                try {
                    long time   = sdf.parse(sms.getMsg_report_time()).getTime();
                    if(sms.getMsg_report_time()!=null &&System.currentTimeMillis() - time > 1000 * 1800000){
                        System.out.println("超时");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(max-receive_sn <MIN_NUMBER){
            receive_sn = min ;
        }

        if(receive_sn_list.size() > 0){
            System.out.println(receive_sn_list);
        }
        if(catch_sn_list.size() >0){
            System.out.println(catch_sn_list);
        }
    }

    public Map<String, Integer> getReceiveReportSn() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        String sql = "select max(sn) max,min(sn) min from  receive_report_info ";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.put("max", rs.getInt("max"));
                result.put("min", rs.getInt("min"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(conn, ps, rs);
        }
        return result;
    }

    public Map<ReportBean, SmsMessage> fetchReceiveReport(long receive_sn, int limit) {
        Map<ReportBean, SmsMessage> result = new HashMap<ReportBean, SmsMessage>();
//group by 千万别忘记，会导致发送记录多写
        String sql = "select c.submit_sn, c.user_sn, c.user_id, c.service_code, c.ext_code, c.user_ext_code, "
                + "c.td_code c_td_code , c.sp_number c_sp_number, c.filter_flag, "
                + "c.msg_content, c.msg_id c_msg_id,"
                + "c.status, c.response, c.fail_desc c_fail_desc,"
                + "c.pknumber, c.pktotal, c.sub_msg_id,"
                + "c.price, c.charge_count,"
                + "c.insert_time c_insert_time, c.msg_format,"
                + "c.dest_flag,"
                + "c.msg_receive_time, c.msg_deal_time, c.msg_scan_time, c.msg_send_time,"
                + "c.check_user,  c.cache_sn, c.country_cn, c.ori_mobile,c.is_encode,c.complete_content,c.msg_guid,"
                + "c.dept_code, c.global_seq,"
                + "r.sn,r.rpt_return_time, r.td_code r_td_code, r.sp_number r_sp_number,"
                + "r.mobile r_mobile ,r.msg_id r_msg_id, r.fail_desc r_fail_desc, to_char(r.insert_time,'yyyy-mm-dd hh:mm:ss') r_insert_time, r.err, r.stat "
                + " from (select * from  receive_report_info where sn >= "+receive_sn+" and sn in ("
                + " select  max(sn) from  receive_report_info  group by mobile,msg_id) and ROWNUM<= "+limit+" order by sn asc) r left join submit_message_send_catch c "
                + " on  r.mobile = c.mobile  and  r.msg_id = c.tmp_msg_id ";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                SmsMessage form = new SmsMessage();
                form.setSubmit_sn(rs.getInt("submit_sn"));
                form.setUser_sn(rs.getInt("user_sn"));
                form.setUser_id(rs.getString("user_id"));
                form.setService_code(rs.getString("service_code"));
                form.setExt_code(rs.getString("ext_code")==null?"":rs.getString("ext_code"));
                form.setUser_ext_code(rs.getString("user_ext_code")==null?"":rs.getString("user_ext_code"));
                form.setTd_code(rs.getString("c_td_code"));//
                form.setSp_number(rs.getString("c_sp_number"));//
                form.setFilter_flag(rs.getInt("filter_flag"));
                form.setMobile(rs.getString("r_mobile"));//
                form.setMsg_content(rs.getString("msg_content"));
                form.setMsg_id(rs.getString("c_msg_id"));//
                form.setInsert_time(rs.getString("c_insert_time"));//
                form.setStatus(rs.getInt("status"));
                form.setResponse(1000);
                form.setFail_desc(rs.getString("c_fail_desc"));//
                form.setTmp_msg_id(rs.getString("r_msg_id"));//
                form.setPknumber(rs.getInt("pknumber"));
                form.setPktotal(rs.getInt("pktotal"));
                form.setSub_msg_id(rs.getInt("sub_msg_id"));
                form.setPrice(rs.getDouble("price"));
                form.setCharge_count(rs.getInt("charge_count"));
                form.setMsg_format(rs.getInt("msg_format"));
                form.setDest_flag(rs.getString("dest_flag"));
                form.setMsg_receive_time(rs.getString("msg_receive_time"));
                form.setMsg_deal_time(rs.getString("msg_deal_time"));
                form.setMsg_scan_time(rs.getString("msg_scan_time"));
                form.setMsg_send_time(rs.getString("msg_send_time"));
                form.setMsg_report_time(rs.getString("r_insert_time"));//
                form.setCheck_user(rs.getString("check_user"));
                form.setCache_sn(rs.getInt("cache_sn"));
                form.setCountry_cn(rs.getString("country_cn"));
                form.setOri_mobile(rs.getString("ori_mobile"));
                form.setIs_encode(rs.getInt("is_encode"));
                form.setComplete_content(rs.getString("complete_content"));
                form.setMsg_guid(rs.getString("msg_guid"));
                form.addExtraField("dept_code", rs.getString("dept_code"));//新增交易机构号
                form.addExtraField("global_seq", rs.getString("global_seq"));//新增全局流水号

                ReportBean report = new ReportBean();
                report.setSn(rs.getInt("sn"));
                report.setTd_code(rs.getString("r_td_code"));//
                report.setMobile(rs.getString("r_mobile"));//
                report.setSp_number(rs.getString("r_sp_number"));//
                report.setFail_desc(rs.getString("r_fail_desc"));//
                report.setErr(rs.getString("err"));
                report.setStat(rs.getInt("stat"));
                report.setMsg_id(rs.getString("r_msg_id"));//
                report.setRpt_return_time(rs.getString("rpt_return_time"));
                result.put(report, form);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(conn, ps, rs);
        }
        return result;
    }

    private void setNextStartSn(ReportBean eachReport) {
        if(eachReport.getSn() > receive_sn){
            receive_sn = eachReport.getSn();
        }
    }

    public void free(Connection conn, PreparedStatement ps, ResultSet rs){
        try {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
