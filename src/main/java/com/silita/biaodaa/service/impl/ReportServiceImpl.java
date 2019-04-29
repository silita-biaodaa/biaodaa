package com.silita.biaodaa.service.impl;

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
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.ReportService;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/4/16.
 */
@Service("reportService")
public class ReportServiceImpl implements ReportService {

    @Autowired
    TbReportInfoMapper tbReportInfoMapper;
    @Autowired
    AptitudeDictionaryMapper aptitudeDictionaryMapper;
    @Autowired
    VipInfoMapper vipInfoMapper;

    @Override
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
        param.put("title", PropertiesUtils.getProperty("report.title"));
        param.put("reportPath", PropertiesUtils.getProperty("report.path"));
        //收费标准码
        List<TbVipFeeStandard> list = new ArrayList<>();
        list.addAll(vipInfoMapper.queryFeeStandardReport(Constant.REPORT_CHANNEL_COMMON));
        list.addAll(vipInfoMapper.queryFeeStandardReport(Constant.REPORT_CHANNEL_VIP));
        param.put("price", list);
        return param;
    }

    @Override
    public void saveReportOrder(Map<String, Object> param) {
        TbReportInfo reportInfo = new TbReportInfo();
        reportInfo.setPkid(MapUtils.getInteger(param, "pkid"));
        reportInfo.setPayStatus(MapUtils.getInteger(param, "payStatus"));
        reportInfo.setOrderNo(MapUtils.getString(param, "orderNo"));
        if (null != param.get("email")) {
            reportInfo.setEmail(MapUtils.getString(param, "email"));
        }
        if (null != param.get("phone")) {
            reportInfo.setPhone(MapUtils.getString(param, "phone"));
        }
        if (null != param.get("stdCode")) {
            reportInfo.setStdCode(MapUtils.getString(param, "stdCode"));
        }
        tbReportInfoMapper.updateReportOrder(reportInfo);
    }

    @Override
    public void updateReportOrderPayStatus(String orderNo, String resultCode,String transactionId) {
        int orderStatus;
        if ("SUCCESS".equalsIgnoreCase(resultCode)) {
            orderStatus = 9;//支付成功
        } else {
            orderStatus = 3;//支付失败
        }
        TbReportInfo reportInfo = new TbReportInfo();
        reportInfo.setPayStatus(orderStatus);
        reportInfo.setOrderNo(orderNo);
        //微信订单号
        reportInfo.setTransactionId(transactionId);
        tbReportInfoMapper.updateReportOrderPayStatus(reportInfo);
    }

    @Override
    public boolean updateReportEmail(Map<String,Object> param) {
        //有没有生成报告
        String email = MapUtils.getString(param, "email");
        Integer pkid = MapUtils.getInteger(param, "pkid");
        String orderNo = MapUtils.getString(param, "orderNo");
        TbReportInfo report = tbReportInfoMapper.queryReportDetailOrderNo(orderNo);
        if (null != report && StringUtils.isNotEmpty(report.getReportPath())){
            TbReportInfo reportInfo = new TbReportInfo();
            reportInfo.setPkid(pkid);
            reportInfo.setEmail(email);
            tbReportInfoMapper.updateReportEmail(reportInfo);
            return true;
        }
        return false;
    }

    @Override
    public PageInfo listHistory(Map<String, Object> param) {
        Integer pageNo = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setCurrentPage(pageNo);
        page.setPageSize(pageSize);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = tbReportInfoMapper.queryReportList(param);
        for (int i = 0; i < list.size(); i++) {
            String reqCondition = MapUtils.getString(list.get(i), "repCondition");
            Map<String, Object> condition = JSONObject.parseObject(reqCondition);
            setMap(list.get(i), condition);
            setQualName(list.get(i));
            list.get(i).remove("repCondition");
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

    @Override
    public void setQualName(Map<String, Object> param) {
        String qualCode = MapUtils.getString(param, "qualCode");
        if (StringUtils.isNotEmpty(qualCode)) {
            String[] qualCodeStr = qualCode.split(",");
            StringBuffer qualName = new StringBuffer();
            for (String str : qualCodeStr) {
                String[] qua = str.split("/");
                AptitudeDictionary aptitudeDictionary = aptitudeDictionaryMapper.queryQualDetail(qua[0]);
                if (null != aptitudeDictionary) {
                    if (qua.length == 2) {
                        qualName.append(aptitudeDictionary.getAptitudename() + aptitudeDictionary.getMajorname() + this.setQualGrade(qua[1])).append(",");
                    } else {
                        qualName.append(aptitudeDictionary.getAptitudename()).append(",");
                    }
                }
            }
            param.put("qualName", StringUtils.strip(qualName.toString(), ","));
        }
    }

    @Override
    public Map<String, Object> getReportMap(Map<String,Object> param) {
        TbReportInfo reportInfo = tbReportInfoMapper.queryReportDetailOrderPayStatus(param);
        if (null != reportInfo) {
            Map<String, Object> resultMap = new HashedMap();
            resultMap.put("pkid", reportInfo.getPkid());
            resultMap.put("repTitle", reportInfo.getRepTitle());
            resultMap.put("reportPath", reportInfo.getReportPath());
            resultMap.put("email", reportInfo.getEmail());
            resultMap.put("payDate", MyDateUtils.getDate(reportInfo.getUpdated(), "yyyy-MM-dd"));
            resultMap.put("pattern",reportInfo.getPattern());
            return resultMap;
        }
        return null;
    }

    private void setMap(Map<String, Object> target, Map<String, Object> soure) {
        target.put("regisAddress", soure.get("regisAddress"));
        target.put("qualCode", soure.get("qualCode"));
        target.put("rangeType", soure.get("rangeType"));
        target.put("projSource", soure.get("projSource"));
        target.put("projName", soure.get("projName"));
        target.put("buildStart", soure.get("buildStart"));
        target.put("buildEnd", soure.get("buildEnd"));
        target.put("amountStart", soure.get("amountStart"));
        target.put("amountEnd", soure.get("amountEnd"));
        soure.remove("repCondition");
    }
}
