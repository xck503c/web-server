package com.xck;

import com.xck.cmpp.CmppUserBean;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class Handler extends ChannelInboundHandlerAdapter {
    protected CmppUserBean cmppUserBean;

    public void setCmppUserBean(CmppUserBean cmppUserBean) {
        this.cmppUserBean = cmppUserBean;
    }

    public CmppUserBean getCmppUserBean() {
        return cmppUserBean;
    }
}
