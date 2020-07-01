package com.xck.attachapi;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class AttachReflectMain {

    public static void main(String[] args) throws Exception {
        System.out.println("D://java-demo-test-1.0.0.jar");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
            virtualMachine.loadAgent("D://java-demo-test-1.0.0.jar", "cxs");
            System.out.println("ok");
            virtualMachine.detach();
        }
    }
}
