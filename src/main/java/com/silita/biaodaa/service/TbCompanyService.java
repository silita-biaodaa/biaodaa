package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.cache.GlobalCache;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.common.RedisConstantInterface.PRO_OVER_TIME;

/**
 * Created by zhangxiahui on 18/4/4.
 */
@Component
public class TbCompanyService {

    Logger logger = Logger.getLogger(TbCompanyService.class);

    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @Autowired
    TbCompanyQualificationMapper tbCompanyQualificationMapper;

    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;

    @Autowired
    AptitudeDictionaryMapper aptitudeDictionaryMapper;

    @Autowired
    CertBasicMapper certBasicMapper;

    @Autowired
    TbSafetyCertificateMapper tbSafetyCertificateMapper;

    @Autowired
    TbPersonMapper tbPersonMapper;

    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;

    @Autowired
    ElasticseachService elasticseachService;

    @Autowired
    ProjectService projectService;

    @Autowired
    LawService lawService;

    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Autowired
    QualService qualService;

    @Autowired
    TbUnderConstructMapper tbUnderConstructMapper;

    private GlobalCache globalCache = GlobalCache.getGlobalCache();

    private ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();


    public PageInfo queryCompanyList(Page page, String keyWord) {
        List<TbCompany> list = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbCompanyMapper.queryCompanyList(keyWord);
        PageInfo pageInfo = new PageInfo(list);

        return pageInfo;
    }

    public TbCompany getCompany(String comId) {
        TbCompany tbCompany = tbCompanyMapper.getCompany(comId);
        Map<String, Object> param = new HashMap<>();
        param.put("comId", comId);
        param.put("userId", VisitInfoHolder.getUid());
        Integer num = tbCompanyMapper.getColleCount(param);
        if (num > 0) {
            tbCompany.setCollected(true);
        }
        // TODO: 18/5/7 存续状态写死
        tbCompany.setSubsist("存续");
//        if (tbCompany.getRegisAddress() != null && tbCompany.getRegisAddress().indexOf("湖南") > -1) {
//            String url = tbCompanyMapper.getCompanyUrl(tbCompany.getComId());
//            tbCompany.setUrl(url);
//        }
        return setCompanyDetail(tbCompany);
    }

    public Map<String, List<TbCompanyQualification>> queryCompanyQualification(String comId) {
        Map<String, List<TbCompanyQualification>> qualMap = new HashMap<>();
        if (comId != null) {
            // TODO: 18/4/10 需要关联标准资质表
            List<TbCompanyQualification> list = tbCompanyQualificationMapper.queryCompanyQualification(comId);
            for (TbCompanyQualification qual : list) {
                if (qual.getQualType() != null && qual.getRange() != null) {
                    String key = qual.getQualType();
                    List<TbCompanyQualification> qualList;
                    if (qualMap.get(key) != null) {
                        qualList = qualMap.get(key);
                    } else {
                        qualList = new ArrayList<>();

                    }
                    String[] range = qual.getRange().split(",");
                    if (range.length < 2) {
                        range = qual.getRange().split(";");
                    }
                    if (range.length < 2) {
                        range = qual.getRange().split("\\|");
                    }

                    if (range.length >= 2) {
                        for (int i = 0; i < range.length; i++) {
                            TbCompanyQualification qualT = qual.clon();
                            qualT.setQualName(range[i]);
                            qualList.add(qualT);
                        }
                    } else {
                        qual.setQualName(range[0]);
                        qualList.add(qual);
                    }
                    //排序

                    qualMap.put(key, qualList);
                }
            }

        }
        return qualMap;
    }

    public List<Map<String, Object>> queryQualList(String comId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, List<TbCompanyQualification>> qualMap = queryCompanyQualification(comId);
        Set<String> set = qualMap.keySet();
        for (String key : set) {
            Map<String, Object> map = new HashMap<>();
            map.put("qualType", getQualType(key));
            map.put("list", qualService.sortCompanyQual(qualMap.get(key),key));
            list.add(map);
        }
        return qualService.sortQualType2(list);
    }

    public List<Map<String, Object>> getCompanyPersonCate(String comId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        if (comId != null) {
            if ("0".equals(comId)) {
                List<Map<String, Object>> categoryList = new ArrayList<>();
                String[] str = {"一级注册建筑师", "一级临时注册建造师", "一级注册建造师", "一级注册结构工程师",
                        "二级注册建筑师", "二级临时注册建造师", "二级注册建造师", "二级注册结构工程师",
                        "注册公用设备工程师（动力）", "注册公用设备工程师（暖通空调）", "注册公用设备工程师（给水排水）",
                        "注册化工工程师", "注册土木工程师（岩土）", "注册电气工程师（供配电）", "注册电气工程师（发输变电）",
                        "注册监理工程师", "注册造价工程师"};
                for (int i = 0; i < str.length; i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", str[i]);
                    map.put("num", 0);
                    categoryList.add(map);
                }
                return categoryList;
            }
            TbCompany company = getCompany(comId);
            if (company != null) {
                param.put("comId", comId);
                param.put("comName", company.getComName());
                String province = company.getRegisAddress();
                String tableCode = "hunan";
                if (province.indexOf("湖南省") > -1) {
                    province = "湖南省";
                }
                if (province != null && !"".equals(province)) {
                    tableCode = getProvinceCode(province);
                    if (tableCode == null) {
                        return list;
                    }
                }
                param.put("tableCode", tableCode);
                list = tbPersonQualificationMapper.getCompanyPersonCate(param);
            }
        }
        return list;
    }

    public PageInfo queryCompanyPerson(Page page, Map<String, Object> param) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbPersonQualification> list = tbPersonQualificationMapper.queryCompanyPerson(param);
        PageInfo pageInfo = new PageInfo(this.setIsUnder(list));
        return pageInfo;
    }

    public List<CompanyQual> getCompanyQual() {
        List<AptitudeDictionary> list = aptitudeDictionaryMapper.queryAptitude();
        Map<String, CompanyQual> map = new HashMap<>();
        List<CompanyQual> companyQualList = new ArrayList<>();
        for (AptitudeDictionary ap : list) {
            CompanyQual qual = new CompanyQual();
            qual.setName(ap.getMajorname());
            qual.setCode(ap.getMajoruuid());
            qual.setList(getZzTpyeList(ap.getMajoruuid(), ap.getZztype()));
            if (map.get(ap.getAptitudename()) != null) {
                CompanyQual pany = map.get(ap.getAptitudename());
                List<CompanyQual> panyList = pany.getList();
                if (panyList == null) {
                    panyList = new ArrayList<>();
                }
                panyList.add(qual);
            } else {
                CompanyQual pany = new CompanyQual();
                List<CompanyQual> panyList = new ArrayList<>();
                panyList.add(qual);
                pany.setName(ap.getAptitudename());
                pany.setCode(ap.getAptitudecode());
                pany.setList(panyList);
                map.put(ap.getAptitudename(), pany);
            }
        }
        for (CompanyQual companyQual : map.values()) {
            companyQualList.add(companyQual);
        }
        return companyQualList;
    }

    public List<CompanyQual> getZzTpyeList(String majoruuid, String zzType) {
        List<CompanyQual> list = new ArrayList<>();
        if ("1".equals(zzType)) {
            //等级：0=特级，1=一级，2=二级，11=一级及以上，21=二级及以上，31=三级及以上，-1=甲级，-2=乙级，-3=丙级
            CompanyQual qual0 = new CompanyQual();
            qual0.setName("特级");
            qual0.setCode(majoruuid + "/" + "0");

            CompanyQual qual1 = new CompanyQual();
            qual1.setName("一级");
            qual1.setCode(majoruuid + "/" + "1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("二级");
            qual2.setCode(majoruuid + "/" + "2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("三级");
            qual3.setCode(majoruuid + "/" + "3");

            CompanyQual qual11 = new CompanyQual();
            qual11.setName("一级及以上");
            qual11.setCode(majoruuid + "/" + "11");

            CompanyQual qual21 = new CompanyQual();
            qual21.setName("二级及以上");
            qual21.setCode(majoruuid + "/" + "21");

            CompanyQual qual31 = new CompanyQual();
            qual31.setName("三级及以上");
            qual31.setCode(majoruuid + "/" + "31");

            list.add(qual0);
            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
            list.add(qual11);
            list.add(qual21);
            list.add(qual31);
        } else if ("2".equals(zzType)) {
            CompanyQual qual1 = new CompanyQual();
            qual1.setName("甲级");
            qual1.setCode(majoruuid + "/" + "-1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("乙级");
            qual2.setCode(majoruuid + "/" + "-2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("丙级");
            qual3.setCode(majoruuid + "/" + "-3");

            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
        }
        return list;
    }


    public List<Map<String, String>> getIndustry() {
        return aptitudeDictionaryMapper.getIndustry();
    }

    public PageInfo filterCompany(Page page, Map<String, Object> param, String levelRank) {
        //判断是否*****级以上
        param.put("zzLevel", "default");
        if (null != param.get("qualCode") && StringUtils.isNotEmpty(param.get("qualCode").toString())) {
            String qualCode = param.get("qualCode").toString();
            String[] qualCodes = qualCode.split(",");
            List<String> rangeList = new ArrayList<>();
            String zzLevel = null;
            List<Map> rangeMapList = new ArrayList<>();
            Map rangeMap = null;
            for (String str : qualCodes) {
                rangeList = new ArrayList<>();
                rangeMap = new HashMap();
                if (str.contains("/")) {
                    zzLevel = str.substring(analysisUtil.getIndex(str, "/") + 1, str.length());
                } else {
                    zzLevel = "default";
                }
                rangeList.addAll(this.getQualCode(str, zzLevel));
                rangeMap.put("rangeList", rangeList);
                rangeMapList.add(rangeMap);
            }
            param.put("zzLevel", "level");
            param.put("range", rangeMapList);
        }
        //地区
        if (null != param.get("regisAddress")) {
            getRegisAddress(param);
        }
        List<TbCompany> list = new ArrayList<>();
        Map<String, CertBasic> certBasicMap = getCertBasicMap();
        Map<String, TbSafetyCertificate> safetyCertificateMap = getSafetyCertMap();
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        if (levelRank != null && levelRank.length() == 4) {
            String level = levelRank.substring(0, 2);
            String rank = levelRank.substring(2, 4);
            param.put("level", level);
            param.put("rank", rank);
            list = tbCompanyMapper.filterSecureCompany(param);
        } else {
            list = tbCompanyMapper.filterCompany(param);
        }

        TbCompanyInfo companyInfo = null;
        String tabCode = null;
        for (TbCompany company : list) {
            tabCode = CommonUtil.getCode(company.getRegisAddress());
            companyInfo = tbCompanyInfoMapper.queryDetailByComName(company.getComName(), tabCode);
            if (null != companyInfo) {
                if (null != companyInfo.getPhone()) {
//                    company.setPhone(companyInfo.getPhone().split(";")[0].trim());
                    company.setPhone(this.solPhone(companyInfo.getPhone(), "replace"));
                }
                if (null == company.getRegisCapital() && null != companyInfo.getRegisCapital()) {
                    company.setRegisCapital(companyInfo.getRegisCapital());
                }
            }
            if (company.getComName() != null && company.getBusinessNum() != null) {
                CertBasic certBasic = certBasicMap.get(company.getComName() + "|" + company.getBusinessNum());
                if (certBasic != null) {
                    //company.setSubsist(certBasic.getRegisterstatus());
                    company.setComRange(certBasic.getRunscope());
                }
                TbSafetyCertificate tbSafetyCertificate = safetyCertificateMap.get(company.getComName());
                if (tbSafetyCertificate != null) {
                    company.setCertNo(tbSafetyCertificate.getCertNo());
                    company.setCertDate(tbSafetyCertificate.getCertDate());
                    company.setValidDate(tbSafetyCertificate.getValidDate());
                }
                // TODO: 18/4/26  存续状态暂时写死
                company.setSubsist("存续");
            }
            if (company.getRegisAddress() != null && company.getRegisAddress().indexOf("湖南") > -1) {
                String url = tbCompanyMapper.getCompanyUrl(company.getComId());
                company.setUrl(url);
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public String getAreaCode(String name) {
        return tbCompanyMapper.getAreaCode(name);
    }


    public List<Map<String, Object>> getArea() {
        List<Map<String, Object>> areaList = new ArrayList<>();
        List<Map<String, String>> list = tbCompanyMapper.queryProvinceList();
        for (Map<String, String> map : list) {
            Map<String, Object> area = new HashMap<>();
            List<String> cityListT = tbCompanyMapper.queryCityList(map.get("id"));
            List<String> cityList = new ArrayList<>();
            for (String city : cityListT) {
                cityList.add(city.replace(map.get("display_name"), ""));
            }
            if ("北京市".equals(map.get("display_name"))) {
                cityList = new ArrayList<>();
                for (String city : cityListT) {
                    cityList.add(city.replace("北京", ""));
                }
            }
            if ("天津市".equals(map.get("display_name"))) {
                cityList = new ArrayList<>();
                for (String city : cityListT) {
                    cityList.add(city.replace("天津", ""));
                }
            }

            area.put("name", map.get("display_name"));
            area.put("list", cityList);
            areaList.add(area);
        }
        return areaList;
    }

    public Map<String, Object> getCompanyReputation(String comId) {
        Map<String, Object> resultMap = new HashMap<>();
        Double score = 0d;
        Double secur = 0d;

        TbCompany company = tbCompanyMapper.getCompanyOrgCode(comId);
        if (company != null) {
            company = setCompanyDetail(company);
            List<String> srcUuids = tbCompanyMapper.getCertSrcUuidByName(company.getComName());
            if ((null == srcUuids || srcUuids.size() <= 0) && null != company.getOrgCode()) {
                srcUuids = tbCompanyMapper.getCertSrcUuid(company.getOrgCode());
            }
            if (srcUuids != null && srcUuids.size() > 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("list", srcUuids);
                Map<String, Object> anrz = tbCompanyMapper.getCertAqrz(param);
                if (anrz != null) {
                    resultMap.put("securityCert", anrz.get("mateName").toString());
                    resultMap.put("securityScore", anrz.get("score").toString());
                    if (anrz.get("score") != null) {
                        secur = Double.valueOf(anrz.get("score").toString());
                    }
                }
                List<Map<String, Object>> qyhjListT = tbCompanyMapper.getCertQyhj(param);
                Double unScore = tbCompanyMapper.getUndesirableScore(param);
                if (unScore == null) {
                    unScore = 0d;
                }
                if (qyhjListT != null) {
                    //将过期的排出
                    List<Map<String, Object>> qyhjList = new ArrayList<>();
                    for (Map<String, Object> map : qyhjListT) {
                        // TODO: 18/4/24 时间有效期临时写死
                        if (map.get("code") != null && map.get("years") != null) {
                            String years = map.get("years").toString();
                            String code = map.get("code").toString();
                            if ("b1".equals(code) && (years.indexOf("2016") > -1 || years.indexOf("2017") > -1)) {
                                qyhjList.add(map);
                            } else if ("b2".equals(code) && years.indexOf("2017") > -1) {
                                qyhjList.add(map);
                            } else if ("b3".equals(code) && (years.indexOf("2017") > -1 || years.indexOf("2018") > -1)) {
                                qyhjList.add(map);
                            } else if ("c1".equals(code) && years.indexOf("2016") > -1) {
                                qyhjList.add(map);
                            } else if ("c2".equals(code) && years.indexOf("2016") > -1) {
                                qyhjList.add(map);
                            }
                        }
                    }
                    //--------------------

                    //将奖项去重
                    for (int i = 0; i < qyhjList.size() - 1; i++) {
                        for (int j = qyhjList.size() - 1; j > i; j--) {
                            if (qyhjList.get(j).get("projName").toString().equals(qyhjList.get(i).get("projName").toString())) {
                                if (Double.valueOf(qyhjList.get(j).get("score").toString()).compareTo(
                                        Double.valueOf(qyhjList.get(i).get("score").toString())) > 0) {
                                    qyhjList.remove(i);
                                } else if (Double.valueOf(qyhjList.get(j).get("score").toString()).compareTo(
                                        Double.valueOf(qyhjList.get(i).get("score").toString())) < 0) {
                                    qyhjList.remove(j);
                                } else {
                                    if (!"gjjhj".equals(qyhjList.get(j).get("type").toString())) {
                                        qyhjList.remove(j);
                                    }
                                }
                            }
                        }
                    }

                    resultMap.put("allNum", qyhjList.size());

                    //分组-----------
                    Map<String, Map<String, List<Map<String, Object>>>> map = new HashMap<>();
                    //分值
                    List<Double> gjjScore = new ArrayList<>();
                    List<Double> sjScore = new ArrayList<>();
                    List<Double> sjScore7 = new ArrayList<>();


                    for (Map<String, Object> qyhj : qyhjList) {

                        //--计算分值------------start--只计算 joinType = 承建单位
                        if (qyhj.get("score") != null && qyhj.get("type") != null
                                && qyhj.get("joinType") != null
                                && "承建单位".equals(qyhj.get("joinType").toString())) {
                            String scoreStr = qyhj.get("score").toString();
                            if ("gjjhj".equals(qyhj.get("type").toString()) && gjjScore.size() < 3) {
                                gjjScore.add(Double.valueOf(scoreStr));
                            }
                            if ("sjhj".equals(qyhj.get("type").toString())) {
                                if (sjScore.size() < 7) {
                                    sjScore.add(Double.valueOf(scoreStr));
                                } else {
                                    sjScore7.add(Double.valueOf(scoreStr));
                                }
                            }
                        }
                        //--计算分值--------------end

                        if (qyhj.get("type") != null && qyhj.get("code") != null) {
                            String type = qyhj.get("type").toString();
                            String code = qyhj.get("code").toString();
                            Map<String, List<Map<String, Object>>> subMap;
                            List<Map<String, Object>> list;
                            if (map.get(type) != null) {
                                subMap = map.get(type);
                                if (subMap.get(code) != null) {
                                    list = subMap.get(code);
                                } else {
                                    list = new ArrayList<>();
                                }
                            } else {
                                subMap = new HashMap<>();
                                list = new ArrayList<>();
                            }
                            list.add(qyhj);
                            subMap.put(code, list);
                            map.put(type, subMap);
                        }

                    }
                    //---------------------end

                    List<Map<String, Object>> resultList = new ArrayList<>();
                    Set<String> set = map.keySet();
                    for (String type : set) {
                        int pNum = 0;
                        String name = "其他奖项";
                        if ("gjjhj".equals(type)) {
                            name = "国家级奖项";
                        } else if ("sjhj".equals(type)) {
                            name = "省级奖项";
                        }
                        Map<String, Object> parentMap = new HashMap<>();
                        parentMap.put("name", name);
                        parentMap.put("code", type);
                        Map<String, List<Map<String, Object>>> subMap = map.get(type);
                        List<Map<String, Object>> subList = new ArrayList<>();
                        Set<String> subSet = subMap.keySet();
                        for (String code : subSet) {
                            Map<String, Object> subRe = new HashMap<>();
                            List<Map<String, Object>> list = subMap.get(code);
                            if (list != null && list.size() > 0) {
                                subRe.put("name", list.get(0).get("mateName"));
                                subRe.put("shortName", list.get(0).get("shortName"));
                                subRe.put("shortRemark", list.get(0).get("shortRemark"));
                                subRe.put("code", code);
                                subRe.put("num", list.size());
                                subRe.put("list", list);
                                pNum = pNum + list.size();
                                subList.add(subRe);
                            }
                        }
                        parentMap.put("num", pNum);
                        parentMap.put("list", subList);
                        resultList.add(parentMap);
                    }
                    for (Double d : gjjScore) {
                        score = score + d;
                    }
                    for (Double d : sjScore) {
                        score = score + d;
                    }
                    Double score7 = 0d;
                    for (Double d : sjScore7) {
                        score7 = score7 + d;
                    }
                    score7 = score7 / 5;
                    score = score + score7 + secur + unScore;
                    if (score == 0 && unScore == 0 && resultList.size() == 0) {
                        score = null;
                    }
                    resultMap.put("reputation", resultList);
                    resultMap.put("score", score);
                }
            }
        }
        return resultMap;
    }

    public Map<String, Object> getUndesirable(String comId) {
        Map<String, Object> resultMap = new HashMap<>();

        TbCompany company = tbCompanyMapper.getCompanyOrgCode(comId);
        if (company != null) {
            company = setCompanyDetail(company);
            List<String> srcUuids = tbCompanyMapper.getCertSrcUuidByName(company.getComName());
            if ((null == srcUuids || srcUuids.size() <= 0) && null != company.getOrgCode()) {
                srcUuids = tbCompanyMapper.getCertSrcUuid(company.getOrgCode());
            }
            if (srcUuids != null && srcUuids.size() > 0) {
                Map<String, Object> param = new HashMap<>();
                param.put("list", srcUuids);
                List<Map<String, Object>> list = tbCompanyMapper.getUndesirable(param);
                resultMap.put("allNum", list.size());
                Map<String, List<Map<String, Object>>> unMap = new HashMap<>();
                for (Map<String, Object> map : list) {
                    if (map.get("nature") != null && !"".equals(map.get("nature"))) {
                        String key = map.get("nature").toString();
                        List<Map<String, Object>> unList;
                        if (unMap.get(key) != null) {
                            unList = unMap.get(key);
                        } else {
                            unList = new ArrayList<>();
                        }
                        unList.add(map);
                        unMap.put(key, unList);
                    }
                }

                List<Map<String, Object>> resultList = new ArrayList<>();
                Set<String> set = unMap.keySet();
                for (String key : set) {
                    List<Map<String, Object>> mapList = unMap.get(key);
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", "性质" + key);
                    map.put("num", mapList.size());
                    map.put("list", mapList);
                    resultList.add(map);
                }
                resultMap.put("undesirable", resultList);
            }
        }
        return resultMap;
    }


    public Map<String, CertBasic> getCertBasicMap() {
        Map<String, CertBasic> certBasicMap;
        Long time = globalCache.getVaildTime().get("certBasic");
        long nowTime = System.currentTimeMillis();
        long value = 18000000;
        String cacheTime = PropertiesUtils.getProperty("cacheTime");
        if (cacheTime != null) {
            value = Long.parseLong(cacheTime);
        }
        if (time != null && nowTime - time < value) {
            certBasicMap = globalCache.getCertBasicMap();
            if (certBasicMap != null && certBasicMap.size() > 0) {
                logger.info("企业工商数据启用缓存========缓存数据共计[" + certBasicMap.size() + "]条");
                return certBasicMap;
            }
        }
        List<CertBasic> certBasicList = certBasicMapper.getCertBasicMap();
        certBasicMap = new HashMap<>();
        for (CertBasic basic : certBasicList) {
            certBasicMap.put(basic.getCompanyname() + "|" + basic.getRegisterno(), basic);
        }
        globalCache.setCertBasicMap(certBasicMap);
        globalCache.getVaildTime().put("certBasic", nowTime);
        return certBasicMap;
    }

    public Map<String, TbSafetyCertificate> getSafetyCertMap() {
        Map<String, TbSafetyCertificate> safetyCertificateMap;
        Long time = globalCache.getVaildTime().get("safetyCert");
        long nowTime = System.currentTimeMillis();
        long value = 18000000;
        String cacheTime = PropertiesUtils.getProperty("cacheTime");
        if (cacheTime != null) {
            value = Long.parseLong(cacheTime);
        }
        if (time != null && nowTime - time < value) {
            safetyCertificateMap = globalCache.getSafetyCertMap();
            if (safetyCertificateMap != null && safetyCertificateMap.size() > 0) {
                logger.info("企业安许证数据启用缓存========缓存数据共计[" + safetyCertificateMap.size() + "]条");
                return safetyCertificateMap;
            }
        }
        List<TbSafetyCertificate> tbSafetyCertificateList = tbSafetyCertificateMapper.getSafetyCertMap();
        safetyCertificateMap = new HashMap<>();
        for (TbSafetyCertificate safetyCertificate : tbSafetyCertificateList) {
            safetyCertificateMap.put(safetyCertificate.getComName(), safetyCertificate);
        }
        globalCache.setSafetyCertMap(safetyCertificateMap);
        globalCache.getVaildTime().put("safetyCert", nowTime);
        return safetyCertificateMap;
    }

    /**
     * 查询企业logo
     *
     * @param comId
     * @return
     */
    public String getLogo(String comId) {
        return tbCompanyMapper.getLogo(comId);
    }

    /**
     * 获得企业人员详细信息
     *
     * @param params
     * @return
     */
    public Map<String, Object> getPersonDetail(Map<String, Object> params) {
        return tbPersonMapper.queryPersonDetail(params);
    }


    public PageInfo getPersonCacheMap(Page page, Map<String, Object> param) {
        String key = "person|" + page.getCurrentPage() + "|" + page.getPageSize() + "|"
                + param.get("comId") + "|" + param.get("category") + "|" + param.get("keyWord") + "|" + param.get("tableCode");
        String cacheTime = PropertiesUtils.getProperty("personCacheTime");
        param.put("currentPage",page.getCurrentPage());
        int paramHash = ObjectUtils.buildMapParamHash(param);
        String listKey = RedisConstantInterface.PERSON_LIST + paramHash;
        PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
        if (null != pageInfo) {
            logger.info("注册人员数据启用缓存[" + key + "]========缓存数据共计[" + pageInfo.getList().size() + "]条");
            return pageInfo;
        }
        pageInfo = queryCompanyPerson(page, param);
        if (null != pageInfo && null != pageInfo.getList()) {
            myRedisTemplate.setObject(listKey, pageInfo, Integer.valueOf(cacheTime));
        }
        return pageInfo;
    }


    public List<Map<String, Object>> getProvince() {
        return tbPersonQualificationMapper.getProvinceList();
    }

    public String getProvinceCode(String name) {
        String code = null;
        Map<String, String> provinceMap = globalCache.getProvinceMap();
        if (provinceMap.get(name) != null) {
            code = provinceMap.get(name);
        } else {
            code = tbPersonQualificationMapper.getProvinceCode(name);
            if (code != null) {
                provinceMap.put(name, code);
                globalCache.setProvinceMap(provinceMap);
            }
        }
        return code;
    }

    public void updatePersonPX(String tableCode, String name) {
        Map<String, Object> param = new HashMap<>();
        if (tableCode != null && name != null) {
            param.put("tableCode", tableCode);
            param.put("name", name);
            tbPersonQualificationMapper.updatePersonPX(param);
        }
    }

    public TbCompany getCompanyDetailByName(Map<String, Object> param) {
        String comName = param.get("comName").toString();
        TbCompany tbCompany = tbCompanyMapper.queryCompanyDetail(comName);
        if (null == tbCompany) {
            return null;
        }
        param.put("comId", tbCompany.getComId());
        param.put("userId", VisitInfoHolder.getUid());
        Integer num = tbCompanyMapper.getColleCount(param);
        if (num > 0) {
            tbCompany.setCollected(true);
        }
        // TODO: 18/5/7 存续状态写死
        tbCompany.setSubsist("存续");
        if (tbCompany.getRegisAddress() != null && tbCompany.getRegisAddress().indexOf("湖南") > -1) {
            String url = tbCompanyMapper.getCompanyUrl(tbCompany.getComId());
            tbCompany.setUrl(url);
        }
        return setCompanyDetail(tbCompany);
    }

    /**
     * @param qualCode 原先code
     * @param zzLevel  级数(31/21/11)
     * @return
     */
    public List<String> getQualCode(String qualCode, String zzLevel) {
        List<String> rangeList = new ArrayList<>();
        String superfineLevel = null;
        String stairLevel = null;
        String secondLevel = null;
        switch (zzLevel) {
            case "11":
                superfineLevel = qualCode.replace("/11", "/0");
                stairLevel = superfineLevel.replace("/0", "/1");
                rangeList.add(superfineLevel);
                rangeList.add(stairLevel);
                break;
            case "21":
                superfineLevel = qualCode.replace("/21", "/0");
                stairLevel = superfineLevel.replace("/0", "/1");
                secondLevel = stairLevel.replace("/1", "/2");
                rangeList.add(superfineLevel);
                rangeList.add(stairLevel);
                rangeList.add(secondLevel);
                break;
            case "31":
                qualCode = qualCode.replace("/31", "");
                rangeList.add(qualCode);
                break;
            case "-11":
                superfineLevel = qualCode.replace("/-11", "/0");
                stairLevel = superfineLevel.replace("/0", "/-1");
                rangeList.add(superfineLevel);
                rangeList.add(stairLevel);
                break;
            case "-21":
                superfineLevel = qualCode.replace("/-21", "/0");
                stairLevel = superfineLevel.replace("/0", "/-1");
                secondLevel = stairLevel.replace("/-1", "/-2");
                rangeList.add(superfineLevel);
                rangeList.add(stairLevel);
                rangeList.add(secondLevel);
                break;
            case "-31":
                qualCode = qualCode.replace("/-31", "");
                rangeList.add(qualCode);
                break;
            default:
                rangeList.add(qualCode);
                break;
        }
        return rangeList;
    }

    public List<Map> matchName(Map argMap) {
        return tbCompanyMapper.matchName(argMap);
    }


    public List<Map<String, Object>> getProvinceCityCounty() {
        List<Map<String, Object>> areaList = new ArrayList<>();
        List<Map<String, String>> list = tbCompanyMapper.queryProvinceList();
        List<Map<String, Object>> countyList = tbCompanyMapper.queryCountyList();
        Map<String, Object> cityMap = null;
        for (Map<String, String> map : list) {
            Map<String, Object> area = new HashMap<>();
            List<Map<String, Object>> cityListT = tbCompanyMapper.queryCityPathList(map.get("id"));
            List<Map<String, Object>> cityList = new ArrayList<>();
            for (Map<String, Object> city : cityListT) {
                cityMap = new HashMap<>();
                cityMap.put("cityName", city.get("displayName").toString().replace(map.get("display_name"), ""));
                if ("湖南省".equals(map.get("display_name"))) {
                    List<String> cityCountyList = new ArrayList<>();
                    for (Map<String, Object> countyMap : countyList) {
                        if (city.get("path").toString().equals(countyMap.get("parentId").toString())) {
                            if (!city.get("displayName").toString().replace(map.get("display_name"), "").contains(countyMap.get("displayName").toString())
                                    && !"长沙县".equals(countyMap.get("displayName").toString())) {
                                cityCountyList.add(MapUtils.getString(countyMap, "displayName"));
                            }
                        }
                    }
                    cityMap.put("countyList", cityCountyList);
                }
                cityList.add(cityMap);
            }
            if ("北京市".equals(map.get("display_name"))) {
                cityList = new ArrayList<>();
                for (Map<String, Object> city : cityListT) {
                    cityMap = new HashMap<>();
                    cityMap.put("cityName", city.get("displayName").toString().replace("北京", ""));
                    cityList.add(cityMap);
                }
            }
            if ("天津市".equals(map.get("display_name"))) {
                cityList = new ArrayList<>();
                for (Map<String, Object> city : cityListT) {
                    cityMap = new HashMap<>();
                    cityMap.put("cityName", city.get("displayName").toString().replace("天津", ""));
                    cityList.add(cityMap);
                }
            }

            area.put("name", map.get("display_name"));
            area.put("list", cityList);
            areaList.add(area);
        }
        return areaList;
    }

    /**
     * 获取热门企业
     *
     * @param param
     * @return
     */
    public List<TbCompany> getHostCompanyList(Map<String, Object> param) {
        if (null != PropertiesUtils.getProperty("company.size")) {
            param.put("limit", Integer.parseInt(PropertiesUtils.getProperty("company.size")));
        }
        List<TbCompany> companyList = tbCompanyMapper.queryHostCompanyList(param);
        TbCompanyInfo companyInfo = null;
        if (null != companyList && companyList.size() > 0) {
            for (TbCompany company : companyList) {
                companyInfo = tbCompanyInfoMapper.queryDetailByComName(company.getComName(), CommonUtil.getCode(company.getRegisAddress()));
                if (null != companyInfo) {
                    if (null != companyInfo.getPhone()) {
//                        company.setPhone(companyInfo.getPhone().split(";")[0].trim());
                        company.setPhone(this.solPhone(companyInfo.getPhone(), "replace"));
                    }
                    if (null != companyInfo.getRegisCapital()) {
                        company.setRegisCapital(companyInfo.getRegisCapital());
                    }
                }
            }
        }
        return companyList;
    }

    private Map<String, Object> getRegisAddress(Map<String, Object> param) {
        String regisAddress = param.get("regisAddress").toString();
        String[] regis = regisAddress.split("\\|\\|");
        String province = regis[0];
        List<String> cityList = new ArrayList<>();
        if (regis.length > 1) {
            String[] citys = regis[1].split(",");
            if (citys.length <= 3) {
                for (String str : citys) {
                    if (str.contains("湘西")) {
                        cityList.add("湘西");
                    } else {
                        cityList.add(str.replace("市", ""));
                    }
                }
            }
        }
        param.put("province", province);
        param.put("tabCode", CommonUtil.getCode(province));
        param.put("cityList", cityList);
        return param;
    }

    private String getQualType(String key) {
        String qualType = null;
        switch (key) {
            case "勘察资质":
                qualType = "勘察";
                break;
            case "建筑业企业资质":
                qualType = "建筑业";
                break;
            case "招标代理资格":
                qualType = "招标代理";
                break;
            case "监理资质":
                qualType = "监理";
                break;
            case "设计与施工一体化资质":
                qualType = "设计与施工";
                break;
            case "设计资质":
                qualType = "设计";
                break;
            case "造价资询资质":
                qualType = "造价资询";
                break;
            default:
                qualType = key;
                break;
        }
        return qualType;
    }

    /**
     * TODO: 设置公司值
     *
     * @param tbCompany
     */
    public TbCompany setCompanyDetail(TbCompany tbCompany) {
        String tabCode = CommonUtil.getCode(tbCompany.getRegisAddress());
        TbCompanyInfo companyInfo = tbCompanyInfoMapper.queryDetailByComName(tbCompany.getComName(), tabCode);
        if (null != companyInfo && null != companyInfo.getPhone()) {
            tbCompany.setPhone(solPhone(companyInfo.getPhone().trim(), null));
        }
        String scope = lawService.queryCompangScope(tbCompany.getComName());
        if (MyStringUtils.isNull(scope) && null != companyInfo && null != companyInfo.getScope()) {
            tbCompany.setComRange(companyInfo.getScope());
        } else {
            tbCompany.setComRange(scope);
        }
        if (null != companyInfo && null != companyInfo.getComUrl()) {
            tbCompany.setComUrl(companyInfo.getComUrl().split(";")[0].trim());
        }
        if (null != companyInfo && null != companyInfo.getEmail()) {
            tbCompany.setEmail(companyInfo.getEmail().split(";")[0].trim());
        }
        if (null != companyInfo && null != companyInfo.getOrgCode()) {
            tbCompany.setOrgCode(companyInfo.getOrgCode());
        }
        return tbCompany;
    }

    public String solPhone(String phone, String type) {
        List<String> phoneList = new ArrayList<>();
        List<String> tellList = new ArrayList<>();
        Object[] phones = doWeightPhone(phone.split(";"));
        if (null != phones && phones.length > 0) {
            for (Object str : phones) {
                if (str.toString().startsWith("1")) {
                    phoneList.add(str.toString());
                } else {
                    str = insertString("-", str.toString(), 4);
                    tellList.add(str.toString());
                }
            }
            String resultPhone = "";
            if (null != phoneList && phoneList.size() > 0) {
                for (int i = 0; i < phoneList.size(); i++) {
                    if (0 == i) {
                        resultPhone = phoneList.get(0);
                    } else {
                        resultPhone = resultPhone + ";" + phoneList.get(i);
                    }
                }
            }
            if (null != tellList && tellList.size() > 0) {
                for (int i = 0; i < tellList.size(); i++) {
                    if (null != resultPhone && !"".equals(resultPhone)) {
                        resultPhone = resultPhone + ";" + tellList.get(i);
                    } else {
                        resultPhone = tellList.get(0);
                    }
                }
            }
            if ("replace".equals(type)) {
                resultPhone = replaceStr("*", resultPhone.split(";")[0]);
            }
            return resultPhone;
        }
        return phone;
    }

    /**
     * 电话号码去重
     *
     * @param strings
     * @return
     */
    public Object[] doWeightPhone(String[] strings) {
        String regEx = "[`~!@#$%^&*()\\-+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        List<String> list = new ArrayList<>();
        for (String str : strings) {
            Matcher m = p.matcher(str);
            list.add(m.replaceAll("").replace(" ", "").trim());
        }
        //去重
        Set<String> set = new HashSet<>();
        set.addAll(list);
        return set.toArray();
    }

    //插入字符
    public static String insertString(String srcStr, String descStr, int off) {
        if (off > descStr.length()) {
            return "";
        }
        char[] src = srcStr.toCharArray();
        char[] desc = descStr.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i <= desc.length; i++) {
            if (i == off) {
                for (int j = 0; j < src.length; j++) {
                    ret.append(src[j]);
                }
            }
            if (i < desc.length) {
                ret.append(desc[i]);
            }
        }
        return ret.toString();
    }

    /**
     * 电话号码替换*
     *
     * @param srcStr
     * @param descStr
     * @return
     */
    public static String replaceStr(String srcStr, String descStr) {
        char[] descs = descStr.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < descStr.length(); i++) {
            if (descStr.startsWith("0")) {
                if (i >= 5 && i <= 8) {
                    stringBuilder.append("*");
                } else {
                    stringBuilder.append(descs[i]);
                }
            } else {
                if (i >= 3 && i < 7) {
                    stringBuilder.append(srcStr);
                } else {
                    stringBuilder.append(descs[i]);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 企业分享-业绩/人员/分支机构个数
     *
     * @param param
     * @return
     */
    public Map<String, Object> getShareTotal(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("branchCompanyTotal", 0);
        resultMap.put("projectTotal", 0);
        //分支机构
        List<TbCompanyInfo> companyInfoList = elasticseachService.queryBranchCompany(param);
        if (null != companyInfoList && companyInfoList.size() > 0) {
            resultMap.put("branchCompanyTotal", companyInfoList.size());
        }
        //业绩
        List<Map<String, Object>> projectList = projectService.getProjectCompanyList(MapUtils.getString(param, "comId"));
        if (null != projectList && projectList.size() > 0) {
            resultMap.put("projectTotal", projectList.size());
        }
        return resultMap;
    }

    /**
     * 设置是否在建
     *
     * @param list
     */
    private List<TbPersonQualification> setIsUnder(List<TbPersonQualification> list) {
        if (null != list && list.size() > 0) {
            for (TbPersonQualification person : list) {
                person.setIsUnder(false);
                int count = tbUnderConstructMapper.queryUnderConstructByInnerid(person.getInnerid());
                if (count > 0) {
                    person.setIsUnder(true);
                }
            }
        }
        return list;
    }
}
