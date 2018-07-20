package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.crm.dao.mapper.CompanyMapper;
import com.silita.biaodaa.bidCompute.BidComputeHandler;
import com.silita.biaodaa.bidCompute.algorithm.HeliAlgorithm;
import com.silita.biaodaa.bidCompute.algorithm.ZongheAlgorithm;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.es.ElasticseachService;
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
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    ZongheAlgorithm zongheAlgorithm;
    @Autowired
    HeliAlgorithm heliAlgorithm;

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
        return elasticseachService.querySnatchUrl(param);
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
            heliAlgorithm.bidCompute(param);
        }
        //返回数据
        return this.getBidResult(param);
    }

    // TODO: 综合评价计算
    private void computeSy(Map<String, Object> param) throws Exception {
        // 有效企业
        List<Map<String, Object>> validList = new ArrayList<>();
        //作废企业
        List<Map<String, Object>> aboList = new ArrayList<>();
        if (null != param.get("company")) {
            List<Map<String, Object>> company = (List<Map<String, Object>>) param.get("company");
            //招标控制价
            Double bidPrice = MapUtils.getDouble(param, "bidPrice");
            Double comPrice = 0D;
            for (Map<String, Object> comMap : company) {
                comPrice = MapUtils.getDouble(comMap, "comPrice");
                if (comPrice.compareTo(bidPrice) <= 0) {
                    validList.add(comMap);
                } else {
                    aboList.add(comMap);
                }
            }
            param.remove("company");
            //保存废标企业
            this.saveAboComList(param, aboList);
            //初始化信誉参数
            this.getConditionMap(param);
            //计算有效企业
            param.put("validList",validList);
            zongheAlgorithm.bidCompute(param);
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

    //TODO: 保存无效企业
    public void saveAboComList(Map param, List<Map<String, Object>> aboList) {
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
        List<Integer> listId = (List<Integer>) param.get("list");
        for(Integer pkid : listId){
            param.put("pkid",pkid);
            bidComputeMapper.delBidComput(param);
        }
    }

    public Map<String, Object> getBidCoDetail(Map<String, Object> param) throws ParseException {
        TbCompany tbCompany = companyMapper.queryCompanyDetail(MapUtils.getString(param, "comName"));
        if (null == tbCompany) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        List<String> srcList = companyMapper.getCertSrcUuid(tbCompany.getOrgCode());
        //获奖
        param.put("list", srcList);
        List<Map<String, Object>> nationPrize = bidDetailMapper.queryMateNameList(param);
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        Map<String, Object> resultMap = null;
        if (null != nationPrize && nationPrize.size() > 0) {
            for (Map<String, Object> map : nationPrize) {
                resultMap = new HashMap<>();
                param.put("mateName", map.get("mateName"));
                resultMap.put("mateName", map.get("mateName"));
                resultMap.put("list", bidDetailMapper.queryPrizeBidDetail(param));
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
