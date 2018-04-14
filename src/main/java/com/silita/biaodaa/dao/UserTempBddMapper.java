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
}