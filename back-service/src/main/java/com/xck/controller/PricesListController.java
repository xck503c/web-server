package com.xck.controller;

import com.xck.model.req.ReqPricesRecord;
import com.xck.model.req.ReqResp;
import com.xck.model.req.RespCode;
import org.springframework.web.bind.annotation.*;

@RestController
public class PricesListController {

    @GetMapping(value = "/prices/create")
    @ResponseBody
    public ReqResp create(@PathVariable("foodId") Long foodId, @RequestBody ReqPricesRecord reqPricesRecord) {

        return new ReqResp(RespCode.SUCCESS, null);
    }
}
