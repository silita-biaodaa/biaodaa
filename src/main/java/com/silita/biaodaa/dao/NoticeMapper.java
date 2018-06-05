package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/9.
 */
public interface NoticeMapper {
    Long queryNoticesByCompanyName(String companyName);

    List queryNoticeList(Map argMap);

    List searchNoticeList(Map argMap);

    String queryNoticeZZById(Map id);

    Integer queryCompanyCountByZZ(Map map);

    List<TbCompany> queryComInfoByZZ(Map map);

    List<TbCompany> selectCompanyCert(Map comList);

    List<TbCompany> selectCompanyCertBasic(Map comList);

    List<Map> queryNoticeDetail(Map map);

    Long queryRelCount(Long id);

    List<Map> queryNoticeFile(Long id);

    List<Map> queryRelations(Map map);

    List<Long> queryCompanyCollStatus(Map map);

    List<Long> queryNoticeCollStatus(Map map);

    String queryProvince(Map<String,Object> param);

    List<Map> queryComAptitudeByName(Map argMap);

    /**
     * 查询湖南公告，湖南本地企业匹配数
     * @param argMap
     * @return
     */
    List<Map> queryMatchComCount(Map argMap);

    void inertMatchComCount(Map listMap);

    /**
     * 查询湖南公告，“入湘企业”匹配数
     * @param argMap
     * @return
     */
    List<Map> queryMatchIntoComCount(Map argMap);
}
