package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.crm.dao.mapper.CompanyMapper;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.TbBidCompute;
import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评标办法service
 */
@Service
public class BidEvaluationMethodService {

    @Autowired
    SnatchurlMapper snatchurlMapper;
    @Autowired
    BidEvaluationMethodMapper bidEvaluationMethodMapper;
    @Autowired
    TbBidComputeMapper bidComputeMapper;
    @Autowired
    TbBidResultMapper bidResultMapper;
    @Autowired
    TbCompanyMapper companyMapper;
    @Autowired
    BidComputeService bidComputeService;

    /**
     * 获取项目名称
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> getSnatchUrlList(Map<String, Object> param) {
        if (null == param.get("limit")) {
            param.put("limit", 5);
        }
        return snatchurlMapper.querySnatchurlList(param);
    }

    public List<Map<String, Object>> getNoticeDetailList(Map<String, Object> param) {
        return snatchurlMapper.queryNoticeDetailBySnatchurlId(param);
    }

    public String getReaYear() {
        return bidEvaluationMethodMapper.queryGradeYear();
    }


    public Map<String, Object> bidCompute(Map<String, Object> param) throws Exception {
        //获取基本信息
        String projName = MapUtils.getString(param, "projName");
        String projType = MapUtils.getString(param, "projType");
        String bidWay = MapUtils.getString(param, "bidWay");
        String bidDate = MapUtils.getString(param, "bidDate");
        String secName = MapUtils.getString(param, "secName");
        String paramTmp = JSON.toJSONString(param);
        //保存基本信息并返回id
        TbBidCompute bidCompute = new TbBidCompute();
        bidCompute.setBidDate(bidDate);
        bidCompute.setBidWay(bidWay);
        bidCompute.setProName(projName);
        bidCompute.setProType(projType);
        bidCompute.setSecName(secName);
        bidCompute.setUserId(VisitInfoHolder.getUid());
        bidCompute.setCredTmp(paramTmp);
        bidComputeMapper.insertBidCompute(bidCompute);
        param.put("pkid", bidCompute.getPkid());
        //判断评标办法
        if ("综合评估法".equals(bidWay)) {
            this.computeSy(param);
        }

        //返回数据
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> qParam = new HashMap<>();
        qParam.put("pkid",bidCompute.getPkid());
        qParam.put("bStatus",1);
        List<TbBidResult> valiList = bidResultMapper.queryBidResultList(qParam);
        resultMap.put("validList",valiList);
        qParam.put("bStatus",0);
        List<TbBidResult> aboList = bidResultMapper.queryBidResultList(qParam);
        resultMap.put("aboList",aboList);
        return resultMap;
    }

    // TODO: 综合评价计算
    private void computeSy(Map<String, Object> param) throws Exception {
        //有效企业
        List<Map<String, Object>> validList = new ArrayList<>();
        //作废企业
        List<Map<String, Object>> aboList = new ArrayList<>();

        if (null != param.get("company")) {
            //初始化信誉参数
            this.getConditionMap(param);
            List<Map<String, Object>> company = (List<Map<String, Object>>) param.get("company");
            //招标控制价
            Double bidPrice = MapUtils.getDouble(param, "bidPrice");
            Double comPrice = 0D;
            for (Map<String, Object> comMap : company) {
                comPrice = MapUtils.getDouble(comMap, "comPrice");
                if (comPrice.compareTo(bidPrice) > 0) {
                    aboList.add(comMap);
                } else {
                    validList.add(comMap);
                }
            }
            param.remove("company");

            //保存废标企业
            if (null != aboList && aboList.size() > 0) {
                TbBidResult bidResult = null;
                for (Map ab : aboList) {
                    bidResult = new TbBidResult();
                    bidResult.setBidPkid(MapUtils.getInteger(param, "pkid"));
                    bidResult.setComName(MapUtils.getString(ab, "comName"));
                    bidResult.setBidPrice(MapUtils.getDouble(ab, "comPrice"));
                    bidResult.setBidStatus(0);
                    bidResultMapper.insertBidResult(bidResult);
                }
            }

            //计算有效标的企业
            Double comTotal = 0D;
            for (Map<String, Object> validMap : validList) {
                comTotal = DoubleUtils.add(comTotal, MapUtils.getDouble(validMap, "comPrice"), 0D);
            }

            //报价分权数值
            Double bidRates = MapUtils.getDouble(param, "bidCount");
            //下浮系数
            Double bidRate = MapUtils.getDouble(param, "bidRate");
            Double rate = DoubleUtils.div(bidRate, 100, 4);
            //算出基准价
            Double standPrice = DoubleUtils.mul(DoubleUtils.div(comTotal, validList.size(), 4), DoubleUtils.subtract(1, rate));
            Double x = 0D;
            Double bidCount = 0D;
            Double lowerRate = 0D;
            Map<String,Object> comMap = null;
            TbCompany tbCompany = null;
            TbBidResult bidResult = null;
            for (Map<String, Object> map : validList) {
                comMap = new HashMap<>();
                comPrice = MapUtils.getDouble(map, "comPrice");
                x = DoubleUtils.mul(DoubleUtils.subtract(1, DoubleUtils.div(comPrice, standPrice, 4)), 100);
                x = Math.abs(x);
                if (x.compareTo(3D) > 0) {
                    x = 3D;
                }
                if (comPrice.compareTo(standPrice) > 0) {
                    bidCount = DoubleUtils.subtract(100, DoubleUtils.mul(x, 2));
                } else {
                    //是否有下浮系数
                    if (bidRate.compareTo(0D) > 0) {
                        bidCount = DoubleUtils.subtract(100, DoubleUtils.mul(x, 1));
                    } else {
                        bidCount = DoubleUtils.add(100, DoubleUtils.mul(x, 2), 0D);
                    }
                }

                //报价总得分
                bidCount = DoubleUtils.round(DoubleUtils.mul(bidCount,bidRates),2);

                //计算下浮率
                lowerRate = DoubleUtils.div(comPrice,bidPrice,4);

                comMap.put("lowerRate",lowerRate);
                //计算信誉分值
                tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(map,"comName"));
                if(null == tbCompany){
                    bidResult = new TbBidResult();
                    bidResult.setBidPkid(MapUtils.getInteger(param, "pkid"));
                    bidResult.setComName(MapUtils.getString(map,"comName"));
                    bidResult.setBidPrice(comPrice);
                    bidResult.setBidRate(DoubleUtils.round(DoubleUtils.mul(lowerRate,100),2)+"%");
                    bidResult.setOfferScore(bidCount);
                    bidResult.setCreditScore(0D);
                    bidResult.setTotal(bidCount);
                    bidResult.setBidStatus(1);
                    bidResultMapper.insertBidResult(bidResult);
                }else {
                    List<String> srcUid = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
                    comMap.put("srcUuidList",srcUid);
                    comMap.put("projType",MapUtils.getString(param,"projType"));
                    comMap.put("comName",MapUtils.getString(map,"comName"));
                    comMap.put("comPrice",comPrice);
                    comMap.put("bidCount",bidCount);
                    comMap.put("yearList",param.get("yearList"));
                    comMap.put("pkid",param.get("pkid"));
                    bidComputeService.computeHandler(comMap, (List<Map<String, Object>>) param.get("conditionList"));
                }
            }
        }
    }


    //TODO: 初始化值
    private void getConditionMap(Map<String,Object> param){
        List<Map<String,Object>> conditionList = new ArrayList<>();
        //获奖
        Map<String,Object> prize = MapUtils.getMap(param,"prize");
        prize.put("code","Prize");
        prize.put("resourceName","获奖情况");
        conditionList.add(prize);
        //安全认值
        Map<String,Object> safety = MapUtils.getMap(param,"safety");
        safety.put("code","Safety");
        safety.put("resourceName","安全认值");
        conditionList.add(safety);
        Map<String,Object> undes = MapUtils.getMap(param,"undes");
        undes.put("code","Undesirable");
        undes.put("resourceName","不良行为");
        conditionList.add(undes);
        param.put("conditionList",conditionList);
        //项目类型
        String projType = MapUtils.getString(param,"projType");
        if("房建".equals(projType)){
            projType = "建筑工程";
        }else if("市政".equals(projType)){
            projType = "市政公用工程";
        }else if("公路".equals(projType)){
            projType = "公路";
        }
        param.put("projType",projType);
        List<Map<String,Object>> yearList = bidEvaluationMethodMapper.queryCertYears();
        param.put("yearList",yearList);
    }
}
