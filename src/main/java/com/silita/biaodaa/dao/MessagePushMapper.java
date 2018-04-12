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
}