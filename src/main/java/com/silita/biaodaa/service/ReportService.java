package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.AptitudeDictionaryMapper;
import com.silita.biaodaa.dao.TbReportInfoMapper;
import com.silita.biaodaa.dao.VipInfoMapper;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbReportInfo;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/4/16.
 */
@Service
public class ReportService {

    @Autowired
    TbReportInfoMapper tbReportInfoMapper;
    @Autowired
    AptitudeDictionaryMapper aptitudeDictionaryMapper;
    @Autowired
    VipInfoMapper vipInfoMapper;

    public Map<String, Object> saveCondition(Map<String, Object> param) {
        TbReportInfo reportInfo = new TbReportInfo();
        reportInfo.setUserId(VisitInfoHolder.getUid());
        reportInfo.setRepCondition(JSONObject.toJSONString(param));
        reportInfo.setPattern(Constant.REPORT_PATTERN_PDF);
        reportInfo.setPayStatus(Constant.PAY_STATUS_QUERY);
        reportInfo.setRepTitle(PropertiesUtils.getProperty("report.title"));
        tbReportInfoMapper.insert(reportInfo);

        //设置资质名称
        setQualName(param);
        param.put("pkid", reportInfo.getPkid());
        param.put("title",PropertiesUtils.getProperty("report.title"));
        param.put("reportPath",PropertiesUtils.getProperty("report.path"));
        //收费标准码
        param.put("price",vipInfoMapper.queryFeeStandardReport(Constant.REPORT_CHANNEL));
        return param;
    }

    public PageInfo listHistory(Map<String, Object> param){
        Integer pageNo = MapUtils.getInteger(param,"pageNo");
        Integer pageSize = MapUtils.getInteger(param,"pageSize");
        Page page = new Page();
        page.setCurrentPage(pageNo);
        page.setPageSize(pageSize);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String,Object>> list = tbReportInfoMapper.queryReportList(param);
        for (Map<String,Object> map : list){
            String reqCondition = MapUtils.getString(map,"repCondition");
            Map<String,Object> condition = JSONObject.parseObject(reqCondition);
            setQualName(condition);
            condition.put("pkid",map.get("pkid"));
            condition.put("title",map.get("title"));
            condition.put("userId",map.get("userId"));
            condition.put("price",map.get("price"));
            list.add(condition);
            list.remove(map);
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    private String setQualGrade(String grade) {
        String gradeName = null;
        switch (grade) {
            case "0":
                gradeName = "特级";
                break;
            case "1":
                gradeName = "一级";
                break;

            case "2":
                gradeName = "二级";
                break;
            case "3":
                gradeName = "三级";
                break;
            case "11":
                gradeName = "一级及以上";
                break;
            case "21":
                gradeName = "二级及以上";
                break;
            case "31":
                gradeName = "三级及以上";
                break;
            case "-1":
                gradeName = "甲级";
                break;
            case "-2":
                gradeName = "乙级";
                break;
            case "-3":
                gradeName = "丙级";
                break;
        }
        return gradeName;
    }

    private void setQualName(Map<String,Object> param){
        String qualCode = MapUtils.getString(param, "qualCode");
        if (StringUtils.isNotEmpty(qualCode)) {
            String[] qualCodeStr = qualCode.split(",");
            StringBuffer qualName = new StringBuffer();
            for (String str : qualCodeStr) {
                String[] qua = str.split("/");
                AptitudeDictionary aptitudeDictionary = aptitudeDictionaryMapper.queryQualDetail(qua[0]);
                qualName.append(aptitudeDictionary.getAptitudename() + aptitudeDictionary.getMajorname() + this.setQualGrade(qua[1])).append(",");
            }
            param.put("qualName", qualName.toString());
        }
    }
}
