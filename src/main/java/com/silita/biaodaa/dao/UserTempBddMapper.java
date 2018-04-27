package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.MyMapper;

public interface UserTempBddMapper extends MyMapper<UserTempBdd> {
    /**
     *
     * @param userPhone
     * @return
     */
    UserTempBdd getUserByUserPhone(String userPhone);

    /**
     *
     * @param userTempBdd
     */
    void InsertUserTemp(UserTempBdd userTempBdd);

    /**
     *
     * @return
     */
    UserTempBdd getUserByUserNameOrPhoneAndPassWd(UserTempBdd userTempBdd);

    /**
     *
     */
    void updateUserTempByWxBind(UserTempBdd userTempBdd);

    /**
     *
     * @param userTempBdd
     */
    void updateUserTempByQQBind(UserTempBdd userTempBdd);

    /**
     *
     * @param wxUnionid
     * @return
     */
    UserTempBdd getUserTempByWXUnionId(String wxUnionid);

    /**
     *
     * @param wxopenid
     * @return
     */
    UserTempBdd getUserTempByWXOpenId(String wxopenid);

    /**
     *
     * @param userTempBdd
     */
    void updateWXUnionIdByWXOpenId(UserTempBdd userTempBdd);

    /**
     *
     * @param qqopenid
     * @return
     */
    UserTempBdd getUserTempByQQOpenId(String qqopenid);

    /**
     *
     * @param userid
     * @return
     */
    UserTempBdd getUserTempByUserId(String userid);

    /**
     *
     * @param userTempBdd
     */
    void updateUserTemp(UserTempBdd userTempBdd);


    /**
     *
     * @param userTempBdd
     */
    void updatePassWdByUserIdAndPhone(UserTempBdd userTempBdd);

    /**
     *
     */
    Integer getTotalByUserPhone(String userphone);
}