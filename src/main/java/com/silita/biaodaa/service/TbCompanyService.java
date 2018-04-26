package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.cache.GlobalCache;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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

    private GlobalCache globalCache = GlobalCache.getGlobalCache();


    public PageInfo queryCompanyList(Page page,String keyWord){
        List<TbCompany> list = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbCompanyMapper.queryCompanyList(keyWord);
        PageInfo pageInfo = new PageInfo(list);

        return pageInfo;
    }

    public TbCompany getCompany(Integer comId){
        TbCompany tbCompany = tbCompanyMapper.getCompany(comId);
        Map<String,Object> param = new HashMap<>();
        param.put("comId",comId);
        param.put("userId", VisitInfoHolder.getUid());
        Integer num = tbCompanyMapper.getColleCount(param);
        if(num>0){
            tbCompany.setCollected(true);
        }
        return tbCompany;
    }

    public Map<String,List<TbCompanyQualification>> queryCompanyQualification(Integer comId){
        Map<String,List<TbCompanyQualification>> qualMap = new HashMap<>();
        if(comId!=null){
            // TODO: 18/4/10 需要关联标准资质表
            List<TbCompanyQualification> list = tbCompanyQualificationMapper.queryCompanyQualification(comId);
            for(TbCompanyQualification qual : list){
                if(qual.getQualType()!=null&&qual.getRange()!=null){
                    String key = qual.getQualType();
                    List<TbCompanyQualification> qualList;
                    if(qualMap.get(key)!=null){
                        qualList = qualMap.get(key);
                    }else{
                        qualList = new ArrayList<>();

                    }
                    String [] range = qual.getRange().split("；");
                    if(range.length>=2){
                        for(int i = 0;i<range.length;i++){
                            TbCompanyQualification qualT = qual.clon();
                            qualT.setQualName(range[i]);
                            qualList.add(qualT);
                        }
                    }else{
                        qual.setQualName(range[0]);
                        qualList.add(qual);
                    }
                    qualMap.put(key,qualList);
                }
            }

        }
        return qualMap;
    }

    public List<Map<String,Object>> queryQualList(Integer comId){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,List<TbCompanyQualification>> qualMap = queryCompanyQualification(comId);
        Set<String> set = qualMap.keySet();
        for(String key : set){
            Map<String,Object> map = new HashMap<>();
            map.put("qualType",key);
            map.put("list",qualMap.get(key));
            list.add(map);
        }
        return list;
    }

    public List<Map<String,Object>> getCompanyPersonCate(Integer comId){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        if(comId!=null){
            if(comId==0){
                return tbPersonQualificationMapper.getCompanyPersonCate(param);
            }
            TbCompany company = getCompany(comId);
            if(company!=null){
                param.put("comId",comId);
                param.put("comName",company.getComName());
                list = tbPersonQualificationMapper.getCompanyPersonCate(param);
            }
        }
        return list;
    }

    public PageInfo queryCompanyPerson(Page page,Map<String,Object> param){
        List<TbPersonQualification> list = new ArrayList<>();
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbPersonQualificationMapper.queryCompanyPerson(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public List<CompanyQual> getCompanyQual(){
        List<AptitudeDictionary> list = aptitudeDictionaryMapper.queryAptitude();
        Map<String,CompanyQual> map = new HashMap<>();
        List<CompanyQual> companyQualList = new ArrayList<>();
        for(AptitudeDictionary ap : list){
            CompanyQual qual = new CompanyQual();
            qual.setName(ap.getMajorname());
            qual.setCode(ap.getMajoruuid());
            qual.setList(getZzTpyeList(ap.getMajoruuid(),ap.getZztype()));
            if(map.get(ap.getAptitudename())!=null){
                CompanyQual pany = map.get(ap.getAptitudename());
                List<CompanyQual> panyList = pany.getList();
                if(panyList==null){
                    panyList = new ArrayList<>();
                }
                panyList.add(qual);
            }else{
                CompanyQual pany = new CompanyQual();
                List<CompanyQual> panyList = new ArrayList<>();
                panyList.add(qual);
                pany.setName(ap.getAptitudename());
                pany.setCode(ap.getAptitudecode());
                pany.setList(panyList);
                map.put(ap.getAptitudename(),pany);
            }
        }
        for(CompanyQual companyQual : map.values()){
            companyQualList.add(companyQual);
        }
        return companyQualList;
    }

    public List<CompanyQual> getZzTpyeList(String majoruuid,String zzType){
        List<CompanyQual> list = new ArrayList<>();
        if("1".equals(zzType)){
            //等级：0=特级，1=一级，2=二级，11=一级及以上，21=二级及以上，31=三级及以上，-1=甲级，-2=乙级，-3=丙级
            CompanyQual qual0 = new CompanyQual();
            qual0.setName("特级");
            qual0.setCode(majoruuid+"/"+"0");

            CompanyQual qual1 = new CompanyQual();
            qual1.setName("一级");
            qual1.setCode(majoruuid+"/"+"1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("二级");
            qual2.setCode(majoruuid+"/"+"2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("三级");
            qual3.setCode(majoruuid+"/"+"3");

            CompanyQual qual11 = new CompanyQual();
            qual11.setName("一级及以上");
            qual11.setCode(majoruuid+"/"+"11");

            CompanyQual qual21 = new CompanyQual();
            qual21.setName("二级及以上");
            qual21.setCode(majoruuid+"/"+"21");

            CompanyQual qual31 = new CompanyQual();
            qual31.setName("三级及以上");
            qual31.setCode(majoruuid+"/"+"31");

            list.add(qual0);
            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
            list.add(qual11);
            list.add(qual21);
            list.add(qual31);
        }else if("2".equals(zzType)){
            CompanyQual qual1 = new CompanyQual();
            qual1.setName("甲级");
            qual1.setCode(majoruuid+"/"+"-1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("乙级");
            qual2.setCode(majoruuid+"/"+"-2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("丙级");
            qual3.setCode(majoruuid+"/"+"-3");

            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
        }
        return list;
    }

    
    public List<Map<String,String>> getIndustry(){
        return aptitudeDictionaryMapper.getIndustry();
    }

    public PageInfo filterCompany(Page page,Map<String,Object> param){
        List<TbCompany> list = new ArrayList<>();
        Map<String,CertBasic> certBasicMap = getCertBasicMap();
        Map<String,TbSafetyCertificate> safetyCertificateMap = getSafetyCertMap();
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbCompanyMapper.filterCompany(param);
        for(TbCompany company : list){
            if(company.getComName()!=null&&company.getBusinessNum()!=null){
                CertBasic certBasic = certBasicMap.get(company.getComName()+"|"+company.getBusinessNum());
                if(certBasic!=null){
                    //company.setSubsist(certBasic.getRegisterstatus());
                    // TODO: 18/4/26  存续状态暂时写死
                    company.setSubsist("存续");
                    company.setComRange(certBasic.getRunscope());
                }
                TbSafetyCertificate tbSafetyCertificate = safetyCertificateMap.get(company.getComName());
                if(tbSafetyCertificate!=null){
                    company.setCertNo(tbSafetyCertificate.getCertNo());
                    company.setCertDate(tbSafetyCertificate.getCertDate());
                    company.setValidDate(tbSafetyCertificate.getValidDate());
                }
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public String getAreaCode(String name){
        return tbCompanyMapper.getAreaCode(name);
    }


    public List<Map<String,Object>> getArea(){
        List<Map<String,Object>> areaList = new ArrayList<>();
        List<Map<String,String>> list = tbCompanyMapper.queryProvinceList();
        for(Map<String,String> map : list){
            Map<String,Object> area = new HashMap<>();
            List<String> cityListT = tbCompanyMapper.queryCityList(map.get("id"));
            List<String> cityList = new ArrayList<>();
            for(String city : cityListT){
                cityList.add(city.replace(map.get("display_name"),""));
            }
            if("北京市".equals(map.get("display_name"))){
                cityList = new ArrayList<>();
                for(String city : cityListT){
                    cityList.add(city.replace("北京",""));
                }
            }
            if("天津市".equals(map.get("display_name"))){
                cityList = new ArrayList<>();
                for(String city : cityListT){
                    cityList.add(city.replace("天津",""));
                }
            }

            area.put("name",map.get("display_name"));
            area.put("list",cityList);
            areaList.add(area);
        }
        return areaList;
    }

    public Map<String,Object> getCompanyReputation(Integer comId){
        Map<String,Object> resultMap = new HashMap<>();
        Double score = 0d;
        Double secur = 0d;

        TbCompany company = tbCompanyMapper.getCompanyOrgCode(comId);
        if(company!=null&&company.getOrgCode()!=null){
            List<String> srcUuids = tbCompanyMapper.getCertSrcUuid(company.getOrgCode());
            if(srcUuids!=null&&srcUuids.size()>0){
                Map<String,Object> param = new HashMap<>();
                param.put("list",srcUuids);
                Map<String,Object> anrz = tbCompanyMapper.getCertAqrz(param);
                if(anrz!=null){
                    resultMap.put("securityCert",anrz.get("mateName").toString());
                    resultMap.put("securityScore",anrz.get("score").toString());
                    if(anrz.get("score")!=null){
                        secur = Double.valueOf(anrz.get("score").toString());
                    }
                }
                List<Map<String,Object>> qyhjListT = tbCompanyMapper.getCertQyhj(param);
                Double unScore = tbCompanyMapper.getUndesirableScore(param);
                if(unScore==null){
                    unScore = 0d;
                }
                if(qyhjListT!=null){
                    //将过期的排出
                    List<Map<String,Object>> qyhjList = new ArrayList<>();
                    for(Map<String,Object> map : qyhjListT){
                        // TODO: 18/4/24 时间有效期临时写死
                        if(map.get("code")!=null&&map.get("years")!=null){
                            String years = map.get("years").toString();
                            String code = map.get("code").toString();

                            if("b1".equals(code)&&(years.indexOf("2016")>-1||years.indexOf("2017")>-1)){
                                qyhjList.add(map);
                            }else if(years.indexOf("2017")>-1){
                                qyhjList.add(map);
                            }
                        }
                    }
                    //--------------------


                    resultMap.put("allNum",qyhjList.size());
                    //分组-----------
                    Map<String,Map<String,List<Map<String,Object>>>> map = new HashMap<>();
                    //分值
                    List<Double> gjjScore = new ArrayList<>();
                    List<Double> sjScore = new ArrayList<>();
                    List<Double> sjScore7 = new ArrayList<>();


                    for(Map<String,Object> qyhj : qyhjList){

                        //--计算分值------------start
                        if(qyhj.get("score")!=null&&qyhj.get("type")!=null){
                            String scoreStr = qyhj.get("score").toString();
                            if("gjjhj".equals(qyhj.get("type").toString())&&gjjScore.size()<3){
                                gjjScore.add(Double.valueOf(scoreStr));
                            }
                            if("sjhj".equals(qyhj.get("type").toString())){
                                if(gjjScore.size()<7){
                                    sjScore.add(Double.valueOf(scoreStr));
                                }else{
                                    sjScore7.add(Double.valueOf(scoreStr));
                                }
                            }
                        }
                        //--计算分值--------------end

                        if(qyhj.get("type")!=null&&qyhj.get("code")!=null){
                            String type = qyhj.get("type").toString();
                            String code = qyhj.get("code").toString();
                            Map<String,List<Map<String,Object>>> subMap;
                            List<Map<String,Object>> list;
                            if(map.get(type)!=null){
                                subMap = map.get(type);
                                if(subMap.get(code)!=null){
                                    list = subMap.get(code);
                                }else{
                                    list = new ArrayList<>();
                                }
                            }else{
                                subMap = new HashMap<>();
                                list = new ArrayList<>();
                            }
                            list.add(qyhj);
                            subMap.put(code,list);
                            map.put(type,subMap);
                        }

                    }
                    //---------------------end

                    List<Map<String,Object>> resultList = new ArrayList<>();
                    Set<String> set = map.keySet();
                    for(String type : set){
                        int pNum = 0;
                        String name = "其他奖项";
                        if("gjjhj".equals(type)){
                            name = "国家级奖项";
                        }else if("sjhj".equals(type)){
                            name = "省级奖项";
                        }
                        Map<String,Object> parentMap = new HashMap<>();
                        parentMap.put("name",name);
                        parentMap.put("code",type);
                        Map<String,List<Map<String,Object>>> subMap = map.get(type);
                        List<Map<String,Object>> subList = new ArrayList<>();
                        Set<String> subSet = subMap.keySet();
                        for(String code : subSet){
                            Map<String,Object> subRe = new HashMap<>();
                            List<Map<String,Object>> list = subMap.get(code);
                            if(list!=null&&list.size()>0){
                                subRe.put("name",list.get(0).get("mateName"));
                                subRe.put("shortName",list.get(0).get("shortName"));
                                subRe.put("shortRemark",list.get(0).get("shortRemark"));
                                subRe.put("code",code);
                                subRe.put("num",list.size());
                                subRe.put("list",list);
                                pNum = pNum + list.size();
                                subList.add(subRe);
                            }
                        }
                        parentMap.put("num",pNum);
                        parentMap.put("list",subList);
                        resultList.add(parentMap);
                    }
                    for(Double d :gjjScore){
                        score = score + d;
                    }
                    for(Double d :sjScore){
                        score = score + d;
                    }
                    for(Double d :sjScore7){
                        score = score + d/5;
                    }
                    score = score + secur + unScore;
                    resultMap.put("reputation",resultList);
                    resultMap.put("score",score);
                }
            }
        }
        return resultMap;
    }

    public Map<String,Object> getUndesirable(Integer comId) {
        Map<String, Object> resultMap = new HashMap<>();

        TbCompany company = tbCompanyMapper.getCompanyOrgCode(comId);
        if (company != null && company.getOrgCode() != null) {
            List<String> srcUuids = tbCompanyMapper.getCertSrcUuid(company.getOrgCode());
            if(srcUuids!=null&&srcUuids.size()>0) {
                Map<String, Object> param = new HashMap<>();
                param.put("list", srcUuids);
                List<Map<String,Object>> list = tbCompanyMapper.getUndesirable(param);
                resultMap.put("allNum",list.size());
                Map<String,List<Map<String,Object>>> unMap = new HashMap<>();
                for(Map<String,Object> map : list){
                    if(map.get("nature")!=null&&!"".equals(map.get("nature"))){
                        String key = map.get("nature").toString();
                        List<Map<String,Object>> unList;
                        if(unMap.get(key)!=null){
                            unList = unMap.get(key);
                        }else{
                            unList = new ArrayList<>();
                        }
                        unList.add(map);
                        unMap.put(key,unList);
                    }
                }

                List<Map<String,Object>> resultList = new ArrayList<>();
                Set<String> set = unMap.keySet();
                for(String key : set){
                    List<Map<String,Object>> mapList = unMap.get(key);
                    Map<String,Object> map = new HashMap<>();
                    map.put("name","性质"+key);
                    map.put("num",mapList.size());
                    map.put("list",mapList);
                    resultList.add(map);
                }
                resultMap.put("undesirable",resultList);
            }
        }
        return resultMap;
    }


    public Map<String,CertBasic> getCertBasicMap(){
        Map<String,CertBasic> certBasicMap;
        Long time = globalCache.getVaildTime().get("certBasic");
        long nowTime = System.currentTimeMillis();
        long value = 18000000;
        String cacheTime = PropertiesUtils.getProperty("cacheTime");
        if( cacheTime!=null){
            value = Long.parseLong(cacheTime);
        }
        if(time!=null&&nowTime-time<value){
            certBasicMap = globalCache.getCertBasicMap();
            if(certBasicMap!=null&&certBasicMap.size()>0){
                logger.info("企业工商数据启用缓存========缓存数据共计["+certBasicMap.size()+"]条");
                return certBasicMap;
            }
        }
        List<CertBasic> certBasicList = certBasicMapper.getCertBasicMap();
        certBasicMap = new HashMap<>();
        for(CertBasic basic : certBasicList){
            certBasicMap.put(basic.getCompanyname()+"|"+basic.getRegisterno(),basic);
        }
        globalCache.setCertBasicMap(certBasicMap);
        globalCache.getVaildTime().put("certBasic",nowTime);
        return certBasicMap;
    }

    public Map<String,TbSafetyCertificate> getSafetyCertMap(){
        Map<String,TbSafetyCertificate> safetyCertificateMap;
        Long time = globalCache.getVaildTime().get("safetyCert");
        long nowTime = System.currentTimeMillis();
        long value = 18000000;
        String cacheTime = PropertiesUtils.getProperty("cacheTime");
        if( cacheTime!=null){
            value = Long.parseLong(cacheTime);
        }
        if(time!=null&&nowTime-time<value){
            safetyCertificateMap = globalCache.getSafetyCertMap();
            if(safetyCertificateMap!=null&&safetyCertificateMap.size()>0){
                logger.info("企业安许证数据启用缓存========缓存数据共计["+safetyCertificateMap.size()+"]条");
                return safetyCertificateMap;
            }
        }
        List<TbSafetyCertificate> tbSafetyCertificateList = tbSafetyCertificateMapper.getSafetyCertMap();
        safetyCertificateMap = new HashMap<>();
        for(TbSafetyCertificate safetyCertificate : tbSafetyCertificateList){
            safetyCertificateMap.put(safetyCertificate.getComName(),safetyCertificate);
        }
        globalCache.setSafetyCertMap(safetyCertificateMap);
        globalCache.getVaildTime().put("safetyCert",nowTime);
        return safetyCertificateMap;
    }

    /**
     * 查询企业logo
     * @param comId
     * @return
     */
    public String getLogo(Integer comId) {
        return tbCompanyMapper.getLogo(comId);
    }

    /**
     * 获得企业人员详细信息
     * @param params
     * @return
     */
    public Map<String, Object> getPersonDetail(Map<String, Object> params) {
        return tbPersonMapper.queryPersonDetail(params);
    }
}
