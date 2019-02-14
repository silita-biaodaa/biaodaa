package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.dao.TbProjectCompanyMapper;
import com.silita.biaodaa.dao.TbProjectCompletionMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbProjectCompany;
import com.silita.biaodaa.model.TbProjectCompletion;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectCompletionService {

    @Autowired
    TbProjectCompletionMapper tbProjectCompletionMapper;
    @Autowired
    TbProjectCompanyMapper tbProjectCompanyMapper;

    /**
     * 获取竣工备案列表
     *
     * @param param
     * @return
     */
    public List<TbProjectCompletion> getProjectCompletList(Map<String, Object> param) {
        return tbProjectCompletionMapper.queryProCompleList(param);
    }

    /**
     * 获取竣工备案列表
     *
     * @param param
     * @return
     */
    public PageInfo getProjectCompletListPages(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param,"pageNo");
        Integer pageSize = MapUtils.getInteger(param,"pageSize");
        String proId = MapUtils.getString(param, "proId");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbProjectCompletion> list = tbProjectCompletionMapper.queryProCompleList(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 获取竣工备案详情
     *
     * @param param
     * @return
     */
    public TbProjectCompletion getProCompletDetail(Map<String, Object> param) {
        TbProjectCompletion projectCompletion = tbProjectCompletionMapper.queryProCompleDetail(param);
        if (null == projectCompletion) {
            return projectCompletion;
        }

        //TODO: 企业信息
        param.put("pid",param.get("pkid"));
        param.put("type","completion");
        List<TbProjectCompany> companyList = tbProjectCompanyMapper.queryProComList(param);
        projectCompletion.setCompanys(companyList);
        return projectCompletion;
    }
}
