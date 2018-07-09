package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.crm.dao.mapper.CompanyMapper;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.TbBidCompute;
import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.DoubleUtils;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * 评标办法service
 */
@Service
@Transactional
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
    @Autowired
    TbBidDetailMapper bidDetailMapper;

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
        String date = bidEvaluationMethodMapper.queryGradeYear();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        try {
            Date d = sdf.parse(date);
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(new Date());
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
        } else {
            this.computeRea(param);
        }

        //返回数据
        param.put("pkid", bidCompute.getPkid());
        return this.getBidResult(param);
    }

    // TODO: 综合评价计算
    private void computeSy(Map<String, Object> param) throws Exception {
        // 有效企业
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
            this.saveAboComList(param, aboList);
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
                        List<String> srcUid = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
                        comMap.put("srcUuidList", srcUid);
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

    //TODO: 初始化值
    private void getConditionMap(Map<String, Object> param) {
        List<Map<String, Object>> conditionList = new ArrayList<>();
        //获奖
        Map<String, Object> prize = MapUtils.getMap(param, "prize");
        prize.put("code", "Prize");
        prize.put("resourceName", "获奖情况");
        conditionList.add(prize);
        //安全认值
        Map<String, Object> safety = MapUtils.getMap(param, "safety");
        safety.put("code", "Safety");
        safety.put("resourceName", "安全认值");
        conditionList.add(safety);
        Map<String, Object> undes = MapUtils.getMap(param, "undes");
        undes.put("code", "Undesirable");
        undes.put("resourceName", "不良行为");
        conditionList.add(undes);
        param.put("conditionList", conditionList);
        //项目类型
        String projType = MapUtils.getString(param, "projType");
        if ("房建".equals(projType)) {
            projType = "建筑工程";
        } else if ("市政".equals(projType)) {
            projType = "市政公用工程";
        } else if ("公路".equals(projType)) {
            projType = "公路";
        }
        param.put("projType", projType);
        List<Map<String, Object>> yearList = bidEvaluationMethodMapper.queryCertYears();
        param.put("yearList", yearList);
    }

    private void getReaMap(Map<String, Object> param) {
        List<Map<String, Object>> conditionList = new ArrayList<>();
        Map<String, Object> reputateMap = MapUtils.getMap(param, "reputate");
        reputateMap.put("code", "Rational");
        reputateMap.put("resourceName", "信用等级");
        conditionList.add(reputateMap);
        param.put("conditionList", conditionList);
    }

    //TODO: 合理计算
    private void computeRea(Map param) throws Exception {
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
            this.saveAboComList(param, aboList);

            //计算有效企业值
            if (null != validList && validList.size() > 0) {
                //存储
                List<Map<String,Object>> valdList = new ArrayList<>();
                valdList.addAll(validList);
                //初始化
                this.getReaMap(param);
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
                Double touPrice = 0d;
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
                    if (MapUtils.getBoolean(param, "isRep")) {
                        touPrice = 99d;
                    } else {
                        touPrice = 100d;
                    }

                    //投标报价是否大于基准价
                    if (comPrice.compareTo(jizhunPrice) > 0) {
                        total = DoubleUtils.subtract(touPrice, DoubleUtils.mul(2,DoubleUtils.mul(devRate, 100)));
                    } else {
                        total = DoubleUtils.subtract(touPrice, DoubleUtils.mul(1,DoubleUtils.mul(devRate, 100)));
                    }
                    //TODO: 计算信用等级分
                    //计算下浮率
                    lowerRate = DoubleUtils.div(comPrice, bidPrice, 4);

                    comMap.put("lowerRate", lowerRate);
                    //计算信誉分值
                    tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(vildMap, "comName"));
                    if (null == tbCompany) {
                        bidResult = new TbBidResult();
                        bidResult.setComName(MapUtils.getString(vildMap, "comName"));
                        bidResult.setBidPkid(MapUtils.getInteger(param, "pkid"));
                        bidResult.setBidPrice(comPrice);
                        bidResult.setBidRate(DoubleUtils.round(DoubleUtils.mul(lowerRate, 100), 2) + "%");
                        bidResult.setOfferScore(total);
                        bidResult.setCreditScore(0D);
                        bidResult.setTotal(total);
                        bidResult.setBidStatus(1);
                        bidResultMapper.insertBidResult(bidResult);
                    } else {
                        List<String> srcUid = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
                        comMap.put("srcUuidList", srcUid);
                        comMap.put("projType", MapUtils.getString(param, "projType"));
                        comMap.put("comName", MapUtils.getString(vildMap, "comName"));
                        comMap.put("comPrice", comPrice);
                        comMap.put("bidCount", total);
                        comMap.put("yearList", param.get("yearList"));
                        comMap.put("pkid", param.get("pkid"));
                        bidComputeService.computeHandler(comMap, (List<Map<String, Object>>) param.get("conditionList"));
                    }
                }
            }
        }
    }

    //TODO: 保存无效企业
    private void saveAboComList(Map param, List<Map<String, Object>> aboList) {
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
    }

    public List<TbBidCompute> getBidComputerList() {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", VisitInfoHolder.getUid());
        return bidComputeMapper.queryBidComputeList(param);
    }

    public void delBidComputer(Map<String, Object> param) {
        bidComputeMapper.delBidComput(param);
    }

    public Map<String, Object> getBidCoDetail(Map<String, Object> param) throws ParseException {
        TbCompany tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(param, "comName"));
        if (null == tbCompany) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        List<String> srcList = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
        //获奖
        param.put("projType", bidComputeMapper.queryProType(MapUtils.getInteger(param, "bidPkid")));
        param.put("list", srcList);
        param.put("joinType", "承建单位");
        List<Map<String, Object>> nationPrize = bidDetailMapper.queryMateNameList(param);
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        Map<String, Object> resultMap = null;
        List<String> years = null;
        if (null != nationPrize && nationPrize.size() > 0) {
            for (Map<String, Object> map : nationPrize) {
                years = new ArrayList<>();
                resultMap = new HashMap<>();
                param.put("mateName", map.get("mateName"));
                resultMap.put("mateName", map.get("mateName"));
                resultMap.put("list", bidDetailMapper.queryPrizeBidDetail(param));
                //获取总数
                if ("鲁班奖".equals(map.get("mateName").toString())) {
                    getLubanYears(years, map.get("years").toString());
                } else {
                    years.add(map.get("years").toString());
                }
                param.put("years", years);
                resultMap.put("prizeCount", bidEvaluationMethodMapper.queryCertPrizeCount(param));
                resultMapList.add(resultMap);
            }
        }
        result.put("prize", resultMapList);
        //安全认证
        result.put("safety", bidDetailMapper.querySafetyDetail(param));
        //不良行为
        List<Map<String, Object>> undesList = bidDetailMapper.queryUnabDetail(param);
        result.put("undesirable", undesList);
        return result;
    }

    private void getLubanYears(List<String> years, String year) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        String date = null;
        if (year.contains("～")) {
            date = year.split("～")[1];
        } else if (year.contains("-")) {
            date = year.split("-")[1];
        } else {
            date = year;
        }
        String dateN = ProjectAnalysisUtil.getStrNumber(date);
        Date lastDate = sdf.parse(date);
        Date endDate = null;
        String endStr = null;
        if (Integer.valueOf(dateN) % 2 == 0) {
            endDate = MyDateUtils.getDayBefore(lastDate, -1);
            endStr = sdf.format(endDate);
            years.add(endStr + "～" + date);
            years.add(date + "-" + endStr + "度");
        } else {
            endDate = MyDateUtils.getDayBefore(lastDate, 1);
            endStr = sdf.format(endDate);
            years.add(endStr + "～" + date);
            years.add(endStr + "-" + dateN + "年度");
        }
    }

    public Map<String, Object> getBidResult(Map<String, Object> param) {
        //返回数据
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> qParam = new HashMap<>();
        qParam.put("pkid", param.get("pkid"));
        qParam.put("bStatus", 1);
        List<TbBidResult> valiList = bidResultMapper.queryBidResultList(qParam);
        resultMap.put("validList", valiList);
        qParam.put("bStatus", 0);
        List<TbBidResult> aboList = bidResultMapper.queryBidResultList(qParam);
        resultMap.put("aboList", aboList);
        resultMap.put("bidWay", param.get("bidWay"));
        return resultMap;
    }
}
