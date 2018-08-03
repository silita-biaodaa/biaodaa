package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/9.
 */
public interface NoticeMapper {

    /**
     * 获取公告id
     * @param companyName
     * @return
     */
    Long queryNoticesByCompanyName(String companyName);

    /**
     * 获取公告
     * @param argMap
     * @return
     */
    List queryNoticeList(Map argMap);

    /**
     * 查询公告
     * @param argMap
     * @return
     */
    List searchNoticeList(Map argMap);

    /**
     * 获取公告资质
     * @param id
     * @return
     */
    String queryNoticeZZById(Map id);

    /**
     * 获取符合资质企业数
     * @param map
     * @return
     */
    Integer queryCompanyCountByZZ(Map map);

    /**
     * 获取符合资质企业
     * @param map
     * @return
     */
    List<TbCompany> queryComInfoByZZ(Map map);

    /**
     * 获取企业安许证号
     * @param comList
     * @return
     */
    List<TbCompany> selectCompanyCert(Map comList);

    /**
     * 获取企业旧数据
     * @param comList
     * @return
     */
    List<TbCompany> selectCompanyCertBasic(Map comList);

    /**
     * 获取公告详情
     * @param map
     * @return
     */
    List<Map> queryNoticeDetail(Map map);

    /**
     * 获取公告关联id
     * @param id
     * @return
     */
    Long queryRelCount(Long id);

    /**
     * 获取公告文件
     * @param id
     * @return
     */
    List<Map> queryNoticeFile(Long id);

    /**
     * 获取关联
     * @param map
     * @return
     */
    List<Map> queryRelations(Map map);

    /**
     * 获取企业关注
     * @param map
     * @return
     */
    List<String> queryCompanyCollStatus(Map map);

    /**
     * 获取公告关注
     * @param map
     * @return
     */
    List<Long> queryNoticeCollStatus(Map map);

    /**
     * 获取省份
     * @param param
     * @return
     */
    String queryProvince(Map<String,Object> param);

    /**
     * 获取企业资质
     * @param argMap
     * @return
     */
    List<Map> queryComAptitudeByName(Map argMap);

    /**
     * 查询湖南公告，湖南本地企业匹配数
     * @param argMap
     * @return
     */
    List<Map> queryMatchComCount(Map argMap);

    /**
     * 添加
     * @param listMap
     */
    void inertMatchComCount(Map listMap);

    /**
     * 查询湖南公告，“入湘企业”匹配数
     * @param argMap
     * @return
     */
    List<Map> queryMatchIntoComCount(Map argMap);
}
