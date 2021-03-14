package com.xck.wechat;

import com.xck.Packet;
import com.xck.wechat.Command;

/**
 * 登陆请求数据包
 */
public class LoginRequestPacket extends Packet {

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
