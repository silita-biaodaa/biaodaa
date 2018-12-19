package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbLoginInfo;
import com.silita.biaodaa.utils.MyMapper;

/**
 * tb_login_info Mapper
 */
public interface TbLoginInfoMapper extends MyMapper<TbLoginInfo> {

    /**
     * 删除信息
     * @param tel
     * @return
     */
    int queryCount(String tel);

    /**
     * l
     * @param loginInfo
     * @return
     */
    int insertLoginInfo(TbLoginInfo loginInfo);
}