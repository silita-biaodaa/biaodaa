package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.MessageService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhushuai on 2019/5/24.
 */
@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    MessageService messageService;

    /**
     * 评论列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result);
        param.put("userId",VisitInfoHolder.getUid());
        buildReturnMap(result, messageService.listMessage(param));
        return result;
    }

    /**
     * 删除消息
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/del", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> del(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result);
        param.put("userId", VisitInfoHolder.getUid());
        messageService.delMsg(param);
        return result;
    }

    /**
     * 设置已读
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/set/read", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> setRead(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap();
        successMsg(result);
        messageService.setIsRead(param);
        return result;
    }

    /**
     * 未读消息个数
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/count/unread", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> countUnread() {
        Map<String, Object> result = new HashedMap();
        successMsg(result,messageService.getIsReadCount());
        return result;
    }
}
