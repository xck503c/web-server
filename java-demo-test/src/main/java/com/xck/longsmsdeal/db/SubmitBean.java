package com.xck.longsmsdeal.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubmitBean {
    private long submitSn;
    private int userSn;
    private String userId;
    private String spNumber;
    private String mobile;
    private String content;
    private String msgId;
    private int pkNumber;
    private int pkTotal;
    private int subMsgId;
    private int msgFormat;
    private String msgReceiveTime;
    private Map<String, Object> extraFieldsJson = new ConcurrentHashMap<String, Object>();

    public long getSubmitSn() {
        return submitSn;
    }

    public void setSubmitSn(long submitSn) {
        this.submitSn = submitSn;
    }

    public int getUserSn() {
        return userSn;
    }

    public void setUserSn(int userSn) {
        this.userSn = userSn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getPkNumber() {
        return pkNumber;
    }

    public void setPkNumber(int pkNumber) {
        this.pkNumber = pkNumber;
    }

    public int getPkTotal() {
        return pkTotal;
    }

    public void setPkTotal(int pkTotal) {
        this.pkTotal = pkTotal;
    }

    public int getSubMsgId() {
        return subMsgId;
    }

    public void setSubMsgId(int subMsgId) {
        this.subMsgId = subMsgId;
    }

    public int getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(int msgFormat) {
        this.msgFormat = msgFormat;
    }

    public String getMsgReceiveTime() {
        return msgReceiveTime;
    }

    public void setMsgReceiveTime(String msgReceiveTime) {
        this.msgReceiveTime = msgReceiveTime;
    }

    public Map<String, Object> getExtraFieldsJson() {
        return extraFieldsJson;
    }

    public void setExtraFieldsJson(Map<String, Object> extraFieldsJson) {
        this.extraFieldsJson = extraFieldsJson;
    }

    public void add(String key, Object o){
        extraFieldsJson.put(key, o);
    }
}
