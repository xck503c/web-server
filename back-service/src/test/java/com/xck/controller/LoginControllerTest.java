package com.xck.controller;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
class LoginControllerTest {
    private MockMvc mvc;

    @Before
    public void setupMockMvc(){
        mvc = MockMvcBuilders.standaloneSetup(new LoginController()).build(); //初始化MockMvc对象
    }

    @Test
    void login() throws Exception{
        String responseString = mvc.perform(MockMvcRequestBuilders.post("/login")).andReturn().getResponse().getContentAsString();
        System.out.println("result : "+responseString);
    }
}