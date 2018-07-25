package com.silita.biaodaa.bidCompute.algorithm;

import com.silita.biaodaa.bidCompute.BidComputeHandler;
import com.silita.biaodaa.dao.TbBidResultMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.BidComputeService;
import com.silita.biaodaa.service.BidEvaluationMethodService;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 合理评估算法
 */
@Component
public class HeliAlgorithm implements BidComputeHandler {

    @Autowired
    TbBidResultMapper bidResultMapper;
    @Autowired
    TbCompanyMapper companyMapper;
    @Autowired
    BidComputeService bidComputeService;
    @Autowired
    BidEvaluationMethodService bidEvaluationMethodService;

    @Override
    public void bidCompute(Map<String, Object> param) {
        if (MapUtils.getBoolean(param, "isRep")) {
            this.heliCompute1(param);
        } else {
            this.heliCompute2(param);
        }
    }

    /**
     * 初始化参数
     *
     * @param param
     */
    private void getReaMap(Map<String, Object> param) {
        List<Map<String, Object>> conditionList = new ArrayList<>();
        Map<String, Object> reputateMap = MapUtils.getMap(param, "reputate");
        reputateMap.put("code", "Rational");
        reputateMap.put("resourceName", "信用等级");
        conditionList.add(reputateMap);
        param.put("conditionList", conditionList);
    }

    /**
     * 计算信誉分值
     *
     * @param tbCompany
     * @param bidResult
     * @param param
     * @param vildMap
     * @param comPrice
     * @param total
     * @param lowerRate
     * @param comMap
     */
    private void bidCompte(TbCompany tbCompany, TbBidResult bidResult, Map<String, Object> param, Map<String, Object> vildMap, Double comPrice, Double total, Double lowerRate, Map<String, Object> comMap) {
        tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(vildMap, "comName"));
        if (null == tbCompany || !MapUtils.getBoolean(param, "isRep")) {
            bidResult = new TbBidResult();
            bidResult.setComName(MapUtils.getString(vildMap, "comName"));
            bidResult.setBidPkid(MapUtils.getInteger(param, "pkid"));
            bidResult.setBidPrice(comPrice);
            bidResult.setBidRate(DoubleUtils.round(DoubleUtils.mul(lowerRate, 100), 2) + "%");
            bidResult.setOfferScore(total);
            bidResult.setCreditScore(0D);
            bidResult.setTotal(DoubleUtils.round(total, 2));
            bidResult.setBidStatus(1);
            bidResultMapper.insertBidResult(bidResult);
        } else {
            //初始化
            this.getReaMap(param);
            List<String> srcUid = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
            if (null != srcUid && srcUid.size() > 0) {
                comMap.put("srcUuidList", srcUid);
            }
            comMap.put("projType", MapUtils.getString(param, "projType"));
            comMap.put("comName", MapUtils.getString(vildMap, "comName"));
            comMap.put("comPrice", comPrice);
            comMap.put("bidCount", DoubleUtils.round(total, 2));
            comMap.put("yearList", param.get("yearList"));
            comMap.put("pkid", param.get("pkid"));
            try {
                bidComputeService.computeHandler(comMap, (List<Map<String, Object>>) param.get("conditionList"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合理评估1，含有信誉分值
     *
     * @param param
     */
    private void heliCompute1(Map<String, Object> param) {
        if (null != param.get("company")) {
            List<Map<String, Object>> company = (List<Map<String, Object>>) param.get("company");
            //总价
            Double comTotal = 0d;
            for (Map<String, Object> com : company) {
                comTotal = DoubleUtils.add(comTotal, MapUtils.getDouble(com, "comPrice"), 0);
            }

            //最高限价
            Double bidPrice = MapUtils.getDouble(param, "bidPrice");
            //理论成本价
            Double costPrice = DoubleUtils.mul(0.88, DoubleUtils.add(DoubleUtils.mul(bidPrice, 0.6),
                    DoubleUtils.mul(DoubleUtils.div(comTotal, company.size(), 4), 0.4), 0));

            //有效企业
            List<Map<String, Object>> validList = new ArrayList<>();
            //无效企业
            List<Map<String, Object>> aboList = new ArrayList<>();
            for (Map<String, Object> com : company) {
                if (MapUtils.getDouble(com, "comPrice").compareTo(costPrice) < 0) {
                    aboList.add(com);
                } else {
                    validList.add(com);
                }
            }

            //保存废标企业
            bidEvaluationMethodService.saveAboComList(param, aboList);

            //计算有效企业值
            if (null != validList && validList.size() > 0) {
                //存储
                List<Map<String, Object>> valdList = new ArrayList<>();
                valdList.addAll(validList);
                if (validList.size() > 5) {
                    //排序并去掉最高价企业和最低价企业
                    Collections.sort(validList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                            Double d1 = MapUtils.getDouble(o1, "comPrice");
                            Double d2 = MapUtils.getDouble(o2, "comPrice");
                            return d2.compareTo(d1);
                        }
                    });
                    validList.remove(0);
                    validList.remove(validList.size() - 1);
                }

                //报价平均值
                Double valiCount = 0d;
                for (Map<String, Object> viMap : validList) {
                    valiCount = DoubleUtils.add(valiCount, MapUtils.getDouble(viMap, "comPrice"), 0);
                }
                Double perCount = DoubleUtils.div(valiCount, validList.size(), 4);

                //TODO: 计算基准价
                //权重C1
                Double bidCount = MapUtils.getDouble(param, "bidCount");
                //权重C2
                Double repCount = MapUtils.getDouble(param, "repCount");
                //下浮系数
                Double bidRate = DoubleUtils.div(MapUtils.getDouble(param, "bidRate"), 100, 4);
                Double jizhunPrice = DoubleUtils.mul(DoubleUtils.add(DoubleUtils.mul(bidPrice, bidCount),
                        DoubleUtils.mul(perCount, repCount), 0), DoubleUtils.subtract(1, bidRate));
                //偏差率
                Double devRate = 0d;
                //投标价
                Double touPrice = 99d;
                Double comPrice = 0d;
                //最后报价得分
                Double total = 0d;
                //下浮率
                Double lowerRate = 0d;
                Map<String, Object> comMap = null;
                TbCompany tbCompany = null;
                TbBidResult bidResult = null;
                for (Map<String, Object> vildMap : valdList) {
                    comMap = new HashMap<>();
                    comPrice = MapUtils.getDouble(vildMap, "comPrice");
                    devRate = DoubleUtils.div(Math.abs(DoubleUtils.subtract(comPrice, jizhunPrice)), jizhunPrice, 4);
                    //投标报价是否大于基准价
                    if (comPrice.compareTo(jizhunPrice) > 0) {
                        total = DoubleUtils.subtract(touPrice, DoubleUtils.mul(2, DoubleUtils.mul(devRate, 100)));
                    } else {
                        total = DoubleUtils.subtract(touPrice, DoubleUtils.mul(1, DoubleUtils.mul(devRate, 100)));
                    }
                    //TODO: 计算信用等级分
                    //计算下浮率
                    lowerRate = DoubleUtils.div(comPrice, bidPrice, 4);

                    comMap.put("lowerRate", lowerRate);
                    //计算信誉分值
                    this.bidCompte(tbCompany, bidResult, param, vildMap, comPrice, total, lowerRate, comMap);
                }
            }
        }
    }

    private void heliCompute2(Map<String, Object> param) {
        if (null != param.get("company")) {
            List<Map<String, Object>> companyList = (List<Map<String, Object>>) param.get("company");
            List<Map<String, Object>> aboList = new ArrayList<>();
            List<Map<String, Object>> validList = new ArrayList<>();
            //获取投标上限价
            Double bidPrice = MapUtils.getDouble(param, "bidPrice");

            Double comPrice = 0d;
            for (Map<String, Object> com : companyList) {
                comPrice = MapUtils.getDouble(com, "comPrice");
                if (comPrice.compareTo(bidPrice) > 0) {
                    aboList.add(com);
                } else {
                    validList.add(com);
                }
            }

            //保存废标
            bidEvaluationMethodService.saveAboComList(param, aboList);

            if (validList.size() > 0) {
                List<Map<String,Object>> vald = new ArrayList<>();
                vald.addAll(validList);
                if (validList.size() > 5) {
                    this.sort(validList);
                }
                //计算报价评价值
                Double perCount = this.computePerCount(validList);
                Double jizhunPrice = perCount;
                //计算偏差率
                Double devRate = 0d;
                Double total = 0d;
                Double lowerRate = 0d;
                for (Map<String, Object> val : vald) {
                    comPrice = MapUtils.getDouble(val, "comPrice");
                    devRate = DoubleUtils.div(Math.abs(DoubleUtils.subtract(comPrice, jizhunPrice)), jizhunPrice, 4);
                    if (comPrice.compareTo(jizhunPrice) > 0) {
                        total = DoubleUtils.subtract(100, DoubleUtils.mul(2,DoubleUtils.mul(devRate, 100)));
                    } else {
                        total = DoubleUtils.subtract(100, DoubleUtils.mul(1,DoubleUtils.mul(devRate, 100)));
                    }
                    //计算下浮率
                    lowerRate = DoubleUtils.div(comPrice, bidPrice, 4);
                    this.bidCompte(new TbCompany(), new TbBidResult(), param, val, comPrice, total, lowerRate, null);
                }
            }
        }
    }

    /**
     * 排序并去掉最高价企业和最低价企业
     *
     * @param validList
     */
    private void sort(List<Map<String, Object>> validList) {
        Collections.sort(validList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double d1 = MapUtils.getDouble(o1, "comPrice");
                Double d2 = MapUtils.getDouble(o2, "comPrice");
                return d2.compareTo(d1);
            }
        });
        validList.remove(0);
        validList.remove(validList.size() - 1);
    }

    /**
     * 计算报价评价值
     *
     * @param validList
     * @return
     */
    private Double computePerCount(List<Map<String, Object>> validList) {
        Double valiCount = 0d;
        for (Map<String, Object> viMap : validList) {
            valiCount = DoubleUtils.add(valiCount, MapUtils.getDouble(viMap, "comPrice"), 0);
        }
        Double perCount = DoubleUtils.div(valiCount, validList.size(), 4);
        return perCount;
    }
}
