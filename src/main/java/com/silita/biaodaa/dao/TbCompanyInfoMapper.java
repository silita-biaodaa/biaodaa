package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * tb_company_info Mapper
 */
public interface TbCompanyInfoMapper extends MyMapper<TbCompanyInfo> {

    /**
     * 批量添加
     *
     * @param list
     * @return
     */
    int batchInsertCompanyInfo(List<TbCompanyInfo> list);

    /**
     * 添加
     *
     * @param list
     * @return
     */
    int insertCompanyInfo(TbCompanyInfo list);

    /**
     * 获取
     *
     * @param name
     * @return
     */
    String queryPhoneByComName(String name);

    /**
     * 获取详情
     *
     * @param name
     * @return
     */
    TbCompanyInfo queryDetailByComName(String name);

    /**
     * 获取公司总数
     *
     * @return
     */
    Integer queryComCount();

    /**
     * 分页查询信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    List<TbCompany> queryCompanyList(@Param("page") Integer page, @Param("pageSize") Integer pageSize);

    /**
     * 修改
     *
     * @param company
     * @return
     */
    int updateCompany(TbCompany company);

    /**
     * 获取企业个数
     *
     * @param name
     * @return
     */
    Integer queryCount(@Param("name") String name,@Param("tabCode") String tabCode);
}