package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_company Mapper
 */
public interface TbCompanyMapper extends MyMapper<TbCompany> {

    /**
     * 获取公司列表根据关键字
     *
     * @param keyWord
     * @return
     */
    List<TbCompany> queryCompanyList(String keyWord);

    /**
     * 获取企业详情
     *
     * @param comId
     * @return
     */
    TbCompany getCompany(String comId);

    /**
     * 根据名称获取公司详情
     *
     * @param comName
     * @return
     */
    TbCompany queryCompanyDetail(String comName);

    /**
     * 查询企业
     *
     * @param param
     * @return
     */
    List<TbCompany> filterCompany(Map<String, Object> param);

    /**
     * 查询企业包含资质
     *
     * @param param
     * @return
     */
    List<TbCompany> filterSecureCompany(Map<String, Object> param);

    /**
     * 获取地区code
     *
     * @param name
     * @return
     */
    String getAreaCode(String name);

    /**
     * 获取省份
     *
     * @return
     */
    List<Map<String, String>> queryProvinceList();

    /**
     * 获取城市
     *
     * @param parentId
     * @return
     */
    List<String> queryCityList(String parentId);

    /**
     * 获取组织机构代码
     *
     * @param comId
     * @return
     */
    TbCompany getCompanyOrgCode(String comId);

    /**
     * 获取企业uuid
     *
     * @param companyCode
     * @return
     */
    List<String> getCertSrcUuid(String companyCode);

    /**
     * 获取企业安全认证
     *
     * @param param
     * @return
     */
    Map<String, Object> getCertAqrz(Map<String, Object> param);

    /**
     * 获取企业获奖
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> getCertQyhj(Map<String, Object> param);

    /**
     * 获取不良行为
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> getUndesirable(Map<String, Object> param);

    /**
     * 计算不良行为分值
     *
     * @param param
     * @return
     */
    Double getUndesirableScore(Map<String, Object> param);

    /**
     * 获取企业logo
     *
     * @param comId
     * @return
     */
    String getLogo(String comId);

    /**
     * 获取关注次数
     *
     * @param param
     * @return
     */
    Integer getColleCount(Map<String, Object> param);

    /**
     * 获取企业url
     *
     * @param comId
     * @return
     */
    String getCompanyUrl(String comId);

    /**
     * 获取企业根据名称
     *
     * @param argMap
     * @return
     */
    List<Map> matchName(Map argMap);

    /**
     * 根据市名获取区县
     *
     * @return
     */
    List<Map<String, Object>> queryCountyList();

    /**
     * 获取城市path
     *
     * @param parentId
     * @return
     */
    List<Map<String, Object>> queryCityPathList(String parentId);

    /**
     * 获取热门公司
     *
     * @param param
     * @return
     */
    List<TbCompany> queryHostCompanyList(Map<String, Object> param);

    /**
     * 获取公司信息
     *
     * @return
     */
    List<CompanyEs> queryCompanyEsList(Map<String, Object> param);

    /**
     * 获取企业数
     *
     * @return
     */
    Integer queryCompanyCount();

    /**
     * 获取企业数
     *
     * @return
     */
    Integer queryCompanyAddressCount(Map<String, Object> param);

    /**
     * 根据名称获取uuid
     *
     * @param companyName
     * @return
     */
    List<String> getCertSrcUuidByName(String companyName);

    /**
     * 查询长沙
     * @return
     */
    List<Map<String,Object>> queryCompanyChangsha();
}