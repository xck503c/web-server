package com.xck.check.reactor;

/**
 * @Classname CheckHandler
 * @Description TODO
 * @Date 2020/12/6 19:57
 * @Created by xck503c
 */
public interface CheckHandler {

    //审核处理
    void handle(CheckEvent event) throws InterruptedException;
}
