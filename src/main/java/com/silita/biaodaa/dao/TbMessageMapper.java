package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbMessage;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_message Mapper
 */
public interface TbMessageMapper extends MyMapper<TbMessage> {

    /**
     * 我的消息列表
     * @param param
     * @return
     */
    List<Map<String,Object>> queryMessageList(Map<String,Object> param);

    /**
     * 添加消息
     * @return
     */
    int insert(TbMessage message);

    /**
     * 删除多条记录
     * @param param
     * @return
     */
    int deleteMsg(Map<String,Object> param);

    /**
     * 设置消息已读状态
     * @param param
     * @return
     */
    int setIsRead(Map<String,Object> param);

    /**
     * 查询未读消息个数
     * @return
     */
    int queryIsReadCount(Map<String,Object> param);
}