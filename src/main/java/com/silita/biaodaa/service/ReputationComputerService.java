package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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
    private TbAwardHunanMapper tbAwardHunanMapper;
    @Autowired
    private TbReviewDiffMapper tbReviewDiffMapper;
    @Autowired
    private TbReviewFineMapper tbReviewFineMapper;
    @Autowired
    private TbAqrzHunanMapper tbAqrzHunanMapper;
    @Autowired
    private PrizeMapper prizeMapper;
    @Autowired
    private TbAwardNationwideMapper tbAwardNationwideMapper;
    @Autowired
    private TbHighwayCreditMapper tbHighwayCreditMapper;
    @Autowired
    private TbShuiliCreditMapper tbShuiliCreditMapper;

    public Map<String, Object> computer(Map<String, Object> param) {
        //获取最新年份
        param.put("luDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_LUBAN));
        param.put("zhuangDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_DECORATE));
        param.put("biaoDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_BUILD));
        param.put("furongDate", "2017-2018年度第二批");
        param.put("shengyouDate", tbAwardHunanMapper.queryYears(Constant.PRIZE_SUPER));
        List<Map<String, Object>> gjhjList = new ArrayList<>();
        List<Map<String, Object>> sjhjList = new ArrayList<>();
        Map<String, Object> resultMap = new HashedMap();
        Map<String, Object> sjMap = new HashedMap();
        Map<String, Object> canMap = this.computerHj(param);
        Double canTotal = MapUtils.getDouble(canMap, "score") == null ? 0D : MapUtils.getDouble(canMap, "score");
        if (null != canMap.get("gjhjList")) {
            gjhjList.addAll((List) canMap.get("gjhjList"));
        }
        sjMap.put("aqrz", canMap.get("aqrzMap"));
        sjMap.put("reviewCompany", canMap.get("comList"));
        sjMap.put("reviewProject", canMap.get("proList"));
        if (null != canMap.get("sjList")) {
            sjhjList.addAll((List) canMap.get("sjList"));
        }
        //汇总
        Double reviewFineTotal = MapUtils.getDouble(canMap, "reviewFineTotal") == null ? 0D : MapUtils.getDouble(canMap, "reviewFineTotal");
        Double comTotal = MapUtils.getDouble(canMap, "comTotal") == null ? 0D : MapUtils.getDouble(canMap, "comTotal");
        Double reviewDiffTotal = MapUtils.getDouble(canMap, "reviewDiffTotal") == null ? 0D : MapUtils.getDouble(canMap, "reviewDiffTotal");
        Double underTotal = MapUtils.getDouble(canMap, "underTotal") == null ? 0D : MapUtils.getDouble(canMap, "underTotal");
        Double aqrzTotal = 0D;
        if (null != canMap.get("aqrzMap")) {
            aqrzTotal = MapUtils.getDouble((Map) canMap.get("aqrzMap"), "score");
        }
        Double score = DoubleUtils.add(100, DoubleUtils.add(underTotal, aqrzTotal, DoubleUtils.add(DoubleUtils.add(canTotal, 0D, reviewDiffTotal), comTotal, reviewFineTotal)), 0D);
        resultMap.put("score", score);
        sjMap.put("awards", sjhjList);
        resultMap.put("sjhj", sjMap);
        resultMap.put("gjhj", gjhjList);
        return resultMap;
    }


    /**
     * 计算
     *
     * @param param
     * @return
     */
    private Map<String, Object> computerHj(Map<String, Object> param) {
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
        Integer canLuCount = 0;
        Double canLuScore = 0D;
        Integer luCount = 0;
        Double luScore = 0D;
        Integer canBiaoCount = 0;
        Double canBiaoScore = 0D;
        Integer biaoCount = 0;
        Double biaoScore = 0D;
        Integer canZhuangCount = 0;
        Double canZhuangScore = 0D;
        Integer zhuangCount = 0;
        Double zhuangScore = 0D;
        for (Map<String, Object> gjMap : gjhjList) {
            if (Constant.PRIZE_LUBAN.equals(gjMap.get("awardName"))) {
                if (Constant.JOIN_TYPE_CAN.equals(gjMap.get("joinType"))) {
                    canLuCount++;
                    canLuScore = DoubleUtils.div(MapUtils.getDouble(gjMap, "score"), 2, 2);
                } else {
                    luCount++;
                    luScore = MapUtils.getDouble(gjMap, "score");
                }
            } else if (Constant.PRIZE_BUILD.equals(gjMap.get("awardName"))) {
                if (Constant.JOIN_TYPE_CAN.equals(gjMap.get("joinType"))) {
                    canBiaoCount++;
                    canBiaoScore = DoubleUtils.div(MapUtils.getDouble(gjMap, "score"), 2, 2);
                } else {
                    biaoCount++;
                    biaoScore = MapUtils.getDouble(gjMap, "score");
                }
            } else {
                if (Constant.JOIN_TYPE_CAN.equals(gjMap.get("joinType"))) {
                    canZhuangCount++;
                    canZhuangScore = DoubleUtils.div(MapUtils.getDouble(gjMap, "score"), 2, 2);
                } else {
                    zhuangCount++;
                    zhuangScore = MapUtils.getDouble(gjMap, "score");
                }
            }
        }
        Double gjTotal = DoubleUtils.add(DoubleUtils.add(mul(luCount, luScore), mul(biaoCount, biaoScore), mul(zhuangCount, zhuangScore)),
                DoubleUtils.add(mul(canLuCount, canLuScore), mul(canBiaoCount, canBiaoScore), mul(canZhuangCount, canZhuangScore)), 0D);
        Integer canFuCount = 0;
        Double canFuScore = 0D;
        Integer fuCount = 0;
        Double fuScore = 0D;
        Integer canYouCount = 0;
        Double canYouScore = 0D;
        Integer youCount = 0;
        Double youScore = 0D;
        for (Map<String, Object> sjMap : sjList) {
            if (Constant.PRIZE_LOTUS.equals(sjMap.get("awardName"))) {
                if (Constant.JOIN_TYPE_CAN.equals(sjMap.get("joinType"))) {
                    canFuCount++;
                    canFuScore = DoubleUtils.div(MapUtils.getDouble(sjMap, "score"), 2, 2);
                } else {
                    fuCount++;
                    fuScore = MapUtils.getDouble(sjMap, "score");
                }
            } else {
                if (Constant.JOIN_TYPE_CAN.equals(sjMap.get("joinType"))) {
                    canYouCount++;
                    canYouScore = DoubleUtils.div(MapUtils.getDouble(sjMap, "score"), 2, 2);
                } else {
                    youCount++;
                    youScore = MapUtils.getDouble(sjMap, "score");
                }
            }
        }
        Double sjTotal = DoubleUtils.add(mul(fuCount, fuScore), mul(youCount, youScore), DoubleUtils.add(mul(canFuCount, canFuScore), mul(canYouCount, canYouScore), 0D));
        Double total = DoubleUtils.add(gjTotal, sjTotal, 0D);
        Map<String, Object> resultMap = new HashedMap();
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
        setProType(param);
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
            put("proList", proList);
            put("comList", comList);
        }};
        sjhjMap.put("sjList", list);
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
                        if (null != list2.get(j).get("score")) {
                            d2 = MapUtils.getDouble(list2.get(j), "score");
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
                for (int index : listInt) {
                    if (index >= list1.size()) {
                        list1.remove(list1.size() - 1);
                    } else {
                        list1.remove(index);
                    }
                }
            }
            if (null != proInt && proInt.size() > 0) {
                for (int index : proInt) {
                    if (index >= list2.size()) {
                        list2.remove(list2.size() - 1);
                    } else {
                        list2.remove(index);
                    }
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
        setProType(param);
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
     *
     * @param param
     * @return
     */
    public Map<String, Object> listUndesirable(Map<String, Object> param) {
        setProType(param);
        Map<String, Object> resultMap = new HashedMap();
        param.put("type", "company");
        resultMap.put("unCompany", tbReviewDiffMapper.queryReviewDiff(param));
        param.put("type", "project");
        resultMap.put("unProject", tbReviewDiffMapper.queryReviewDiff(param));
        resultMap.put("undesirable", prizeMapper.queryUndersiableList(param));
        return resultMap;
    }

    /**
     * 查询企业诚信列表（无过滤规则即查询全部）
     *
     * @param param
     * @return
     */
    public Map<String, Object> listCompanyAward(Map<String, Object> param) {
        String reqType = MapUtils.getString(param, "reqType");
        Map<String, Object> resultMap = new HashedMap();
        //企业荣誉
        List<Map<String, Object>> companyAwards = new ArrayList<>();
        Map aqrz = tbAqrzHunanMapper.queryCompanyAqrz(param);
        if (MapUtils.isNotEmpty(aqrz)) {
            companyAwards.add(aqrz);
        }
        param.put("type", "company");
        companyAwards.addAll(tbReviewFineMapper.queryCompanyReviewFineList(param));
        resultMap.put("companyAwards", companyAwards);
        //工程获奖
        List<Map<String, Object>> awardsGroupList = new ArrayList<>();
//        if ("湖南省".equals(param.get("source")) && ("APP".equals(reqType) || "WAP".equals(reqType))) {
//            //优良工地
//            param.put("type", "project");
//            awardsGroupList.add(new HashedMap() {{
//                put("awards", "湖南省年度考评优良工地");
//                put("values", tbReviewFineMapper.queryCompanyReviewFineList(param));
//            }});
//        }
//        if ("湖南省".equals(param.get("source")) && "PC".equals(reqType)) {
//            param.put("type", "project");
//            awardsGroupList.addAll(tbReviewFineMapper.queryCompanyReviewFineList(param));
//        }
        if ("APP".equals(reqType) || "WAP".equals(reqType)) {
            List<Map<String, Object>> awards = tbAwardNationwideMapper.queryCompanyAwardsGroup(param);
            if (null != awards && awards.size() > 0) {
                Map<String, Object> groupMap;
                for (Map<String, Object> map : awards) {
                    groupMap = new HashedMap();
                    groupMap.put("awards", map.get("awardName"));
                    if (null != map.get("source")) {
                        groupMap.put("awards", map.get("source").toString() + map.get("awardName"));
                    }
                    groupMap.put("values", tbAwardNationwideMapper.queryCompanyAwards(map));
                    awardsGroupList.add(groupMap);
                }
            }
            resultMap.put("projectAwards", awardsGroupList);
        } else {
            awardsGroupList.addAll(tbAwardNationwideMapper.queryCompanyAwards(param));
            resultMap.put("projectAwards", awardsGroupList);
        }
        //不良记录
        List<Map<String, Object>> under = new ArrayList<>();
        under.addAll(tbReviewDiffMapper.queryCompanyReviewDiff(param));
        under.addAll(prizeMapper.queryCompanyUndersiableList(param));
        resultMap.put("under", under);
        //公路信用等级
        resultMap.put("highway", tbHighwayCreditMapper.queryListCompanyHighway(param));
        //水利信用等级
        resultMap.put("shuili", tbShuiliCreditMapper.queryListCompanyShuiliCredit(param));
        return resultMap;
    }

    /**
     * 查询企业诚信详情
     *
     * @param param
     * @return
     */
    public Map<String, Object> detailCompanyAward(Map<String, Object> param) {
        Map<String, Object> awardNationwide = tbAwardNationwideMapper.queryCompanyAwardDetail(param);
        if (null == awardNationwide) {
            return awardNationwide;
        }
        StringBuffer sbfOrg;
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "unitOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "unitOrg"));
            awardNationwide.put("unitOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "buildOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "buildOrg"));
            awardNationwide.put("buildOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "superOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "superOrg"));
            awardNationwide.put("superOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "exploreOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "exploreOrg"));
            awardNationwide.put("exploreOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "designOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "designOrg"));
            awardNationwide.put("designOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "checkOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "checkOrg"));
            awardNationwide.put("checkOrg", this.setOrgs(sbfOrg.toString()));
        }
        if (StringUtils.isNotEmpty(MapUtils.getString(awardNationwide, "joinOrg"))) {
            sbfOrg = new StringBuffer(MapUtils.getString(awardNationwide, "joinOrg"));
            awardNationwide.put("joinOrg", this.setOrgs(sbfOrg.toString()));
        }
        return awardNationwide;
    }

    /**
     * 设置项目类别
     *
     * @param param
     */
    private void setProType(Map<String, Object> param) {
        if ("市政".equals(param.get("projType"))) {
            param.put("reprojType", "市政工程");
        } else if ("建筑工程".equals(param.get("projType"))) {
            param.put("reprojType", "建筑工程");
        } else if ("市政工程".equals(param.get("projType"))) {
            param.put("reprojType", "市政工程");
        }
    }

    /**
     * 设置单位
     *
     * @param org
     * @return
     */
    private List<Map<String, Object>> setOrgs(String org) {
        List<Map<String, Object>> orgs = new ArrayList<>();
        String[] splitOrgs = org.split(",");
        Map<String, Object> orgMap;
        for (String unit : splitOrgs) {
            orgMap = new HashedMap();
            String[] splitUnit = unit.split("/");
            orgMap.put("comName", splitUnit[0]);
            if (splitUnit.length > 1) {
                orgMap.put("comId", splitUnit[1]);
            }
            orgs.add(orgMap);
        }
        return orgs;
    }
}
