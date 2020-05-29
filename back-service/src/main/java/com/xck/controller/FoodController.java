package com.xck.controller;

import com.xck.model.req.ReqPricesRecord;
import com.xck.model.req.ReqResp;
import com.xck.model.req.RespCode;
import org.springframework.web.bind.annotation.*;

@RestController
public class FoodController {

    @GetMapping(value = "/food/{foodId}/record")
    @ResponseBody
    public ReqResp login(@PathVariable("foodId") Long foodId, @RequestBody ReqPricesRecord reqPricesRecord) {

        return new ReqResp(RespCode.SUCCESS, null);
    }
}
