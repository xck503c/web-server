package com.xck.controller;

import com.xck.model.req.ReqLogin;
import com.xck.model.req.RespCode;
import com.xck.model.req.ReqResp;
import com.xck.model.UserInfo;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @GetMapping(value = "/login")
    @ResponseBody
    public ReqResp login(@RequestBody ReqLogin reqLogin) {
        System.out.println(reqLogin);
        UserInfo info = new UserInfo();
        info.setId(1);
        info.setUserName("xck");
        info.setEmail("123@14.com");
        info.setMobile("123456789");

        return new ReqResp<UserInfo>(RespCode.SUCCESS, info);
    }
}
