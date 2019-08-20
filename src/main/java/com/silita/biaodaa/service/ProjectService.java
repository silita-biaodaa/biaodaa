package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ObjectUtils;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.RedisConstantInterface.PROJECT_LIST;
import static com.silita.biaodaa.common.RedisConstantInterface.PRO_OVER_TIME;

@Service
public class ProjectService {

    @Autowired
    TbProjectMapper tbProjectMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    TbProjectContractMapper tbProjectContractMapper;
    @Autowired
    TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    TbProjectZhaotoubiaoMapper tbProjectZhaotoubiaoMapper;
    @Autowired
    TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    TbProjectCompanyMapper tbProjectCompanyMapper;
    @Autowired
    MyRedisTemplate myRedisTemplate;
    @Autowired
    ProjectCompletionService projectCompletionService;
    @Autowired
    TbProjectShuiliMapper tbProjectShuiliMapper;
    @Autowired
    TbProjectTrafficMapper tbProjectTrafficMapper;
    @Autowired
    TbTrafficPersonMapper tbTrafficPersonMapper;
    @Autowired
    TbProjectCompletionMapper tbProjectCompletionMapper;

    private static ProjectAnalysisUtil projectAnalysisUtil = new ProjectAnalysisUtil();

    /**
     * 按工程查询
     *
     * @param param
     * @param result
     * @return
     */
    public Map<String, Object> getProjectList(Map<String, Object> param, Map<String, Object> result) {
        Integer pageNum = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        if (null != param.get("proType") && "房屋建筑".equals(param.get("proType"))) {
            param.put("proType", "房屋建筑工程");
        }
        String cacheKey = ObjectUtils.buildCacheKey(PROJECT_LIST, param);
        PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(cacheKey);
        if (null == pageInfo) {
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNum);
            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
            List<Map<String, Object>> projectList = tbProjectMapper.queryObject(param);
            if (null != projectList && projectList.size() > 0) {
                for (Map<String, Object> map : projectList) {
                    this.putProvince(map);
                }
                pageInfo = new PageInfo(projectList);
                myRedisTemplate.setObject(cacheKey, pageInfo, PRO_OVER_TIME);
            }
        }
        if (null != pageInfo) {
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        }
        return result;
    }

    /**
     * 水利业绩
     *
     * @param param
     * @return
     */
    public PageInfo getProjectShuiliList(Map<String, Object> param) {
        Integer pageNo = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setCurrentPage(pageNo);
        page.setPageSize(pageSize);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = tbProjectShuiliMapper.queryShuiliProjectList(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 交通业绩
     *
     * @param param
     * @return
     */
    public PageInfo getProjectJiaoList(Map<String, Object> param) {
        Integer pageNo = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setCurrentPage(pageNo);
        page.setPageSize(pageSize);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = tbProjectTrafficMapper.queryProjectList(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }


    /**
     * 获取项目详情
     * created by zhushuai
     *
     * @param proId  项目id
     * @param result 返回result
     * @return
     */
    public Map<String, Object> getProjectDetail(String proId, Map<String, Object> result) {
        TbProject project = tbProjectMapper.queryProjectDetail(proId);
        if (null == project.getAcreage()) {
            if (null != project.getProScope()) {
                project.setAcreage(projectAnalysisUtil.analysisData(projectAnalysisUtil.regx, project.getProScope()));
            }
        }
        if (null != project.getInvestAmount() && project.getInvestAmount().contains("（万元）")) {
            project.setInvestAmount(project.getInvestAmount().substring(0, projectAnalysisUtil.getIndex(project.getInvestAmount(), "（")));
        }
        project.setAcreage(projectAnalysisUtil.getNumber(project.getAcreage()));
        result.put("data", project);
        return result;
    }

    /**
     * 获取水利详情
     * created by zhushuai
     *
     * @param proId  项目id
     * @param result 返回result
     * @return
     */
    public Map<String, Object> getProjectShuiliDetail(String proId, Map<String, Object> result) {
        TbProjectShuili shuili = tbProjectShuiliMapper.queryShuiliDetail(proId);
        if (null == shuili) {
            return result;
        }
        if (MyStringUtils.isNotNull(shuili.getPersons())) {
            List<Map<String, Object>> personList = new ArrayList<>();
            List<Map<String, Object>> persons = (List<Map<String, Object>>) JSONObject.parse(shuili.getPersons());
            if (null != persons && persons.size() > 0) {
                for (Map<String, Object> map : persons) {
                    if (null != map.get("职务") && ("项目负责人".equals(map.get("职务")) || "技术负责人".equals(map.get("职务")))) {
                        personList.add(map);
                    }
                }
            }
            shuili.setLeaderPersons(personList);
            shuili.setMainPersons(persons);
            shuili.setPersons(null);
        }
        if (MyStringUtils.isNotNull(shuili.getPrizes())) {
            shuili.setProPrizes((List<Map<String, Object>>) JSONObject.parse(shuili.getPrizes()));
            shuili.setPrizes(null);
        }
        result.put("data", shuili);
        return result;
    }

    /**
     * 获取交通详情
     * created by zhushuai
     *
     * @param proId  项目id
     * @param result 返回result
     * @return
     */
    public Map<String, Object> getProjectJiaoDetail(String proId, Map<String, Object> result) {
        TbProjectTraffic projectTraffic = tbProjectTrafficMapper.queryProjectDetail(proId);
        if (null != projectTraffic) {
            projectTraffic.setPersons(tbTrafficPersonMapper.queryPersonListByProId(proId));
        }
        result.put("data", projectTraffic);
        return result;
    }

    /**
     * tab详情
     * created by zhushuai
     *
     * @param params
     * @param result
     * @return
     */
    public Map<String, Object> getTabProjectDetail(Map<String, Object> params, Map<String, Object> result) {
        String proId = MapUtils.getString(params, "proId");
        String tabType = MapUtils.getString(params, "tabType");
        List resultList = new ArrayList();
        PageInfo pageInfo = null;
        //施工图审查
        if ("design".equals(tabType)) {
            pageInfo = getProjectDesignByPage(params);
        } else if ("contract".equals(tabType)) {
            pageInfo = getContractListByPages(params);
        } else if ("zhaotoubiao".equals(tabType)) {
            pageInfo = getZhaotoubiaoListByPage(params);
        } else if ("build".equals(tabType)) {
            pageInfo = getProjectBuildDetailPages(params);
        } else if ("completion".equals(tabType)) {
            pageInfo = projectCompletionService.getProjectCompletListPages(params);
        }
        if (null != pageInfo && (null != pageInfo.getList() && pageInfo.getList().size() > 0)) {
            result.put("data", pageInfo.getList());
            result.put("pages", pageInfo.getPages());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("total", pageInfo.getTotal());
            return result;
        }

        result.put("data", resultList);
        if (null != resultList && resultList.size() > 0) {
            result.put("total", resultList.size());
        }
        return result;
    }


    /**
     * 获取项目下的合同备案列表(分页)
     * created by zhushuai
     *
     * @param params
     * @return
     */
    private PageInfo getContractListByPages(Map<String, Object> params) {
        Integer pageIndex = MapUtils.getInteger(params, "pageNo");
        Integer pageSize = MapUtils.getInteger(params, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbProjectContract> list = this.getContrList(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 招投标列表（分页）
     *
     * @param param
     * @return
     */
    private PageInfo getZhaotoubiaoListByPage(Map<String, Object> param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        String proId = MapUtils.getString(param, "proId");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbProjectZhaotoubiao> list = tbProjectZhaotoubiaoMapper.queryZhaotoubiaoListByProId(proId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 施工许可（分页）
     *
     * @param params
     * @return
     */
    private PageInfo getProjectBuildDetailPages(Map params) {
        Integer pageIndex = MapUtils.getInteger(params, "pageNo");
        Integer pageSize = MapUtils.getInteger(params, "pageSize");
        String proId = MapUtils.getString(params, "proId");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbProjectBuild> proBuildList = tbProjectBuildMapper.queryProjectBuildByProId(proId);
        PageInfo pageInfo = new PageInfo(proBuildList);
        return pageInfo;
    }

    /**
     * 施工图审查（分页）
     *
     * @param param
     * @return
     */
    private PageInfo getProjectDesignByPage(Map param) {
        Integer pageIndex = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        String proId = MapUtils.getString(param, "proId");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageIndex);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbProjectDesign> list = tbProjectDesignMapper.queryProjectDesignByProId(proId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 获取项目下的合同备案
     * created by zhushuai
     *
     * @param params
     * @return
     */
    private List<TbProjectContract> getContrList(Map<String, Object> params) {
        String proId = MapUtils.getString(params, "proId");
        List<TbProjectContract> list = tbProjectContractMapper.queryProjectContractListByProId(proId);
        if (null != list && list.size() > 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("proId", proId);
            List<String> roleList = new ArrayList<>();
            roleList.add("发包单位");
            roleList.add("承包单位");
            param.put("roleList", roleList);
            param.put("type", "contract");
            List<TbProjectCompany> projectCompanyList = null;
            for (TbProjectContract projectContract : list) {
                param.put("pid", projectContract.getPkid());
                projectCompanyList = tbProjectCompanyMapper.queryProComList(param);
                if (null != projectCompanyList && projectCompanyList.size() > 0) {
                    for (TbProjectCompany company : projectCompanyList) {
                        if ("发包单位".equals(company.getRole())) {
                            projectContract.setLetContractComName(company.getComName());
                        } else if ("承包单位".equals(company.getRole())) {
                            projectContract.setContractComName(company.getComName());
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 将省信息放入集合中
     * created by zhushuai
     *
     * @param param
     */
    private void putProvince(Map<String, Object> param) {
        if (null != param.get("proWhere")) {
            String proWhere = param.get("proWhere").toString();
            param.put("province", projectAnalysisUtil.getProvince(proWhere));
        }
    }

    public Map<String, Object> getProjectCompanyList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        String companyId = MapUtils.getString(param, "comId");
        String comName = MapUtils.getString(param, "comName");
        // TODO:获取公司信息
        TbCompany company = tbCompanyMapper.getCompany(companyId);
        if (StringUtils.isEmpty(comName) && null != company) {
            comName = company.getComName();
        }
        if (null != company && null != company.getUrl()) {
            String url = company.getUrl();
            param.put("innerId", url.substring(url.lastIndexOf("/") + 1, url.length()));
        }
        param.put("comName", comName);

        List<Map<String, Object>> resultList = tbProjectMapper.queryListCompanyPro(param);

        List<Map<String, Object>> resList = resultList;
        if (null != resultList && resultList.size() > 0) {
            if ("page".equals(param.get("type"))) {
                Integer pageNo = MapUtils.getInteger(param, "pageNo");
                Integer pageSize = MapUtils.getInteger(param, "pageSize");
                Integer pages = CommonUtil.getPages(resultList.size(), pageSize);
                Integer start = (pageNo - 1) * pageSize;
                Integer end = resultList.size();
                if (pageNo < pages) {
                    end = start + pageSize;
                }
                resList = resultList.subList(start, end);
                resultMap.put("total", resultList.size());
                resultMap.put("pages", pages);
            }
            for (Map<String, Object> map : resList) {
                this.putProvince(map);
            }
        }
        resultMap.put("data", resList);
        return resultMap;
    }

    public Map<String, Object> getProjectDetailCount(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        //施工图审查
        Integer desinCount = tbProjectDesignMapper.queryProjectDesignCount(param);
        if (null == desinCount) {
            desinCount = 0;
        }
        resultMap.put("desin", desinCount);
        resultMap.put("contract", tbProjectContractMapper.queryProjectConstractCount(param));
        resultMap.put("zhaotoubiao", tbProjectZhaotoubiaoMapper.queryProjectZhaotoubiaoCount(param));
        resultMap.put("build", tbProjectBuildMapper.queryProjectBuildCount(param));
        resultMap.put("completion", tbProjectCompletionMapper.queryProjectCompletionCount(param));
        return resultMap;
    }

    public void analysisData() {
        List<Map<String, Object>> paramList = new ArrayList<>();
        List<Map<String, Object>> proList = tbProjectMapper.queryObject(null);
        for (Map<String, Object> map : proList) {
            if (null != map.get("scope")) {
                String scope = map.get("scope").toString();
                map.put("acreage", projectAnalysisUtil.analysisData(projectAnalysisUtil.regx, scope));
                paramList.add(map);
            }
        }
        //修改
        System.out.println("size=================" + paramList.size());
        for (Map<String, Object> resMap : paramList) {
            System.out.println(resMap.get("scope") + "----------------------" + resMap.get("acreage"));
            if (null != resMap.get("acreage")) {
                if (projectAnalysisUtil.isNumeric(resMap.get("acreage").toString())) {
                    tbProjectMapper.update(resMap);
                }
            }
        }
    }
}
