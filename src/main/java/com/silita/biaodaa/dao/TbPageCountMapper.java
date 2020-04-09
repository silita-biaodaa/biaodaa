package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPageCount;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPageCountMapper extends MyMapper<TbPageCount> {

    /**
     * 添加页面统计
     * @param pageCount
     */
    void insertPageCount(TbPageCount pageCount);

    /**
     * 修改页面统计
     */
    void updatePageCount(int pkid);

    /**
     * 根据页面查询主键
     * @param page
     * @return
     */
    Integer queryPkidPage(String page);
}