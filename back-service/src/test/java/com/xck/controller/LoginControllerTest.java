package com.xck.controller;

import com.alibaba.fastjson.JSONObject;
import com.xck.model.ReqLogin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception{
        ReqLogin reqLogin = new ReqLogin();
        reqLogin.setUserName("xck123");
        reqLogin.setPwd("123456");
        this.mockMvc.perform(get("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JSONObject.toJSONString(reqLogin)))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testIOSToUTF8() throws Exception{
        System.out.println(new String("请求成功".getBytes(), "GB2312"));
        System.out.println(new String(new String("请求成功".getBytes(), "ISO8859_1").getBytes(), "UTF-8"));
        System.out.println(new String("请求成功".getBytes("GB2312")));
        System.out.println(new String("请求成功".getBytes("GB2312"), "GB2312"));
        System.out.println(new String("请求成功".getBytes("GB2312"), "ISO8859_1"));
        System.out.println(new String("请求成功".getBytes("ISO8859_1")));
        System.out.println(new String("请求成功".getBytes("ISO8859_1"), "GB2312"));
        System.out.println(new String("请求成功".getBytes("ISO8859_1"), "ISO8859_1"));
    }
}