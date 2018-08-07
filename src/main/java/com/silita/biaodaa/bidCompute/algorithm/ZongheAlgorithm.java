package com.silita.biaodaa.bidCompute.algorithm;

import com.silita.biaodaa.bidCompute.BidComputeHandler;
import com.silita.biaodaa.dao.TbBidResultMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.BidComputeService;
import com.silita.biaodaa.service.TbCompanyService;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 综合评标算法
 */
@Component
public class ZongheAlgorithm implements BidComputeHandler {

    @Autowired
    TbBidResultMapper bidResultMapper;
    @Autowired
    TbCompanyMapper companyMapper;
    @Autowired
    BidComputeService bidComputeService;
    @Autowired
    TbCompanyService tbCompanyService;

    @Override
    public void bidCompute(Map<String, Object> param) throws Exception {
        List<Map<String, Object>> validList = (List<Map<String, Object>>) param.get("validList");
        //招标控制价
        Double bidPrice = MapUtils.getDouble(param, "bidPrice");
        Double comPrice = 0D;
        if (null != validList && validList.size() > 0) {
            //计算有效标的企业
            Double comTotal = 0D;
            for (Map<String, Object> validMap : validList) {
                comTotal = DoubleUtils.add(comTotal, MapUtils.getDouble(validMap, "comPrice"), 0D);
            }

            //报价分权数值
            Double bidRates = MapUtils.getDouble(param, "bidCount");
            Double repCount = MapUtils.getDouble(param, "repCount");
            //下浮系数
            Double bidRate = MapUtils.getDouble(param, "bidRate");
            Double rate = DoubleUtils.div(bidRate, 100, 4);
            //算出基准价
            Double standPrice = DoubleUtils.mul(DoubleUtils.div(comTotal, validList.size(), 4), DoubleUtils.subtract(1, rate));
            Double x = 0D;
            Double bidCount = 0D;
            Double lowerRate = 0D;
            Map<String, Object> comMap = null;
            TbCompany tbCompany = null;
            TbBidResult bidResult = null;
            for (Map<String, Object> map : validList) {
                comMap = new HashMap<>();
                comPrice = MapUtils.getDouble(map, "comPrice");
                x = DoubleUtils.mul(DoubleUtils.subtract(1, DoubleUtils.div(comPrice, standPrice, 4)), 100);
                x = Math.abs(x);
                if (comPrice.compareTo(standPrice) > 0) {
                    if (bidRate.compareTo(0D) <= 0 && x.compareTo(3D) > 0) {
                        x = 3D;
                    }
                    bidCount = DoubleUtils.subtract(100, DoubleUtils.mul(x, 2));
                } else {
                    //是否有下浮系数
                    if (bidRate.compareTo(0D) > 0) {
                        bidCount = DoubleUtils.subtract(100, DoubleUtils.mul(x, 1));
                    } else {
                        if (x.compareTo(3D) > 0) {
                            x = 3D;
                        }
                        bidCount = DoubleUtils.add(100, DoubleUtils.mul(x, 1), 0D);
                    }
                }

                //报价总得分
                bidCount = DoubleUtils.round(DoubleUtils.mul(bidCount, bidRates), 2);

                //计算下浮率
                lowerRate = DoubleUtils.div(comPrice, bidPrice, 4);

                comMap.put("lowerRate", lowerRate);
                //计算信誉分值
                tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(map, "comName"));
                if (null == tbCompany) {
                    bidResult = new TbBidResult();
                    bidResult.setBidPkid(MapUtils.getInteger(param, "pkid"));
                    bidResult.setComName(MapUtils.getString(map, "comName"));
                    bidResult.setBidPrice(comPrice);
                    bidResult.setBidRate(DoubleUtils.round(DoubleUtils.mul(lowerRate, 100), 2) + "%");
                    bidResult.setOfferScore(bidCount);
                    bidResult.setCreditScore(0D);
                    bidResult.setTotal(bidCount);
                    bidResult.setBidStatus(1);
                    bidResultMapper.insertBidResult(bidResult);
                } else {
                    tbCompany = tbCompanyService.setCompanyDetail(tbCompany);
                    List<String> srcUid = companyMapper.getCertSrcUuidByName(tbCompany.getComName());
                    if(null == srcUid && null != tbCompany.getOrgCode()){
                        srcUid = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
                    }
                    if (null != srcUid && srcUid.size() > 0) {
                        comMap.put("srcUuidList", srcUid);
                    }
                    comMap.put("projType", MapUtils.getString(param, "projType"));
                    comMap.put("comName", MapUtils.getString(map, "comName"));
                    comMap.put("comPrice", comPrice);
                    comMap.put("bidCount", bidCount);
                    comMap.put("yearList", param.get("yearList"));
                    comMap.put("pkid", param.get("pkid"));
                    comMap.put("repCount", repCount);
                    bidComputeService.computeHandler(comMap, (List<Map<String, Object>>) param.get("conditionList"));
                }
            }
        }
    }
}
