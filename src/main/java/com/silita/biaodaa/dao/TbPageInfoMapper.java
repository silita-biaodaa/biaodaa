package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPageInfo;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPageInfoMapper extends MyMapper<TbPageInfo> {

    /**
     * 添加页面统计详情
     * @param pageInfo
     */
    void insertPageInfo(TbPageInfo pageInfo);

}