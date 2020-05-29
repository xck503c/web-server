package com.xck.controller;

import com.alibaba.fastjson.JSONObject;
import com.xck.model.req.ReqPricesRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FoodControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception{
        ReqPricesRecord reqPricesRecord = new ReqPricesRecord();
        reqPricesRecord.setId(1);
        reqPricesRecord.setName("鱼");
        reqPricesRecord.setPrice(1.0);
        reqPricesRecord.setRecordTime("");
        reqPricesRecord.setUnit("斤");
        this.mockMvc.perform(get("/food/1/record")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JSONObject.toJSONString(reqPricesRecord)))
                .andDo(print()).andExpect(status().isOk());
    }
}