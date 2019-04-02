package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.PlatformNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台公示
 */
@RequestMapping("/platform/notice")
@RestController
public class PlatformNoticeController extends BaseController {

    @Autowired
    PlatformNoticeService platformNoticeService;

    /**
     * TODO: 平台列表
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getPlatformNoticeList(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        if (null == param.get("type")){
            param.put("type",1);
        }else {
            param.remove("type");
        }
        return platformNoticeService.getPlatformNoticeList(param, resultMap);
    }

}
