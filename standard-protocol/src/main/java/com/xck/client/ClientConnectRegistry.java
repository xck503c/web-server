package com.xck.client;

import com.xck.cmpp.CmppUserBean;
import com.xck.cmpp.UserBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnectRegistry {

    private static ReentrantLock loginRegistryLock = new ReentrantLock();

    private static ConcurrentHashMap<String, CmppUserBean> cmpploginRegistry =
            new ConcurrentHashMap<String, CmppUserBean>();

    private static ConcurrentHashMap<String, UserBean> userBeanMap =
            new ConcurrentHashMap<String, UserBean>();

    static {
        userBeanMap.put("xck001", new UserBean());
    }

    public static CmppUserBean cmpplogin(String userId, String requestIp){
        CmppUserBean cmppUserBean;
        try{
            loginRegistryLock.lock();
            cmppUserBean = cmpploginRegistry.get(userId);
            if(cmppUserBean == null){
                UserBean userBean = userBeanMap.get(userId);
                cmppUserBean = new CmppUserBean();
                cmppUserBean.setAmoutPerSecond(Integer.parseInt((String)userBean.getParamMap("gate_max_connect")));
                cmppUserBean.setMaxAllowConnectCount(Integer.parseInt((String)userBean.getParamMap("gate_max_speed")));
                cmppUserBean.setUserId(userBean.getUserId());
                cmppUserBean.setPwd(userBean.getPwd());
                cmppUserBean.setStatus(userBean.getStatus());
                cmppUserBean.setRequestIp(requestIp);
                cmpploginRegistry.put(userId, cmppUserBean);
            }
        }finally {
            loginRegistryLock.unlock();
        }

        return cmppUserBean;
    }

    public static void cmppunlogin(String userId){
        CmppUserBean cmppUserBean;
        try{
            loginRegistryLock.lock();
            cmppUserBean = cmpploginRegistry.get(userId);
        }finally {
            loginRegistryLock.unlock();
        }

        if(cmppUserBean == null) return;

        try {
            cmppUserBean.userLock();
            if(cmppUserBean.dercConnectCount()){
                System.out.println("扣除一个链接, userId: " + cmppUserBean.getUserId()
                        + ", 当前连接: " + cmppUserBean.getConnectCount());
            }else {
                System.out.println("无连接，从注册中心中移除, userId: " + cmppUserBean.getUserId()
                        + ", 当前连接: " + cmppUserBean.getConnectCount());
            }
        } finally {
            cmppUserBean.userUnLock();
        }
    }

}
