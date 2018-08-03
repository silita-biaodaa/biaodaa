package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.UserRoleBdd;
import com.silita.biaodaa.utils.MyMapper;

/**
 * user_role_bdd Mapper
 */
public interface UserRoleBddMapper extends MyMapper<UserRoleBdd> {

    /**
     * 添加
     *
     * @param userid
     */
    void insertUserRole(String userid);
}