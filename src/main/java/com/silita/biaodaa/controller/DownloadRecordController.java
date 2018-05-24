package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.DownloadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/record")
@Controller
public class DownloadRecordController extends BaseController{

    @Autowired
    DownloadRecordService downloadRecordService;

    /**
     * 记录下载次数
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/downRecord", method = RequestMethod.POST,produces = "application/json")
    public Map<String,Object> downRecord(@RequestBody Map<String,Object> param){
        Map<String,Object> resultMap = new HashMap<>();
        successMsg(resultMap);
        resultMap.put("data",downloadRecordService.downloadRecord(param));
        return resultMap;
    }
}
