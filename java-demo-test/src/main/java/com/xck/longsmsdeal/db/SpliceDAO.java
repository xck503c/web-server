package com.xck.longsmsdeal.db;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SpliceDAO {

    public static Logger infoLog = Logger.getLogger("info");

    @Resource(name = "dataSource")
    private DataSource dataSource;

    public boolean insert(List<SubmitBean> list, String table){
        boolean result = false;
        if(list != null && list.size()>0){
            String sql = "insert ignore into " + "";
        }
    }

    public Map<Integer, Object> dealTimeout1(String redisTime, int i){
        String columns = "submit_sn, user_sn, user_id, sp_number, mobile"
                + ", msg_content, msg_id, insert_time, pknumber, pktotal"
                + ", sub_msg_id, msg_format, msg_receive_time, long_msg_seq"
                + ", receive_point, status, extra_fields_json, type";

        Connection conn = null;
        PreparedStatement ps = null;

        Map<Integer, Object> spliceResult = new HashMap<Integer, Object>();

        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String select2 = "select long_msg_seq, pktotal from submit_message_long_splice where insert_time<'" + redisTime + "'"
                    + " group by 1 having count(1)=pktotal limit " + i + ", 500";
            ps = conn.prepareStatement(select2);
            ResultSet rs1 = ps.executeQuery();
            StringBuilder sb1 = new StringBuilder();
            while (rs1.next()){
                sb1.append("'").append(rs1.getString("long_msg_seq")).append("'").append(",");
            }

            if(sb1.length() > 0){
                sb1.delete(sb1.length()-1, sb1.length());
            }else{
                return spliceResult;
            }

            String select3 = "select " + columns + " from submit_message_long_splice where long_msg_seq in (" + sb1.toString() + ")";

            ps = conn.prepareStatement(select3);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            List<SubmitBean> selectBeans = new ArrayList<SubmitBean>();
            while (rs.next()){
                SubmitBean submitBean = new SubmitBean();
                submitBean.setSubmitSn(rs.getLong("submit_sn"));
                submitBean.setUserSn(rs.getInt("user_sn"));
                submitBean.setUserId(rs.getString("user_id"));
                if(StringUtils.isBlank(submitBean.getUserId())){
                    continue;
                }
                submitBean.setSpNumber(rs.getString("sp_number"));
                submitBean.setMobile(rs.getString("mobile"));
                submitBean.setContent(rs.getString("msg_content"));
                submitBean.setMsgId(rs.getString("msg_id"));
                submitBean.setPkNumber(rs.getInt("pknumber"));
                submitBean.setPkTotal(rs.getInt("pktotal"));
                submitBean.setSubMsgId(rs.getInt("sub_msg_id"));
                submitBean.setMsgFormat(rs.getInt("msg_format"));
                submitBean.setMsgReceiveTime(rs.getString("msg_receive_time"));
                String extra = rs.getString("extra_fields_json");
                if(StringUtils.isNotBlank(extra)){
                    submitBean.setExtraFieldsJson(JSONObject.parseObject(extra, ConcurrentHashMap.class));
                }
                submitBean.add(SubmitBeanExtendParams.LONG_MSG_SEQ, rs.getString("long_msg_seq"));
                submitBean.add(SubmitBeanExtendParams.TYPE, rs.getInt("type"));
                submitBean.add(SubmitBeanExtendParams.RECEIVE_POINT, rs.getInt("receive_point"));
                selectBeans.add(submitBean);
                sb.append(submitBean.getSubmitSn()).append(",");
            }

            if(selectBeans.isEmpty()){
                if(sb1.length() > 0){
                    spliceResult.put(5, 1);
                }
                return spliceResult;
            }

            if(sb.length() > 0){
                sb.delete(sb.length()-1, sb.length());

                String delete = "delte from submit_message_long_splice where submit_sn int (" + sb.toString() + ")";
                ps = conn.prepareStatement(delete);
                ps.execute();
            }

            conn.commit();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            freeConn(conn, ps);
        }

        return spliceResult;
    }

    public static void freeConn(Connection conn, Statement st){
        try{
            if(st != null){
                 st.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(conn != null){
            try{
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
