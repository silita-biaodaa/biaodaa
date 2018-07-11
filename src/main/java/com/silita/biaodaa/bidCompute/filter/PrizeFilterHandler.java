package com.silita.biaodaa.bidCompute.filter;

import com.silita.biaodaa.bidCompute.condition.PrizeBean;
import com.silita.biaodaa.dao.BidEvaluationMethodMapper;
import com.silita.biaodaa.utils.DoubleUtils;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.Name;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhushuai on 18/7/3.
 */
@Name
@Component
public class PrizeFilterHandler extends BaseFilterHandler<PrizeBean> {

    private static final Logger logger = LoggerFactory.getLogger(PrizeFilterHandler.class);

    @Autowired
    BidEvaluationMethodMapper bidEvaluationMethodMapper;

    @Override
    String getFilterName() {
        return "获奖情况";
    }

    // TODO: 18/7/3 处理资源数据
    @Override
    Double doHandler(Map resourceMap) {
        Map<String, Object> comMap = (Map<String, Object>) resourceMap.get("comInfo");
        Map<String, Object> yearMap = null;
        try {
            yearMap = this.getYears(comMap);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // TODO: 将分值存入Map
        Map<String, Object> scoreMap = this.getPrizeScoreMap();
        // TODO: 获取国家级奖项并去重
        Map<String, Object> nationMap = this.getDeWeightList(this.getNationCert(yearMap, comMap), scoreMap);
        // TODO: 获取省级奖项并去重
        Map<String, Object> provinceMap = this.getDeWeightList(this.getProvinceCert(yearMap, comMap), scoreMap);
        // TODO: 将国家级奖项和省级奖项去重
        this.getCertList(nationMap, provinceMap,scoreMap);
        // TODO: 开始计算
        List<Integer> saveTableList = new ArrayList<>();
        Double natoTotal = computeNation(nationMap, saveTableList);
        Double proTotal = computeProvince(provinceMap, saveTableList);
        Double total = 0D;
        if(null != resourceMap.get("total")){
            total = MapUtils.getDouble(resourceMap,total);
        }
        total= DoubleUtils.add(natoTotal, proTotal, total);
        resourceMap.put("total", total);
        resourceMap.put("saveList", saveTableList);
        return total;
    }

    // TODO: 获取全部奖项颁发的最新时间
    private Map<String, Object> getYears(Map<String, Object> param) throws ParseException {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        List<Map<String, Object>> certList = null;
        if (null != param || null != param.get("yearList")) {
            certList = (List<Map<String, Object>>) param.get("yearList");
        } else {
            certList = bidEvaluationMethodMapper.queryCertYears();
        }
        List<String> yearList = new ArrayList<>();
        if (null == certList) {
            yearList.add(sdf.format(new Date()));
        } else {
            for (Map<String, Object> map : certList) {
                String date = null;
                if (map.get("years").toString().contains("～")) {
                    date = map.get("years").toString().split("～")[1];
                } else if (map.get("years").toString().contains("-")) {
                    date = map.get("years").toString().split("-")[1];
                } else {
                    date = map.get("years").toString();
                }
                yearList = new ArrayList<>();
                if ("鲁班奖".equals(map.get("mateName"))) {
                    String dateN = ProjectAnalysisUtil.getStrNumber(date);
                    Date lastDate = sdf.parse(date);
                    Date endDate = null;
                    String endStr = null;
                    if (Integer.valueOf(dateN) % 2 == 0) {
                        endDate = MyDateUtils.getDayBefore(lastDate, -1);
                        endStr = sdf.format(endDate);
                        yearList.add(endStr + "～" + date);
                        yearList.add(date + "-" + endStr + "度");
                    } else {
                        endDate = MyDateUtils.getDayBefore(lastDate, 1);
                        endStr = sdf.format(endDate);
                        yearList.add(endStr + "～" + date);
                        yearList.add(endStr + "-" + dateN + "年度");
                    }
                    resultMap.put("鲁班奖", yearList);
                } else {
                    yearList.add(date);
                    resultMap.put(map.get("mateName").toString(), yearList);
                }
            }
        }
        return resultMap;
    }

    // TODO: 获取国家级奖项
    private Map<String, Object> getNationCert(Map<String, Object> yearMap, Map<String, Object> comMap) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("joinType", "承建单位");
        param.put("projType", comMap.get("projType"));
        param.put("prizeType", "gjjhj");
        param.put("list", comMap.get("srcUuidList"));
        List<Map<String, Object>> nationList = new ArrayList<>();
        Integer lupanCount = 0;
        //鲁班奖
        if(null != yearMap.get("鲁班奖")){
            param.put("years", yearMap.get("鲁班奖"));
            param.put("mateName", "鲁班奖");
            List<Map<String, Object>> lubanList = bidEvaluationMethodMapper.queryCertPrizeList(param);
            if (null != lubanList && lubanList.size() > 0) {
                lupanCount = lubanList.size();
                nationList.addAll(lubanList);
            }
        }
        //全国建设工程项目施工安全生产标准化工地
        Integer buildCount = 0;
        if(null != yearMap.get("全国建设工程项目施工安全生产标准化工地")){
            param.put("years", yearMap.get("全国建设工程项目施工安全生产标准化工地"));
            param.put("mateName", "全国建设工程项目施工安全生产标准化工地");
            List<Map<String, Object>> buildList = bidEvaluationMethodMapper.queryCertPrizeList(param);
            if (null != buildList && buildList.size() > 0) {
                buildCount = buildList.size();
                nationList.addAll(buildList);
            }
        }
        //全国装饰奖
        Integer decorateCount = 0;
        if(null != yearMap.get("全国建筑工程装饰奖")){
            param.put("years", yearMap.get("全国建筑工程装饰奖"));
            param.put("mateName", "全国建筑工程装饰奖");
            List<Map<String, Object>> decoList = bidEvaluationMethodMapper.queryCertPrizeList(param);
            if (null != decoList && decoList.size() > 0) {
                decorateCount = decoList.size();
                nationList.addAll(decoList);
            }
        }
        resultMap.put("全国建筑工程装饰奖", decorateCount);
        resultMap.put("certList", nationList);
        resultMap.put("全国建设工程项目施工安全生产标准化工地", buildCount);
        resultMap.put("鲁班奖", lupanCount);
        return resultMap;
    }

    // TODO: 获取省级奖项
    private Map<String, Object> getProvinceCert(Map<String, Object> yearMap, Map<String, Object> comMap) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("joinType", "承建单位");
        param.put("projType", comMap.get("projType"));
        param.put("prizeType", "sjhj");
        param.put("list", comMap.get("srcUuidList"));
        List<Map<String, Object>> provinceList = new ArrayList<>();
        //芙蓉奖
        Integer lurotCount = 0;
        if(null != yearMap.get("芙蓉奖")){
            param.put("years", yearMap.get("芙蓉奖"));
            param.put("mateName", "芙蓉奖");
            List<Map<String, Object>> lurotList = bidEvaluationMethodMapper.queryCertPrizeList(param);
            if (null != lurotCount && lurotList.size() > 0) {
                lurotCount = lurotList.size();
                provinceList.addAll(lurotList);
            }
        }
        //省优工程
        Integer superCount = 0;
        if(null != yearMap.get("省优工程")){
            param.put("years", yearMap.get("省优工程"));
            param.put("mateName", "省优工程");
            List<Map<String, Object>> superList = bidEvaluationMethodMapper.queryCertPrizeList(param);
            if (null != superList && superList.size() > 0) {
                superCount = superList.size();
                provinceList.addAll(superList);
            }
        }
        resultMap.put("芙蓉奖", lurotCount);
        resultMap.put("省优工程", superCount);
        resultMap.put("certList", provinceList);
        return resultMap;
    }

    // TODO: 存奖项分值
    private Map<String, Object> getPrizeScoreMap() {
        Map<String, Object> scoreMap = new HashMap<>();
        //国家奖项
        scoreMap.put("鲁班奖", this.config.getLubanPrize());
        scoreMap.put("全国建设工程项目施工安全生产标准化工地", this.config.getBuildPrize());
        scoreMap.put("全国建筑工程装饰奖", this.config.getDecoratePrize());
        //省级奖项
        scoreMap.put("芙蓉奖", this.config.getLotusPrize());
        scoreMap.put("省优工程", this.config.getSuperPrize());
        return scoreMap;
    }

    // TODO: 奖项去重
    private Map<String, Object> getDeWeightList(Map<String, Object> nationMap, Map<String, Object> scoreMap) {
        if (null == nationMap.get("certList")) {
            return nationMap;
        }
        List<Map<String, Object>> nationList = (List<Map<String, Object>>) nationMap.get("certList");
        Integer count = 0;
        for (int i = 0; i < nationList.size() - 1; i++) {
            for (int j = nationList.size() - 1; j > i; j--)
                if (nationList.get(j).get("projName").toString().equals(nationList.get(i).get("projName").toString())) {
                    Double d1 = (Double) scoreMap.get(nationList.get(j).get("mateName").toString());
                    Double d2 = (Double) scoreMap.get(nationList.get(i).get("mateName").toString());
                    if (d1.compareTo(d2) > 0) {
                        count = (Integer) nationMap.get(nationList.get(i).get("mateName").toString());
                        count = count == 0 ? 0 : count - 1;
                        nationMap.put(nationList.get(i).get("mateName").toString(), count);
                        nationList.remove(i);
                    } else if (d1.compareTo(d2) < 0) {
                        count = (Integer) nationMap.get(nationList.get(j).get("mateName").toString());
                        count = count == 0 ? 0 : count - 1;
                        nationMap.put(nationList.get(j).get("mateName").toString(), count);
                        nationList.remove(j);
                    } else {
                        count = (Integer) nationMap.get(nationList.get(j).get("mateName").toString());
                        count = count == 0 ? 0 : count - 1;
                        nationMap.put(nationList.get(j).get("mateName").toString(), count);
                        nationList.remove(j);
                    }
                }
        }
        nationMap.put("certList", nationList);
        return nationMap;
    }

    // TODO: 将国家级和省级奖项去重
    private void getCertList(Map<String, Object> nationMap, Map<String, Object> provinceMap, Map<String, Object> scoreMap) {
        List<Map<String, Object>> nationList = (List<Map<String, Object>>) nationMap.get("certList");
        List<Map<String, Object>> provinceList = (List<Map<String, Object>>) provinceMap.get("certList");
        if (nationList.size() > 0 && provinceList.size() > 0) {
            //存放分值少的奖项
            List<Map<String, Object>> certList = new ArrayList<>();
            for (Map<String, Object> nat : nationList) {
                for (Map<String, Object> pro : provinceList) {
                    if (nat.get("projName").toString().equals(pro.get("projName"))) {
                        Double d1 = (Double) scoreMap.get(nat.get("mateName").toString());
                        Double d2 = (Double) scoreMap.get(pro.get("mateName").toString());
                        if (d1.compareTo(d2) > 0) {
                            certList.add(pro);
                        } else if (d1.compareTo(d2) < 0) {
                            certList.add(nat);
                        } else {
                            certList.add(pro);
                        }
                    }
                }
            }

            Iterator<Map<String, Object>> certIt = null;
            //remove
            Integer count = 0;
            if (null != certList && certList.size() > 0) {
                for (Map<String, Object> map : certList) {
                    if ("gjjhj".equals(map.get("prizeType").toString())) {
                        certIt = nationList.iterator();
                    } else {
                        certIt = provinceList.iterator();
                    }
                    while (certIt.hasNext()) {
                        Map<String, Object> cet = certIt.next();
                        if (map.get("projName").toString().equals(cet.get("projName").toString())
                                && map.get("mateName").toString().equals(cet.get("mateName").toString())) {
                            if ("gjjhj".equals(cet.get("prizeType").toString())) {
                                count = (Integer) nationMap.get(cet.get("mateName").toString());
                                count = count == 0 ? 0 : count - 1;
                                nationMap.put(cet.get("mateName").toString(), count);
                            } else {
                                count = (Integer) provinceMap.get(cet.get("mateName").toString());
                                count = count == 0 ? 0 : count - 1;
                                provinceMap.put(cet.get("mateName").toString(), count);
                            }
                            certIt.remove();
                        }
                    }
                }
            }
            nationMap.put("certList", nationList);
            provinceMap.put("certList", provinceList);
        }
    }

    // TODO: 计算国家级奖项分值
    private Double computeNation(Map<String, Object> nationMap, List<Integer> saveTable) {
        Double luCount = 0D;
        Double buCount = 0D;
        Double deCount = 0D;
        //国家--鲁班奖
        List<Map<String, Object>> lubanList = new ArrayList<>();
        //国家--建筑奖
        List<Map<String, Object>> buildList = new ArrayList<>();
        //国家--装饰奖
        List<Map<String, Object>> desList = new ArrayList<>();
        if (null != nationMap.get("certList")) {
            List<Map<String, Object>> naticnList = (List<Map<String, Object>>) nationMap.get("certList");
            for (Map<String, Object> nation : naticnList) {
                if ("鲁班奖".equals(nation.get("mateName").toString())) {
                    lubanList.add(nation);
                } else if ("全国建设工程项目施工安全生产标准化工地".equals(nation.get("mateName").toString())) {
                    buildList.add(nation);
                } else if ("全国建筑工程装饰奖".equals(nation.get("mateName").toString())) {
                    desList.add(nation);
                }
            }

            Integer count = 0;
            if (null != lubanList && lubanList.size() > 0) {
                count = (Integer) nationMap.get("鲁班奖");
                if (count > this.config.getNationPrizeCon()) {
                    luCount = DoubleUtils.mul(Double.valueOf(this.config.getNationPrizeCon().toString()), this.config.getLubanPrize());
                    for (int i = 0; i < this.config.getNationPrizeCon(); i++) {
                        saveTable.add((Integer) lubanList.get(i).get("id"));
                    }
                } else {
                    luCount = DoubleUtils.mul(Double.valueOf(count.toString()), this.config.getLubanPrize());
                    for (int i = 0; i < count; i++) {
                        saveTable.add((Integer) lubanList.get(i).get("id"));
                    }
                }

            }
            if (null != buildList && buildList.size() > 0) {
                count = (Integer) nationMap.get("全国建设工程项目施工安全生产标准化工地");
                if (count > this.config.getNationPrizeCon()) {
                    buCount = DoubleUtils.mul(Double.valueOf(this.config.getNationPrizeCon().toString()), this.config.getBuildPrize());
                    for (int i = 0; i < this.config.getNationPrizeCon(); i++) {
                        saveTable.add((Integer) buildList.get(i).get("id"));
                    }
                } else {
                    buCount = DoubleUtils.mul(Double.valueOf(count.toString()), this.config.getBuildPrize());
                    for (int i = 0; i < count; i++) {
                        saveTable.add((Integer) buildList.get(i).get("id"));
                    }
                }
            }
            if (null != desList && desList.size() > 0) {
                count = (Integer) nationMap.get("全国建筑工程装饰奖");
                if (count > this.config.getNationPrizeCon()) {
                    buCount = DoubleUtils.mul(Double.valueOf(this.config.getNationPrizeCon().toString()), this.config.getDecoratePrize());
                    for (int i = 0; i < this.config.getNationPrizeCon(); i++) {
                        saveTable.add((Integer) desList.get(i).get("id"));
                    }
                } else {
                    buCount = DoubleUtils.mul(Double.valueOf(count.toString()), this.config.getDecoratePrize());
                    for (int i = 0; i < count; i++) {
                        saveTable.add((Integer) desList.get(i).get("id"));
                    }
                }
            }
        }
        Double total = luCount + buCount + deCount;
        return total;
    }

    // TODO: 计算省级奖项分值
    private Double computeProvince(Map<String, Object> proMap, List<Integer> saveTable) {
        Double lotusCount = 0D;
        Double superCount = 0D;
        if (null != proMap.get("certList")) {
            List<Map<String, Object>> lotusList = new ArrayList<>();
            List<Map<String, Object>> superList = new ArrayList<>();
            List<Map<String, Object>> certList = (List<Map<String, Object>>) proMap.get("certList");
            for (Map<String, Object> proM : certList) {
                if ("芙蓉奖".equals(proM.get("mateName").toString())) {
                    lotusList.add(proM);
                } else if ("省优工程".equals(proM.get("mateName").toString())) {
                    superList.add(proM);
                }
            }

            Integer count = 0;
            if (null != lotusList && lotusList.size() > 0) {
                count = (Integer) proMap.get("芙蓉奖");
                if (count > this.config.getProvinceCount()) {
                    Integer cou = count - this.config.getProvinceCount();
                    lotusCount = DoubleUtils.add(
                            DoubleUtils.mul(Double.valueOf(this.config.getProvinceCount().toString()), this.config.getLotusPrize()),
                            DoubleUtils.mul(DoubleUtils.mul(Double.valueOf(cou.toString()), this.config.getLotusPrize()),
                                    DoubleUtils.div(this.config.getOverPercent(), 100D, 4)), 0D);
                } else {
                    lotusCount = DoubleUtils.mul(Double.valueOf(count.toString()), this.config.getLotusPrize());
                }
                for (Map<String, Object> loMap : lotusList) {
                    saveTable.add((Integer) loMap.get("id"));
                }
            }
            if (null != superList && superList.size() > 0) {
                count = (Integer) proMap.get("省优工程");
                if (count > this.config.getProvinceCount()) {
                    Integer cou = count - this.config.getProvinceCount();
                    superCount = DoubleUtils.add(
                            DoubleUtils.mul(Double.valueOf(this.config.getProvinceCount().toString()), this.config.getSuperPrize()),
                            DoubleUtils.mul(DoubleUtils.mul(Double.valueOf(cou.toString()), this.config.getSuperPrize()),
                                    DoubleUtils.div(this.config.getOverPercent(), 100D, 4)), 0D);
                } else {
                    superCount = DoubleUtils.mul(Double.valueOf(count.toString()), this.config.getSuperPrize());
                }
                for (Map<String, Object> sup : superList) {
                    saveTable.add((Integer) sup.get("id"));
                }
            }
        }
        Double total = DoubleUtils.add(lotusCount, superCount, 0D);
        return total;
    }

}
