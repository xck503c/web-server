package com.xck.longsmsdeal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubmitBean {
    private String userId;
    private String[] mobiles;
    private String content;
    private String spNumber;
    private int pkTotal;
    private int pkNumber;
    private String msgId;
    private String subMsgId;
    private int msgFormat;

    private String oriMobile;
    private String serviceCode;
    private String extCode;
    private String useExtCode;
    private String tdCode;
    private double price;
    private int chargeCount;
    private String signature;
    private String operSignature;
    private int withGateSign;
    private String signExtCode;
    private int checkLength;
    private String srcNumber;
    private String isMultiSign;
    private String isLimitFirst;

    private Map<String, Object> extraFields = new ConcurrentHashMap<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getMobiles() {
        return mobiles;
    }

    public void setMobiles(String[] mobiles) {
        this.mobiles = mobiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public int getPkTotal() {
        return pkTotal;
    }

    public void setPkTotal(int pkTotal) {
        this.pkTotal = pkTotal;
    }

    public int getPkNumber() {
        return pkNumber;
    }

    public void setPkNumber(int pkNumber) {
        this.pkNumber = pkNumber;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSubMsgId() {
        return subMsgId;
    }

    public void setSubMsgId(String subMsgId) {
        this.subMsgId = subMsgId;
    }

    public int getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(int msgFormat) {
        this.msgFormat = msgFormat;
    }

    public String getOriMobile() {
        return oriMobile;
    }

    public void setOriMobile(String oriMobile) {
        this.oriMobile = oriMobile;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getUseExtCode() {
        return useExtCode;
    }

    public void setUseExtCode(String useExtCode) {
        this.useExtCode = useExtCode;
    }

    public String getTdCode() {
        return tdCode;
    }

    public void setTdCode(String tdCode) {
        this.tdCode = tdCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getChargeCount() {
        return chargeCount;
    }

    public void setChargeCount(int chargeCount) {
        this.chargeCount = chargeCount;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOperSignature() {
        return operSignature;
    }

    public void setOperSignature(String operSignature) {
        this.operSignature = operSignature;
    }

    public int getWithGateSign() {
        return withGateSign;
    }

    public void setWithGateSign(int withGateSign) {
        this.withGateSign = withGateSign;
    }

    public String getSignExtCode() {
        return signExtCode;
    }

    public void setSignExtCode(String signExtCode) {
        this.signExtCode = signExtCode;
    }

    public int getCheckLength() {
        return checkLength;
    }

    public void setCheckLength(int checkLength) {
        this.checkLength = checkLength;
    }

    public String getSrcNumber() {
        return srcNumber;
    }

    public void setSrcNumber(String srcNumber) {
        this.srcNumber = srcNumber;
    }

    public String getIsMultiSign() {
        return isMultiSign;
    }

    public void setIsMultiSign(String isMultiSign) {
        this.isMultiSign = isMultiSign;
    }

    public String getIsLimitFirst() {
        return isLimitFirst;
    }

    public void setIsLimitFirst(String isLimitFirst) {
        this.isLimitFirst = isLimitFirst;
    }

    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(Map<String, Object> extraFields) {
        this.extraFields = extraFields;
    }
}
