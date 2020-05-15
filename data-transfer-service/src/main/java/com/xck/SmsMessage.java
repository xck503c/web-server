package com.xck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmsMessage {
	//submit参数
	private long sn;
	private long submit_sn;
	private int user_sn;
	private String user_id;
	private String service_code;
	private String ext_code;
	private String user_ext_code;
	private String td_code;
	private String sp_number;
	private int filter_flag;
	private String mobile;
	private String msg_content;
	private String msg_id;//用户提交时的msgid
	private String insert_time;
	private String update_time;
	private int status;
	private int response;
	private String fail_desc;
	private String tmp_msg_id; //不同通道的临时msg_id
	private int stat_flag;
	private int sub_msg_id;
	private int pknumber;
	private int pktotal;
	private double price;
	private int charge_count;
	private int msg_format;
	private String err;
	private String dest_flag;//流量走向标识
	private String msg_receive_time;//接收端收到的时间
	private String msg_deal_time;//dealdata处理开始时间
	private String msg_scan_time;//发送端扫描时间
	private String msg_send_time;//数据发送时间
	private String msg_report_time;//状态报告返回时间
	private String check_user = "system";
	private int cache_sn;
	private String country_cn;//国家中文名称
	private String ori_mobile;//原始手机号码
	private String signature;//签名信息
	private String operator_signature ;
	private String istest ;//是否是从Test表过来的数据


	private String area_code;
	private String complete_content ;
	private String report_fail_desc = "UNDELIV";
	private int try_times;
	private String md5_index;
	private String src_number;
	private Long rpt_seq; //状态报告和下发历史的唯一关联标识
	private int do_times;

	public SmsMessage() {

	}

	public Long getRpt_seq() {
		if(rpt_seq==null){
			rpt_seq=0l;
		}
		return rpt_seq;
	}

	public void setRpt_seq(Long rpt_seq) {
		this.rpt_seq = rpt_seq;
	}

	public String getOperator_signature() {
		if(operator_signature==null){
			operator_signature="";
		}
		return operator_signature;
	}

	public void setOperator_signature(String operator_signature) {
		this.operator_signature = operator_signature;
	}
	public String getSrc_number() {
		return src_number;
	}

	public void setSrc_number(String src_number) {
		this.src_number = src_number;
	}

	public String getCountry_cn() {
		if(null==country_cn){
			country_cn = "未知" ;
		}else if("中华人民共和国".equals(country_cn)){
			country_cn = "中国大陆" ;
		}
		return country_cn;
	}

	public void setCountry_cn(String country_cn) {
		this.country_cn = country_cn;
	}

	public String getOri_mobile() {
		return ori_mobile;
	}

	public void setOri_mobile(String ori_mobile) {
		this.ori_mobile = ori_mobile;
	}

	public String getSignature() {
		if(signature==null||"null".equals(signature)){
			signature = "";
		}
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getReport_fail_desc() {
		return report_fail_desc;
	}

	public void setReport_fail_desc(String report_fail_desc) {
		this.report_fail_desc = report_fail_desc;
	}

	private Map<String, Object> extraFields = new ConcurrentHashMap<String, Object>();
	
	public String getFullUserSubmitCode(){
		return service_code + ext_code + user_ext_code;
	}
	
	public String getSubserSubmitCode(){
		return service_code + user_ext_code;
	}

	public String getIstest() {
		if(istest==null){
			istest="";
		}
		return istest;
	}

	public void setIstest(String istest) {
		this.istest = istest;
	}
 
	@Override
	public String toString() {
		return "SmsMessage [sn=" + sn + ", submit_sn=" + submit_sn
				+ ", user_sn=" + user_sn + ", user_id=" + user_id
				+ ", service_code=" + service_code + ", ext_code=" + ext_code
				+ ", user_ext_code=" + user_ext_code + ", td_code=" + td_code
				+ ", sp_number=" + sp_number + ", filter_flag=" + filter_flag
				+ ", mobile=" + mobile + ", msg_content=" + msg_content
				+ ", msg_id=" + msg_id + ", insert_time=" + insert_time
				+ ", update_time=" + update_time + ", status=" + status
				+ ", response=" + response + ", fail_desc=" + fail_desc
				+ ", tmp_msg_id=" + tmp_msg_id + ", stat_flag=" + stat_flag
				+ ", sub_msg_id=" + sub_msg_id + ", pknumber=" + pknumber
				+ ", pktotal=" + pktotal + ", price=" + price
				+ ", charge_count=" + charge_count + ", msg_format="
				+ msg_format + ", err=" + err + ", dest_flag=" + dest_flag
				+ ", msg_receive_time=" + msg_receive_time + ", msg_deal_time="
				+ msg_deal_time + ", msg_scan_time=" + msg_scan_time
				+ ", msg_send_time=" + msg_send_time + ", msg_report_time="
				+ msg_report_time + ", check_user=" + check_user
				+ ", cache_sn=" + cache_sn + ", country_cn=" + country_cn
				+ ", ori_mobile=" + ori_mobile + ", signature=" + signature
				+ ", operator_signature=" + operator_signature + ", area_code="
				+ area_code + ", complete_content=" + complete_content
				+ ", report_fail_desc=" + report_fail_desc + ", try_times="
				+ try_times + ", md5_index=" + md5_index + ", src_number="
				+ src_number +", rpt_seq= "+rpt_seq;
	}
	
	public String getString() {
		return "SmsMessage  user_id=" + user_id + ", td_code=" + td_code
				+ ", mobile=" + mobile + ", msg_content=" + msg_content
				+ ", msg_id=" + msg_id  +", do_times="+do_times;
	}

	public void addExtraField(String key, Object value){
		boolean isKeyOK = (key != null && !key.equals(""));
		boolean isValueOK = (value != null);
		if(isKeyOK && isValueOK){
			if(extraFields == null){
				extraFields = new HashMap<String, Object>();;
			}
			extraFields.put(key, value);
		}
	}
	
	public Object getExtraField(String key){
		Object result = null;
		boolean isExtraNotNull = this.extraFields != null;
		boolean isKeyOK = key != null && !key.equals("");
		if(isExtraNotNull && isKeyOK){
			result = this.extraFields.get(key);
		}
		return result;
	}
	
	public long getSubmit_sn() {
		return submit_sn;
	}

	public void setSubmit_sn(long submit_sn) {
		this.submit_sn = submit_sn;
	}

	public String getDest_flag() {
		if(dest_flag==null){
			dest_flag = "";
		}
		return dest_flag;
	}
	public void setDest_flag(String dest_flag) {
		this.dest_flag = dest_flag;
	}
	public int getUser_sn() {
		return user_sn;
	}
	public void setUser_sn(int userSn) {
		user_sn = userSn;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSp_number() {
		return sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msgId) {
		msg_id = msgId;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public void setMsg_content(String msgContent) {
		msg_content = msgContent;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public void setInsert_time(String insertTime) {
		insert_time = insertTime;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String updateTime) {
		update_time = updateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * 0 发送成功
	 * 1000 未知
	 * 2或其他 发送失败
	 * @return
	 */
	public int getResponse() {
		return response;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	public int getPknumber() {
		return pknumber;
	}
	public void setPknumber(int pknumber) {
		this.pknumber = pknumber;
	}
	public int getPktotal() {
		return pktotal;
	}
	public void setPktotal(int pktotal) {
		this.pktotal = pktotal;
	}
	public int getSub_msg_id() {
		return sub_msg_id;
	}
	public void setSub_msg_id(int subMsgId) {
		sub_msg_id = subMsgId;
	}
	public int getMsg_format() {
		return msg_format;
	}
	public void setMsg_format(int msgFormat) {
		msg_format = msgFormat;
	}
	public int getCharge_count() {
		return charge_count;
	}
	public void setCharge_count(int chargeCount) {
		charge_count = chargeCount;
	}
	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}
	public String getFail_desc() {
		if(fail_desc==null){
			fail_desc = "";
		}
		return fail_desc;
	}
	public void setTmp_msg_id(String tmp_msg_id) {
		this.tmp_msg_id = tmp_msg_id;
	}
	public String getTmp_msg_id() {
		return tmp_msg_id;
	}
	public void setStat_flag(int stat_flag) {
		this.stat_flag = stat_flag;
	}
	public int getStat_flag() {
		return stat_flag;
	}
	public void setComplete_content(String complete_content) {
		this.complete_content = complete_content;
	}
	public String getComplete_content() {
		return complete_content;
	}

	public void setExtraFields(Map<String, Object> extraFields) {
		this.extraFields = extraFields;
	}
	public Map<String, Object> getExtraFields() {
		return extraFields;
	}
	public void setMsg_receive_time(String msg_receive_time) {
		this.msg_receive_time = msg_receive_time;
	}

	public String getMsg_receive_time() {
		return msg_receive_time;
	}

	public void setMsg_deal_time(String msg_deal_time) {
		this.msg_deal_time = msg_deal_time;
	}

	public String getMsg_deal_time() {
		return msg_deal_time;
	}

	public void setMsg_scan_time(String msg_scan_time) {
		this.msg_scan_time = msg_scan_time;
	}

	public String getMsg_scan_time() {
		return msg_scan_time;
	}

	public void setMsg_send_time(String msg_send_time) {
		this.msg_send_time = msg_send_time;
	}

	public String getMsg_send_time() {
		return msg_send_time;
	}

	/**
	 * 状态报告返回时间
	 * @param msg_report_time
	 */
	public void setMsg_report_time(String msg_report_time) {
		this.msg_report_time = msg_report_time;
	}

	public String getMsg_report_time() {
		return msg_report_time;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setFilter_flag(int filter_flag) {
		this.filter_flag = filter_flag;
	}

	public int getFilter_flag() {
		return filter_flag;
	}

	public void setService_code(String service_code) {
		this.service_code = service_code;
	}

	public String getService_code() {
		return service_code;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getErr() {
		if(err==null){
			err = "";
		}
		return err.trim();
	}

	public void setExt_code(String ext_code) {
		this.ext_code = ext_code;
	}

	public String getExt_code() {
		return ext_code;
	}

	public void setUser_ext_code(String user_ext_code) {
		this.user_ext_code = user_ext_code;
	}

	public String getUser_ext_code() {
		return user_ext_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}

	public String getTd_code() {
		return td_code;
	}

	public void setSn(long sn) {
		this.sn = sn;
	}

	public long getSn() {
		return sn;
	}

	public void setCheck_user(String check_user) {
		this.check_user = check_user;
	}
	public String getCheck_user() {
		return check_user;
	}
	public void setCache_sn(int cache_sn) {
		this.cache_sn = cache_sn;
	}
	public int getCache_sn() {
		return cache_sn;
	}

	/**
	 * @param area_code the area_code to set
	 */
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	/**
	 * @return the area_code
	 */
	public String getArea_code() {
		return area_code;
	}
	public void setMd5_index(String md5_index) {
		this.md5_index = md5_index;
	}

	public String getMd5_index() {
		return md5_index;
	}

	public void setTry_times(int try_times) {
		this.try_times = try_times;
	}

	public int getTry_times() {
		return try_times;
	}

	public int getDo_times() {
		return do_times;
	}

	public void setDo_times(int do_times) {
		this.do_times = do_times;
	}
}
