package com.silita.biaodaa.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.SnatchurlMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.elastic.common.ConstantUtil;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.elastic.model.PaginationAndSort;
import com.silita.biaodaa.elastic.model.QuerysModel;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.es.BranchCompanyEs;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.service.TbCompanyInfoService;
import com.silita.biaodaa.service.TbCompanyService;
import com.silita.biaodaa.utils.CommonUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ElasticseachService {

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    SnatchurlMapper snatchurlMapper;
    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    TbCompanyInfoService tbCompanyInfoService;
    @Autowired
    TbCompanyService tbCompanyService;


    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

    private TransportClient transportClient = InitElasticseach.initClient();

    public List<Map<String, Object>> quary(Map<String, Object> param) {
        Map sort = new HashMap<String, String>();
        sort.put("px", SortOrder.DESC);
        String comName = MapUtils.getString(param, "name");
        String regisAddress = MapUtils.getString(param, "regisAddress");
        PaginationAndSort pageSort = new PaginationAndSort(1, MapUtils.getInteger(param, "count"), sort);
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "comName", comName));
        querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "comNamePy", comName));
        List<QuerysModel> querys1 = new ArrayList();
        if (null != regisAddress) {
            querys1.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_WILDCARD, "regisAddress", regisAddress));
        }
        SearchResponse response = nativeElasticSearchUtils.complexQuery(transportClient, "company", "comes", querys1, querys, ConstantUtil.CONDITION_MUST, pageSort);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = null;
        for (SearchHit hit : response.getHits()) {
            map = new HashMap<>();
            map = (Map<String, Object>) JSON.parse(hit.getSourceAsString());
            map.put("com_name", map.get("comName"));
            list.add(map);
        }
        return list;
    }

    public Integer getPage(Integer total, Integer pageSize) {
        Integer pages = 0;
        if (total % pageSize == 0) {
            pages = total / pageSize;
        } else {
            pages = (total / pageSize) + 1;
        }
        return pages;
    }

    public List<Map<String, Object>> querySnatchUrl(Map<String, Object> param) {
        String title = MapUtils.getString(param, "projName");
        Map sort = new HashMap<String, String>();
        sort.put("openDate", SortOrder.DESC);
        PaginationAndSort pageSort = new PaginationAndSort(1, MapUtils.getInteger(param, "limit"), sort);
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "title", "*" + title + "*"));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(transportClient, "snatchurl", "snatchurl_zhaobiao", querys, null, null, pageSort);
//        nativeElasticSearchUtils.close(transportClient);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> snatMap = null;
        for (SearchHit hit : response.getHits()) {
            snatMap = new HashMap<>();
            snatMap = (Map<String, Object>) JSON.parse(hit.getSourceAsString());
            snatMap.put("id", snatMap.get("snatchId"));
            list.add(snatMap);
        }
        return list;
    }

    public void batchAddBranchCompany() {
        nativeElasticSearchUtils.createIndexAndCreateMapping(transportClient, BranchCompanyEs.class);
        Integer count = tbCompanyMapper.queryCompanyCount();
        List<Map<String, Object>> provinceList = tbPersonQualificationMapper.getProvinceList();
        if (count > 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("pageSize", 1000);
            //获取pages
            Integer pages = this.getPage(count, 1000);
            List<CompanyEs> list = null;
            BranchCompanyEs branchCompanyEs = null;
            List companyList = null;
            Map<String, Object> comMap = new HashMap<>();
            comMap.put("provinceList", provinceList);
            List<BranchCompanyEs> companyListEs = new ArrayList<>();
            for (int i = 1; i <= pages; i++) {
                param.put("page", (i - 1) * 1000);
                list = tbCompanyMapper.queryCompanyEsList(param);
                if (null != list && list.size() > 0) {
                    for (CompanyEs companyEs : list) {
                        companyListEs = new ArrayList<>();
                        comMap.put("comName", companyEs.getComName());
                        companyList = tbCompanyInfoService.esBranchCompany(comMap);
                        if (null != companyList && companyList.size() > 0) {
                            branchCompanyEs = new BranchCompanyEs();
                            branchCompanyEs.setComId(companyEs.getComId());
                            branchCompanyEs.setBranchCompanys(companyList);
                            companyListEs.add(branchCompanyEs);
                            nativeElasticSearchUtils.batchInsertDate(transportClient, "branch_company", "branch_comes", companyListEs);
                        }
                    }
                }
            }
        }
    }

    public Object queryBranchCompany(Map<String, Object> param) {
        String comId = MapUtils.getString(param, "comId");
        Integer isVip = MapUtils.getInteger(param, "isVip");
        if (null == isVip) {
            isVip = 0;
        }
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_WILDCARD, "comId", comId));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(transportClient, "branch_company", "branch_comes", querys, null, null, null);
        List<TbCompanyInfo> list = new ArrayList<>();
        if (null == response.getHits() || response.getHits().getTotalHits() <= 0) {
            return list;
        }
        String result = response.getHits().getAt(0).getSourceAsString();
        JSONObject jsonObject = JSON.parseObject(result);
        list = (List<TbCompanyInfo>) jsonObject.get("branchCompanys");

        if ("page".equals(param.get("type"))) {
            Integer pageNo = MapUtils.getInteger(param, "pageNo");
            Integer pageSize = MapUtils.getInteger(param, "pageSize");
            Integer pages = CommonUtil.getPages(Long.valueOf(response.getHits().getTotalHits()).intValue(), pageSize);
            Integer start = (pageNo - 1) * pageSize;
            Integer end = list.size();
            if (pageNo < pages) {
                end = start + pageSize;
            }
            List<TbCompanyInfo> resList = list.subList(start, end);
            Map<String, Object> resultMap = new HashedMap();
            resultMap.put("total", response.getHits().getTotalHits());
            resultMap.put("pages", pages);
            resultMap.put("data", this.setPhone(resList, isVip));
            return resultMap;
        }
        return  this.setPhone(list, isVip);
    }

    private List<Map<String,Object>> setPhone(List list, Integer isVip) {
        List<Map<String,Object>> resList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (Object object : list) {
                Map<String,Object> map = (Map<String, Object>) object;
                if (null != map.get("phone")){
                    if (1 == isVip.intValue()) {
                        map.put("phone",tbCompanyService.solPhone(MapUtils.getString(map,"phone"), "show"));
                    } else {
                        map.put("phone",tbCompanyService.solPhone(MapUtils.getString(map,"phone"), "replace"));
                    }
                }
                resList.add(map);
            }
            list = null;
        }
        return resList;
    }
}
