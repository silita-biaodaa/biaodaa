package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.UserRoleBdd;
import com.silita.biaodaa.utils.MyMapper;

public interface UserRoleBddMapper extends MyMapper<UserRoleBdd> {
    void insertUserRole(String userid);
}