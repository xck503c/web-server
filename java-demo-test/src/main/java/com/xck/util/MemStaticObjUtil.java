package com.xck.util;

import com.sun.tools.attach.VirtualMachine;

/**
 * 静态内存对象查看工具
 *
 * @author xuchengkun
 * @date 2021/04/20 11:34
 **/
public class MemStaticObjUtil {

    public static void main(String[] args) throws Exception{
        String pid = "252748";
        VirtualMachine vm = VirtualMachine.attach(pid);

        System.out.println(MemStaticObjUtilTest.map.get("a"));
//        vm.loadAgent(agentPath, agentArgs);
    }
}
