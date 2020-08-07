package com.xck.jvm;

import java.util.ArrayList;
import java.util.List;

//https://blog.csdn.net/iteye_7742/article/details/82141049
public class YGCBeforeOrAfterCheckToFullGC {

    public static void main(String[] args) throws Exception{
        Thread.sleep(15000);
        List<byte[]> list = new ArrayList<>();
        for(int i=0; i<7; i++){
            //每次分配3MB，是的eden在第三次的时候触发ygc
            //这个时候会将eden数据全部放到老年代中，晋升6MB
            //到第5，7次的时候，分别再次触发ygc，一共晋升到old18MB，平均每次6MB
            list.add(new byte[3*1024*1024]);
            Thread.sleep(500);
        }
        Thread.sleep(1000);
        //注意第7次ygc，可以看到old已经到了18MB，所以触发fullgc势在必行
        //但是本次清理，并没有清理掉任何东西
        list.clear();

        //这个时候eden里面还有3MB，我们再来分配两次3MB，让其准备触发YGC
        for(int i=0; i<2; i++){
            list.add(new byte[3*1024*1024]);
            Thread.sleep(1000);
        }
        //但是在YGC之前，先对比是否要直接fullgc，min(6MB,6MB)>old的2MB
        //所以触发了一次fullgc，这次fullgc将old清空，然后将eden中的3MB移动到old中
    }
}
