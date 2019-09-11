package com.silita.biaodaa.dao;

import java.util.Map;

/**
 * 活动内容 tb_activity_content
 */
public interface TbActivityContentMapper {

    /**
     * 查询近期是否有活动
     * @return
     */
    Map<String,Object> queryActivity();

}