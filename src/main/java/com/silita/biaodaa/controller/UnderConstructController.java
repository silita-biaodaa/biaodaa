package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.UnderConstructService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/under")
public class UnderConstructController extends BaseController {

    @Autowired
    private UnderConstructService underConstructService;

    /**
     * 列表
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> list(@RequestBody Map<String, Object> param) {
        return underList(param);
    }

    Map<String, Object> underList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        successMsg(resultMap);
        buildReturnMap(resultMap, underConstructService.listUnderConstruct(param));
        return resultMap;
    }

    Map<String, Object> underQuery(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        List list;
        if ("detail".equals(param.get("type"))) {
            list = underConstructService.getUnderConstruct(param);
            successMsg(resultMap);
            resultMap.put("data", list);
            return resultMap;
        }
        if (MyStringUtils.isNull(param.get("name"))) {
            resultMap.put(CODE_FLAG, 0);
            resultMap.put(MSG_FLAG, "参数name不可为空!");
            return resultMap;
        }
        if (null != param.get("idCard")) {
            String idCard = param.get("idCard").toString();
            if (idCard.length() != 15 && idCard.length() != 18) {
                resultMap.put(CODE_FLAG, 0);
                resultMap.put(MSG_FLAG, "身份证长度不正确!");
                return resultMap;
            }
            if (idCard.length() == 15) {
                if (!ProjectAnalysisUtil.isNumeric(idCard)) {
                    resultMap.put(CODE_FLAG, 0);
                    resultMap.put(MSG_FLAG, "必须是纯数字!");
                    return resultMap;
                }
            } else if (idCard.length() == 18) {
                if (!ProjectAnalysisUtil.isNumeric(idCard.substring(0, 17))) {
                    resultMap.put(CODE_FLAG, 0);
                    resultMap.put(MSG_FLAG, "前17位必须是纯数字!");
                    return resultMap;
                }
                if (!ProjectAnalysisUtil.isNumeric(idCard.substring(idCard.length() - 1))) {
                    if (!idCard.endsWith("X")) {
                        resultMap.put(CODE_FLAG, 0);
                        resultMap.put(MSG_FLAG, "第18位必须是纯数字或X!");
                        return resultMap;
                    }
                }
            }
        }
        list = underConstructService.getApiUnderConstruct(param);
        successMsg(resultMap);
        resultMap.put("data", list);
        return resultMap;
    }

    /**
     * 查询
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> query(@RequestBody Map<String, Object> param) {
        return underQuery(param);
    }
}
