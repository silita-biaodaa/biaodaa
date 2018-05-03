package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbProjectMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    @Autowired
    TbProjectMapper tbProjectMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    /**
     * 按工程查询
     * @param param
     * @param result
     * @return
     */
    public Map<String,Object> getProjectList(Map<String,Object> param,Map<String,Object> result){
        Integer pageNum = MapUtils.getInteger(param,"pageNo");
        Integer pageSize = MapUtils.getInteger(param,"pageSize");
        List<Map<String,Object>> projectList = new ArrayList<Map<String,Object>>();
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNum);

        String areaCode = "";
        if (null != param.get("area")){
            areaCode = tbCompanyMapper.getAreaCode(param.get("area").toString());
        }
        param.put("areaCode",areaCode);

        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        projectList = tbProjectMapper.queryObject(param);

        PageInfo pageInfo = new PageInfo(projectList);
        result.put("data",pageInfo.getList());
        result.put("pageNum",pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("total",pageInfo.getTotal());
        result.put("pages",pageInfo.getPages());
        return result;
    }

    public Map<String,Object> getObjectListByUnit(Map<String,Object> params,Map<String,Object> result){
        Integer pageNum = MapUtils.getInteger(params,"pageNo");
        Integer pageSize = MapUtils.getInteger(params,"pageSize");

        if(null == params.get("pactType")){
            params.put("pactType","施工");
        }

        Page page = new Page();
        page.setCurrentPage(pageNum);
        page.setPageSize(pageSize);

        String areaCode = "";
        if (null != params.get("area")){
            areaCode = tbCompanyMapper.getAreaCode(params.get("area").toString());
        }
        params.put("areaCode",areaCode);

        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        List<Map<String,Object>> projectList = tbProjectMapper.queryObjectByUnit(params);
        List<Map<String,Object>> areaList = tbProjectMapper.queryNameByPath();
        if(null != projectList && projectList.size() > 0){
            for (Map<String,Object> map : projectList){
                for (Map<String,Object> areaMap : areaList){
                    if(map.get("path").toString().split(",")[0].equals(areaMap.get("path").toString())){
                        map.put("province",areaMap.get("name"));
                        map.remove("path");
                        break;
                    }
                }
            }
        }
        PageInfo pageInfo = new PageInfo(projectList);
        result.put("data",pageInfo.getList());
        result.put("pageNum",pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("total",pageInfo.getTotal());
        result.put("pages",pageInfo.getPages());
        return result;
    }
}
