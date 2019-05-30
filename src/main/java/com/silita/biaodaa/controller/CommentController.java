package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.CommentService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/22.
 */
@Controller
@RequestMapping("comment")
public class CommentController extends BaseController {

    @Autowired
    CommentService commentService;

    /**
     * 评论列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = checkParam(param);
        if (MapUtils.isNotEmpty(result)) {
            return result;
        }
        result = new HashedMap();
        successMsg(result);
        buildReturnMap(result, commentService.listComment(param));
        return result;
    }

    /**
     * 与我相关评论列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list/user", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> listUser(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = checkParam(param);
        if (MapUtils.isNotEmpty(result)) {
            return result;
        }
        result = new HashedMap();
        String userId = VisitInfoHolder.getUid();
        param.put("userId", userId);
        successMsg(result);
        buildReturnMap(result, commentService.listUserComment(param));
        return result;
    }

    /**
     * 单个评论
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/single", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> single(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result, commentService.singleComment(param));
        return result;
    }

    /**
     * 评论
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> push(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = checkParam(param);
        if (MapUtils.isNotEmpty(result)) {
            return result;
        }
        String userId = VisitInfoHolder.getUid();
        param.put("userId", userId);
        return commentService.pushComment(param);
    }

    /**
     * 校验参数
     *
     * @param param
     * @return
     */
    private Map<String, Object> checkParam(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        if (("zhongbiao").equals(param.get("relatedType")) || ("zhaobiao").equals(param.get("relatedType"))) {
            if (null == param.get("source")) {
                result.put("code", Constant.ERR_VIEW_CODE);
                result.put("msg", "参数source不能为空！");
                return result;
            }
        }
        return null;
    }
}