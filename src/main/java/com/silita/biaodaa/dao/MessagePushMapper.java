package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.MessagePush;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface MessagePushMapper extends MyMapper<MessagePush> {
    /**
     *
     * @param params
     * @return
     */
    List<MessagePush> listMessageByUserIdAndType(Map<String, Object> params);

    /**
     *
     * @param params
     * @return
     */
    List<MessagePush> listMessage(Map<String, Object> params);

    /**
     *
     * @return
     */
    Integer getNewMsgByUserId(Map<String, Object> params);

    /**
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> listIdByUserId(Map<String, Object> params);

    /**
     *
     * @return
     */
    MessagePush getMessageById(String id);

}