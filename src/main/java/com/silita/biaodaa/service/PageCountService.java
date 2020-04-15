package com.silita.biaodaa.service;

import com.silita.biaodaa.common.SecurityCheck;
import com.silita.biaodaa.dao.TbPageCountMapper;
import com.silita.biaodaa.dao.TbPageInfoMapper;
import com.silita.biaodaa.model.TbPageCount;
import com.silita.biaodaa.model.TbPageInfo;
import com.silita.biaodaa.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhushuai on 2020/4/9.
 */
@Service
public class PageCountService {

    @Autowired
    private TbPageCountMapper tbPageCountMapper;
    @Autowired
    private TbPageInfoMapper tbPageInfoMapper;

    /**
     * 保存页面统计
     *
     * @param page        页面url
     * @param description 页面描述
     */
    public void savePageCount(String page, String description, HttpServletRequest request) {
        TbPageCount pageCount = new TbPageCount();
        pageCount.setPage(page);
        pageCount.setDescription(description);
        Integer pkid = tbPageCountMapper.queryPkidPage(page);
        if (null != pkid && pkid > 0) {
            tbPageCountMapper.updatePageCount(pkid);
            //明细
            this.savePageInfo(page, description, request);
            return;
        }
        pageCount.setPageCount(1);
        tbPageCountMapper.insertPageCount(pageCount);
        //明细
        this.savePageInfo(page, description, request);
    }

    /**
     * 保存明细
     *
     * @param page
     * @param description
     * @param request
     */
    private void savePageInfo(String page, String description, HttpServletRequest request) {
        TbPageInfo pageInfo = new TbPageInfo();
        pageInfo.setPage(page);
        pageInfo.setDescription(description);
        pageInfo.setIp(HttpUtils.getIp(request));
        pageInfo.setInfo(SecurityCheck.getHeaderValue(request, "baseInfo"));
        tbPageInfoMapper.insertPageInfo(pageInfo);
    }
}
