package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.GradeService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/grade")
@Controller
public class GradeController {

    @Autowired
    GradeService gradeService;

    @ResponseBody
    @RequestMapping(value = "/filter", method = RequestMethod.POST,produces = "application/json")
    public Map<String,Object> filtre(@RequestBody Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "筛选条件查询失败!");
        Integer type = MapUtils.getInteger(params,"type");
        try {
            result.put("data",gradeService.getQual(type));
            result.put("code", 1);
            result.put("msg", "筛选条件查询成功!");
        } catch (IllegalArgumentException e) {
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
}
