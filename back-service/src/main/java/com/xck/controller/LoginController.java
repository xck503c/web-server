package com.xck.controller;

import com.xck.model.ReqLogin;
import com.xck.model.RespCode;
import com.xck.model.RespEntity;
import com.xck.model.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class LoginController {

    @PostMapping(value = "/login")
    public RespEntity login(@RequestBody ReqLogin reqLogin) {
        System.out.println(reqLogin);
        UserInfo info = new UserInfo();
        info.setId(1);
        info.setUserName("xck");
        info.setEmail("123@14.com");
        info.setMobile("123456789");

        return new RespEntity(RespCode.SUCCESS, info);
    }
}
