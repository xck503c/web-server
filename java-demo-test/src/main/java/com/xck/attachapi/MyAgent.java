package com.xck.attachapi;

import java.lang.instrument.Instrumentation;

public class MyAgent {

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        System.out.println("=========agentmain方法执行========");
        String[] split = args.split(" ");
        String className = split[0];
        String methodName = split[1];
        ClassLoader classLoader = MyAgent.class.getClassLoader();
        classLoader.loadClass(className);
    }

    public static void premain(String agentOps, Instrumentation inst){
        System.out.println("=========premain方法执行========");
        System.out.println(agentOps);
    }
}
