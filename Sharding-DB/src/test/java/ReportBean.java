import java.io.Serializable;


/**
 * 状态报告实例
 * @author dear
 *
 */
public class ReportBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8653909433806248585L;
	
	private long sn;
	private String user_id;
	private String td_code;
	private String sp_number;
	private String mobile;
	private String msg_id;
	private int status;
	private int try_times;
	private String fail_desc;
	private String err;
	private int stat;
	private int send_status;
	private long submitTime;
	
	private String insert_time;
	private String update_time ;
	
	 
	private String country_cn;//国家中文名称
	private String ori_mobile;//原始手机号码
	private double price;//短信计费
 
	/**
	 * 状态报告产生时的时间
	 */
	private long reveive_time;

	private int send_type;
	
	private int sub_seq;
	
	private int do_times;
	
	private String submit_time;//手机短信下发到网关时的时间
	private String rpt_return_time; 
	private String rpt_match_time ;
	private String rpt_ready_push_time ;
	private String rpt_pushed_time;
	
	private Long rpt_seq; //状态报告和下发历史的唯一关联标识
	
	private int pk_toal;
	private int pk_index;
	 
	public int getPk_toal() {
		return pk_toal;
	}

	public void setPk_toal(int pk_toal) {
		this.pk_toal = pk_toal;
	}

	public int getPk_index() {
		return pk_index;
	}

	public void setPk_index(int pk_index) {
		this.pk_index = pk_index;
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
	public void setRpt_seq(long rpt_seq) {
		this.rpt_seq = rpt_seq;
	}

	public String getSubmit_time() {
		return submit_time;
	}

	public void setSubmit_time(String submit_time) {
		this.submit_time = submit_time;
	}
	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getRpt_return_time() {
		return rpt_return_time;
	}

	public void setRpt_return_time(String rpt_return_time) {
		this.rpt_return_time = rpt_return_time;
	}

	public String getRpt_match_time() {
		return rpt_match_time;
	}

	public void setRpt_match_time(String rpt_match_time) {
		this.rpt_match_time = rpt_match_time;
	}

	public String getRpt_ready_push_time() {
		return rpt_ready_push_time;
	}

	public void setRpt_ready_push_time(String rpt_ready_push_time) {
		this.rpt_ready_push_time = rpt_ready_push_time;
	}

	public String getRpt_pushed_time() {
		return rpt_pushed_time;
	}

	public void setRpt_pushed_time(String rpt_pushed_time) {
		this.rpt_pushed_time = rpt_pushed_time;
	}

	/**
	 * 发送方式
	 * -1:客户不要
	 * 0:初始状态
	 * 1:客户自取
	 * 2:平台推送
	 */
	public int getSend_type() {
		return send_type;
	}
 
 

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	@Override
	public String toString() {
		return "ReportBean [user_id=" + user_id + ", td_code=" + td_code
				+ ", sp_number=" + sp_number + ", mobile=" + mobile
				+ ", msg_id=" + msg_id + ", status=" + status + ", try_times="
				+ try_times + ", send_status=" + send_status + ", submitTime="
				+ submitTime + ", insert_time=" + insert_time + ", ori_mobile="
				+ ori_mobile + ", reveive_time=" + reveive_time
				+ ", send_type=" + send_type + ", sub_seq=" + sub_seq
				+ ",submit_time=" + submit_time + " ,rpt_return_time="
				+ rpt_return_time + ", rpt_match_time=" + rpt_match_time
				+ ", rpt_ready_push_time=" + rpt_ready_push_time
				+ ", rpt_pushed_time=" + rpt_pushed_time + "]" + ", rpt_seq= "
				+ rpt_seq + ", pk_toal=" + pk_toal + ",pk_index=" + pk_index
				+ ", do_times="+do_times+",err="+err+",fail_desc="+fail_desc+"]";

	}

	public ReportBean(String sp_number , String mobile , String msgid , int stat , long sn , int try_times, String err){
		this.mobile = mobile;
		this.sp_number = sp_number;
		this.msg_id = msgid;
		this.stat = stat;
		this.sn = sn;
		this.try_times = try_times;
		this.err = err;
	}
	
	public ReportBean(){
	}
	
	 

	
	/**
	 * 长短信拆分的条数标记
	 * @return
	 */
	public int getSub_seq() {
		return sub_seq;
	}

	public void setSub_seq(int sub_seq) {
		this.sub_seq = sub_seq;
	}

	public long getReveive_time() {
		return reveive_time;
	}

	public void setReveive_time(long reveive_time) {
		this.reveive_time = reveive_time;
	}
	public String getFail_desc() {
		return fail_desc;
	}

	public void setFail_desc(String fail_desc) {
		this.fail_desc = fail_desc;
	}

	public String getCountry_cn() {
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
	/**
	 * 状态报告的发送状态
	 * 0:初始状态 ；1:redis发送超时；-1:客户不需要
	 */
	public int getSend_status() {
		return send_status;
	}

	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}
	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getSp_number() {
		return sp_number == null ? "" : sp_number;
	}
	public void setSp_number(String spNumber) {
		sp_number = spNumber;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getTry_times() {
		return try_times;
	}
	public void setTry_times(int tryTimes) {
		try_times = tryTimes;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}

	/**
	 * 0未推送
	 * 1推送成功
	 * 2推送失败
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	 

	public String getTd_code() {
		return td_code;
	}

	public void setTd_code(String td_code) {
		this.td_code = td_code;
	}

	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}

	public String getInsert_time() {
		return insert_time;
	}

	public int getDo_times() {
		return do_times;
	}

	public void setDo_times(int do_times) {
		this.do_times = do_times;
	}

	
	
}
