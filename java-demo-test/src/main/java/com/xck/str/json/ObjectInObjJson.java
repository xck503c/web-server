package com.xck.str.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeConfig;

public class ObjectInObjJson {

    public static void main(String[] args) {
        A a = new A();
        System.out.println(JSONObject.toJSONString(a));
        SerializeConfig config = new SerializeConfig();
    }

    public static class A{
        private int j = 0;
        private String aStr = "xcka";

        @JSONField(serializeUsing = FastJsonInObjSerializer.class)
        private B b = new B();

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        public String getaStr() {
            return aStr;
        }

        public void setaStr(String aStr) {
            this.aStr = aStr;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B{
        private int i = 0;
        private String bStr = "xckb";

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getbStr() {
            return bStr;
        }

        public void setbStr(String bStr) {
            this.bStr = bStr;
        }
    }
}
