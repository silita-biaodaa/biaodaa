package com.silita.biaodaa.dao;

import java.util.Map;

/**
 * 订阅 tb_user_subscribe
 */
public interface TbUserSubscribeMapper {

    /**
     * 修改是否推送(1:推送，0:不推送)
     * @param userId
     * @return
     */
    int updateIsPub(String userId);

    /**
     * 查询用户最新的订阅条件(根据主键id和userId)
     * @param param
     * @return
     */
    Map<String,Object> queryNewCondition(Map<String,Object> param);

    /**
     * 添加用户订阅条件
     * @param userSubsribe
     * @return
     */
    int insertUserSubscribe(Map<String,Object> userSubsribe);
}