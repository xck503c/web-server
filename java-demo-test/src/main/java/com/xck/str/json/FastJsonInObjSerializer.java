package com.xck.str.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

public class FastJsonInObjSerializer implements ObjectSerializer {

    public void write(JSONSerializer jsonSerializer, Object o, Object fildName, Type type, int features) throws IOException {

    }
}
