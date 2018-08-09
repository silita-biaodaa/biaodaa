package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbProjectCompanyMapper;
import com.silita.biaodaa.dao.TbProjectCompletionMapper;
import com.silita.biaodaa.dao.TbProjectContractMapper;
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
    public PageInfo getProjectCompletList(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        pageIndex = pageIndex == null ? 1 : pageIndex;
        pageSize = pageSize == null ? 20 : pageSize;

        Page page = new Page();
        page.setCurrentPage(pageIndex);
        page.setPageSize(pageSize);

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        return new PageInfo(tbProjectCompletionMapper.queryProCompleList(param));
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
