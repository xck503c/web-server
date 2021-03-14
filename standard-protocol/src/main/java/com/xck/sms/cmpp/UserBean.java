package com.xck.sms.cmpp;

import java.util.HashMap;
import java.util.Map;

public class UserBean {

    private String userIp;
    private String userId;
    private String pwd;
    private int status;
    private Map<String, Object> paramMap = new HashMap<String, Object>();

    public UserBean(){
        userId = "xck001";
        pwd = "xck123";
        status = 0;
        paramMap.put("gate_max_connect", "2");
        paramMap.put("gate_max_speed", "200");
        userIp = "127.0.0.1";
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getParamMap(String key) {
        return paramMap.get(key);
    }

}
