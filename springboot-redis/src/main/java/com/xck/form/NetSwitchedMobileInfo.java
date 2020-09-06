package com.xck.form;

import java.io.Serializable;

//测试redis客户端打印字符串，还原java类
public class NetSwitchedMobileInfo implements Serializable {
    private static final long serialVersionUID = -7483125418993414478L;

    private String mobile;

    private int dest_td_type;

    public NetSwitchedMobileInfo() {}

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getDest_td_type() {
        return this.dest_td_type;
    }

    public void setDest_td_type(int dest_td_type) {
        this.dest_td_type = dest_td_type;
    }
}