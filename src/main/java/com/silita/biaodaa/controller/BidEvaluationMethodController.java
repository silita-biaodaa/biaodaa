package com.silita.biaodaa.controller;

import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.service.BidEvaluationMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 评标办法
 */
@RequestMapping("/bid")
@Controller
public class BidEvaluationMethodController extends BaseController {

    @Autowired
    BidEvaluationMethodService bidEvaluationMethodService;
    @Autowired
    ElasticseachService elasticseachService;

    /**
     * TODO:项目名称检索
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filter", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> filter(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", bidEvaluationMethodService.getSnatchUrlList(params));
        return successMap(resultMap);
    }

    /**
     * TODO: 获取多标段
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list(@RequestBody Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", bidEvaluationMethodService.getNoticeDetailList(params));
        return successMap(resultMap);
    }


    /**
     * TODO: 封装成功code
     *
     * @param resultMap
     * @return
     */
    private Map<String, Object> successMap(Map<String, Object> resultMap) {
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        return resultMap;
    }

    /**
     * TODO:获取信用等级最新年份
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/ration", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getRation() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", bidEvaluationMethodService.getReaYear());
        return successMap(resultMap);
    }

    /**
     * TODO:评标计算
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/bidCompute", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> bidCompute(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> dataMap = bidEvaluationMethodService.bidCompute(param);
            resultMap.put("data", dataMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successMap(resultMap);
    }

    /**
     * TODO:评标历史列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/record/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", bidEvaluationMethodService.getBidComputerList());
        return successMap(resultMap);
    }

    /**
     * TODO:删除评标历史
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/del", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> delBidComputer(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        bidEvaluationMethodService.delBidComputer(param);
        return successMap(resultMap);
    }

    /**
     * TODO:结果详情
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> detail(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap.put("data", bidEvaluationMethodService.getBidCoDetail(param));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return successMap(resultMap);
    }

    /**
     * TODO:评标结果
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/result", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> result(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", bidEvaluationMethodService.getBidResult(param));
        return successMap(resultMap);
    }

}
