package com.xck.sms.cmpp;


import com.xck.sms.Handler;
import com.xck.sms.server.ServerConnectRegistry;
import com.xck.sms.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

public class CmppUserBean {
    private ReentrantLock userLock = new ReentrantLock();
    private ReentrantLock seqLock = new ReentrantLock();
    private String requestIp;

    private String userId;
    private String pwd;

    private int maxAllowConnectCount; //允许的最大连接数
    private int amoutPerSecond; //每秒允许的传输数量
    private int status;

    private int connectCount = 0;

    private int seq;


    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
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

    public int getMaxAllowConnectCount() {
        return maxAllowConnectCount;
    }

    public void setMaxAllowConnectCount(int maxAllowConnectCount) {
        this.maxAllowConnectCount = maxAllowConnectCount;
    }

    public int getAmoutPerSecond() {
        return amoutPerSecond;
    }

    public void setAmoutPerSecond(int amoutPerSecond) {
        this.amoutPerSecond = amoutPerSecond;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void userLock(){
        userLock.lock();
    }

    public void userUnLock(){
        userLock.unlock();
    }

    public void seqLock(){
        seqLock.lock();
    }

    public void seqUnLock(){
        seqLock.unlock();
    }

    public int getSeq(){
        if(++seq <= 0){
            seq = 1;
        }
        return seq;
    }

    public boolean incConnectCount(){
        connectCount++;
        if(connectCount > maxAllowConnectCount){
            connectCount--;
            return false;
        }
        return true;
    }

    public boolean dercConnectCount(){
        connectCount--;
        if(connectCount <= 0){
            return false;
        }
        return true;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public static byte valid(Handler handler, String authenticatorSource, String userId,
                             String requestIp, int timestamp) throws Exception{
        CmppUserBean cmppUserBean = ServerConnectRegistry.cmpplogin(userId, requestIp);
        if(cmppUserBean == null){
            return 3;
        }

        if(cmppUserBean.status == 1){
            return 6;
        }

        ByteBuf md5Buf = Unpooled.buffer(16);
        md5Buf.writeCharSequence(userId, Charset.forName("GBK"));
        md5Buf.writeBytes(new byte[9]);
        md5Buf.writeCharSequence(cmppUserBean.getPwd(), Charset.forName("GBK"));
        md5Buf.writeInt(timestamp);
        String md5 = StringUtils.md5ConvertByte(new String(md5Buf.array()), "ISO8859_1").trim();
        if(!authenticatorSource.equalsIgnoreCase(md5)){
            System.out.println("客户传输md5字符串: " + authenticatorSource
                    + ", 生成md5: " + md5);
            return 33;
        }

        int count = 1;
        try{
            cmppUserBean.userLock();
            if(!cmppUserBean.incConnectCount()){
                return 5;
            }
            count = cmppUserBean.getConnectCount();
        }finally {
            cmppUserBean.userUnLock();
        }

        handler.setCmppUserBean(cmppUserBean); //关联
        System.out.println("登陆成功, userId: " + cmppUserBean.getUserId()
                + "关联处理器成功, 当前连接数: " + count);
        return 0;
    }

    //生成下发响应，回调dosomething方法
    public void doSendSubmit(CmppSubmitMessage submitMessage){

    }

    public static String validLog(int code){
        switch (code){
            case 3: return "注册失败";
            case 6: return "账户关闭";
            case 33: return "签名校验错误";
            case 5: return "连接超了";
            case 0: return "登陆成功";
            default: return "未知源于";
        }
    }
}
