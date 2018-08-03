package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.MyMapper;

/**
 * user_temp_bdd Mapper
 */
public interface UserTempBddMapper extends MyMapper<UserTempBdd> {

    /**
     * 获取用户信息
     *
     * @param userPhone
     * @return
     */
    UserTempBdd getUserByUserPhone(String userPhone);

    /**
     * 添加
     *
     * @param userTempBdd
     */
    void InsertUserTemp(UserTempBdd userTempBdd);

    /**
     * 获取用户信息
     *
     * @return
     */
    UserTempBdd getUserByUserNameOrPhoneAndPassWd(UserTempBdd userTempBdd);

    /**
     * 修改WX
     *
     * @param userTempBdd
     */
    void updateUserTempByWxBind(UserTempBdd userTempBdd);

    /**
     * 修改QQ
     *
     * @param userTempBdd
     */
    void updateUserTempByQQBind(UserTempBdd userTempBdd);

    /**
     * 获取WXid
     *
     * @param wxUnionid
     * @return
     */
    UserTempBdd getUserTempByWXUnionId(String wxUnionid);

    /**
     * 获取WXopenId
     *
     * @param wxopenid
     * @return
     */
    UserTempBdd getUserTempByWXOpenId(String wxopenid);

    /**
     * 修改
     *
     * @param userTempBdd
     */
    void updateWXUnionIdByWXOpenId(UserTempBdd userTempBdd);

    /**
     * 获取QQopenid
     *
     * @param qqopenid
     * @return
     */
    UserTempBdd getUserTempByQQOpenId(String qqopenid);

    /**
     * 获取用户信息
     *
     * @param userid
     * @return
     */
    UserTempBdd getUserTempByUserId(String userid);

    /**
     * 修改
     *
     * @param userTempBdd
     */
    void updateUserTemp(UserTempBdd userTempBdd);


    /**
     * 修改密码
     *
     * @param userTempBdd
     */
    void updatePassWdByUserIdAndPhone(UserTempBdd userTempBdd);

    /**
     * 获取总数
     *
     * @param userphone
     * @return
     */
    Integer getTotalByUserPhone(String userphone);
}