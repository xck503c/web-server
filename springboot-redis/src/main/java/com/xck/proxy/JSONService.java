package com.xck.proxy;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

/**
 * 发
 *
 * @author xuchengkun
 * @date 2021/05/17 16:19
 **/
@Component
public class JSONService {
    public String parse2String(Object obj){
        return JSON.toJSONString(obj);
    }
}
