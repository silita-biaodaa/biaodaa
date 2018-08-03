package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.utils.MyMapper;

import java.util.Map;

/**
 * invitation_bdd Mapper
 */
public interface InvitationBddMapper extends MyMapper<InvitationBdd> {

    /**
     * 添加验证码信息
     *
     * @param invitationBdd
     */
    void insertInvitationBdd(InvitationBdd invitationBdd);

    /**
     * 根据手机号码、验证码 取得验证信息
     *
     * @param params
     * @return
     */
    InvitationBdd getInvitationBddByPhoneAndCode(Map<String, Object> params);

    /**
     * 根据手机号码、发送时间 取得该手机单天发生验证码的次数
     *
     * @param invitationBdd
     * @return
     */
    Integer getTotalByPhoneAndCreateDate(InvitationBdd invitationBdd);

    /**
     * 更新验证码状态
     *
     * @param params
     */
    void updateInvitationBddByCodeAndPhone(Map<String, Object> params);
}