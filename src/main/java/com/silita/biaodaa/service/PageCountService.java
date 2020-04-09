package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPageCountMapper;
import com.silita.biaodaa.model.TbPageCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhushuai on 2020/4/9.
 */
@Service
public class PageCountService {

    @Autowired
    private TbPageCountMapper tbPageCountMapper;

    /**
     * 保存页面统计
     *
     * @param page        页面url
     * @param description 页面描述
     */
    public void savePageCount(String page, String description) {
        TbPageCount pageCount = new TbPageCount();
        pageCount.setPage(page);
        pageCount.setDescription(description);
        Integer pkid = tbPageCountMapper.queryPkidPage(page);
        if (null != pkid && pkid > 0) {
            tbPageCountMapper.updatePageCount(pkid);
            return;
        }
        pageCount.setPageCount(1);
        tbPageCountMapper.insertPageCount(pageCount);
    }

}
