package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.utils.DoubleUtils.mul;

/**
 * 信誉计算(new)
 * Created by zhushuai on 2019/3/28.
 */
@Service
public class ReputationComputerService {

    @Autowired
    TbAwardHunanMapper tbAwardHunanMapper;
    @Autowired
    TbReviewDiffMapper tbReviewDiffMapper;
    @Autowired
    TbReviewFineMapper tbReviewFineMapper;
    @Autowired
    TbAqrzHunanMapper tbAqrzHunanMapper;
    @Autowired
    PrizeMapper prizeMapper;

    public Map<String, Object> computer(Map<String, Object> param) {
        //获取最新年份
        param.put("luDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_LUBAN));
        param.put("zhuangDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_DECORATE));
        param.put("biaoDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_BUILD));
        param.put("years", tbAwardHunanMapper.queryYears(Constant.PRIZE_LOTUS));
        param.put("years2", tbAwardHunanMapper.queryYears(Constant.PRIZE_SUPER2));
        List<Map<String,Object>> gjhjList = new ArrayList<>();
        List<Map<String,Object>> sjhjList = new ArrayList<>();
        Map<String, Object> resultMap = new HashedMap();
        Map<String, Object> sjMap = new HashedMap();
        //参建单位
        Map<String, Object> canMap = this.computerHj(param, Constant.JOIN_TYPE_CAN);
        Double canTotal = MapUtils.getDouble(canMap, "total") == null ? 0D : MapUtils.getDouble(canMap, "total");
        if (null != canMap.get("gjhjList")){
            gjhjList.addAll((List)canMap.get("gjhjList"));
        }
        sjMap.put("aqrz", canMap.get("aqrzMap"));
        sjMap.put("reviewCompany", canMap.get("comList"));
        sjMap.put("reviewProject", canMap.get("proList"));
        if (null != canMap.get("sjList")){
            sjhjList.addAll((List)canMap.get("sjList"));
        }
        //承建单位
        Map<String, Object> chengMap = this.computerHj(param, Constant.JOIN_TYPE_CHENG);
        Double chengTotal = MapUtils.getDouble(chengMap, "total") == null ? 0D : MapUtils.getDouble(chengMap, "total");
        //汇总
        Double reviewFineTotal = MapUtils.getDouble(canMap, "reviewFineTotal") == null ? 0D : MapUtils.getDouble(canMap, "reviewFineTotal");
        Double comTotal = MapUtils.getDouble(canMap, "comTotal") == null ? 0D : MapUtils.getDouble(canMap, "comTotal");
        Double reviewDiffTotal = MapUtils.getDouble(canMap, "reviewDiffTotal") == null ? 0D : MapUtils.getDouble(canMap, "reviewDiffTotal");
        Double underTotal = MapUtils.getDouble(canMap, "underTotal") == null ? 0D : MapUtils.getDouble(canMap, "underTotal");
        Double aqrzTotal = 0D;
        if (null != canMap.get("aqrzMap")) {
            aqrzTotal = MapUtils.getDouble((Map) canMap.get("aqrzMap"), "score");
        }
        if (null != chengMap.get("sjList")){
            sjhjList.addAll((List)chengMap.get("sjList"));
        }
        Double score = DoubleUtils.add(100, DoubleUtils.add(underTotal, aqrzTotal, DoubleUtils.add(DoubleUtils.add(canTotal, chengTotal, reviewDiffTotal), comTotal, reviewFineTotal)), 0D);
        resultMap.put("score", score);
        sjMap.put("awards", sjhjList);
        resultMap.put("sjhj", sjMap);
        resultMap.put("gjhj", gjhjList);
        return resultMap;
    }


    /**
     * 计算
     * @param param
     * @param joinType
     * @return
     */
    private Map<String, Object> computerHj(Map<String, Object> param, String joinType) {
        //获取参建
        param.put("joinType", joinType);
        //国家级
        List<Map<String, Object>> gjhjList = this.computerCountry(param);
        //省级
        Map<String, List<Map<String, Object>>> sjhjMap = this.computerProv(param);
        //去重
        List<Map<String, Object>> sjList = sjhjMap.get("sjList");
        this.doweightPro(gjhjList, sjList, 0D);
        List<Map<String, Object>> proList = sjhjMap.get("proList");
        this.doweightPro(gjhjList, proList, 0.3);
        List<Map<String, Object>> comList = sjhjMap.get("comList");
        //计算数值
        Integer luCount = 0;
        Double luScore = 0D;
        Integer biaoCount = 0;
        Double biaoScore = 0D;
        Integer zhuangCount = 0;
        Double zhuangScore = 0D;
        for (Map<String, Object> gjMap : gjhjList) {
            if (Constant.PRIZE_LUBAN.equals(gjMap.get("awardName"))) {
                luCount++;
                luScore = MapUtils.getDouble(gjMap, "score");
            } else if (Constant.PRIZE_BUILD.equals(gjMap.get("awardName"))) {
                biaoCount++;
                biaoScore = MapUtils.getDouble(gjMap, "score");
            } else {
                zhuangCount++;
                zhuangScore = MapUtils.getDouble(gjMap, "score");
            }
        }
        Double gjTotal = DoubleUtils.add(mul(luCount, luScore), mul(biaoCount, biaoScore), mul(zhuangCount, zhuangScore));
        Integer fuCount = 0;
        Double fuScore = 0D;
        Integer youCount = 0;
        Double youScore = 0D;
        for (Map<String, Object> gjMap : gjhjList) {
            if (Constant.PRIZE_LOTUS.equals(gjMap.get("awardName"))) {
                fuCount++;
                fuScore = MapUtils.getDouble(gjMap, "score");
            } else {
                youCount++;
                youScore = MapUtils.getDouble(gjMap, "score");
            }
        }
        Double sjTotal = DoubleUtils.add(mul(fuCount, fuScore), mul(youCount, youScore), 0D);
        Double total = DoubleUtils.add(gjTotal, sjTotal, 0D);
        Map<String, Object> resultMap = new HashedMap();
        if ("参建单位".equals(joinType)) {
            total = DoubleUtils.div(total, 2, 2);
            //省级奖项考评合格
            Double reviewFineTotal = mul(proList.size(), 0.3);
            Double comTotal = mul(comList.size(), 2);
            //安全考评不合格
            Double reviewDiffTotal = this.reviewDiff(param);
            //安全认证
            Map<String, Object> aqrzMap = this.aqrzScore(param);
            //不良行为
            Double underTotal = this.undesirableScore(param);
            resultMap.put("reviewFineTotal", reviewFineTotal);
            resultMap.put("proList", proList);
            resultMap.put("comList", comList);
            resultMap.put("comTotal", comTotal);
            resultMap.put("reviewDiffTotal", reviewDiffTotal);
            resultMap.put("aqrzMap", aqrzMap);
            resultMap.put("underTotal", underTotal);
        }
        resultMap.put("gjhjList", gjhjList);
        resultMap.put("sjList", sjList);
        resultMap.put("score", total);
        return resultMap;
    }

    /**
     * 获取国家级奖项
     *
     * @param param
     * @return
     */
    private List<Map<String, Object>> computerCountry(Map<String, Object> param) {
        List<Map<String, Object>> list = tbAwardHunanMapper.queryGjhjAwardsList(param);
        return list;
    }

    /**
     * 获取省级级奖项
     *
     * @param param
     * @return
     */
    private Map<String, List<Map<String, Object>>> computerProv(Map<String, Object> param) {
        //获取芙蓉奖省优质工程个数5
        List<Map<String, Object>> list = tbAwardHunanMapper.querySjhjAwardsList(param);
        //获取评定合格优良工地个数15
        param.put("type", "project");
        List<Map<String, Object>> proList = tbReviewFineMapper.queryReviewFineList(param);
        //获取评定合格优良企业
        param.put("type", "company");
        List<Map<String, Object>> comList = tbReviewFineMapper.queryReviewFineList(param);
        //去重
        doweightPro(list, proList, 0.3);
        Map<String, List<Map<String, Object>>> sjhjMap = new HashedMap() {{
            put("sjList", list);
            put("proList", proList);
            put("comList", comList);
        }};
        return sjhjMap;
    }

    /**
     * 去重
     *
     * @param list1
     * @param list2
     * @param d
     */
    private void doweightPro(List<Map<String, Object>> list1, List<Map<String, Object>> list2, Double d) {
        if ((null != list1 && list1.size() > 0) && (null != list2 && list2.size() > 0)) {
            List<Integer> listInt = new ArrayList<>();
            List<Integer> proInt = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                for (int j = 0; j < list2.size(); j++) {
                    if (list1.get(i).get("projName").toString().equals(list2.get(j).get("projName").toString())) {
                        Double d1 = MapUtils.getDouble(list1.get(i), "score");
                        Double d2;
                        if (null != list2.get(i).get("score")) {
                            d2 = MapUtils.getDouble(list2.get(i), "score");
                        } else {
                            d2 = d;
                        }
                        if (d1.compareTo(d2) == 1) {
                            proInt.add(j);
                        } else if (d1.compareTo(d2) == -1) {
                            listInt.add(i);
                        } else {
                            proInt.add(j);
                        }
                    }
                }
            }
            if (null != listInt && listInt.size() > 0) {
                for (Integer index : listInt) {
                    list1.remove(index);
                }
            }
            if (null != proInt && proInt.size() > 0) {
                for (Integer index : proInt) {
                    list2.remove(index);
                }
            }
        }
    }

    /**
     * 计算安全考评不合格
     *
     * @param param
     * @return
     */
    private Double reviewDiff(Map<String, Object> param) {
        param.put("type", "project");
        Integer proCount = tbReviewDiffMapper.queryReviewDiffCount(param);
        param.put("type", "company");
        Integer comCount = tbReviewDiffMapper.queryReviewDiffCount(param);
        return DoubleUtils.add(mul(proCount, -0.3), mul(comCount, -2), 0D);
    }

    /**
     * 计算安全认证
     *
     * @param param
     * @return
     */
    private Map<String, Object> aqrzScore(Map<String, Object> param) {
        return tbAqrzHunanMapper.queryAqrz(param);
    }

    /**
     * 计算企业不良行为
     *
     * @param param
     * @return
     */
    private Double undesirableScore(Map<String, Object> param) {
        param.put("nature", Constant.SCORE_SEV);
        Integer sevCount = prizeMapper.queryUndersiableCount(param);
        param.put("nature", Constant.SCORE_COMM);
        Integer commCount = prizeMapper.queryUndersiableCount(param);
        return DoubleUtils.add(mul(sevCount, -4), mul(commCount, -2), 0D);
    }

    /**
     * 不良行为列表
     * @param param
     * @return
     */
    public Map<String,Object> listUndesirable(Map<String, Object> param){
        Map<String,Object> resultMap = new HashedMap();
        param.put("type","company");
        resultMap.put("unCompany",tbReviewDiffMapper.queryReviewDiff(param));
        param.put("type","project");
        resultMap.put("unProject",tbReviewDiffMapper.queryReviewDiff(param));
        resultMap.put("undesirable",prizeMapper.queryUndersiableList(param));
        return resultMap;
    }
}
