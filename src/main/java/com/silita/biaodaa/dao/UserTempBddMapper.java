package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.model.SysUserRole;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * user_temp_bdd Mapper
 */
public interface UserTempBddMapper extends MyMapper<UserTempBdd> {

    /**
     * 获取用户信息
     *
     * @param userPhone
     * @return
     */
    UserTempBdd getUserByUserPhone(String userPhone);

    /**
     * 添加
     *
     * @param userTempBdd
     */
    void InsertUserTemp(UserTempBdd userTempBdd);

    /**
     * 获取用户信息
     *
     * @return
     */
    UserTempBdd getUserByUserNameOrPhoneAndPassWd(UserTempBdd userTempBdd);

    /**
     * 修改WX
     *
     * @param userTempBdd
     */
    void updateUserTempByWxBind(UserTempBdd userTempBdd);

    /**
     * 修改QQ
     *
     * @param userTempBdd
     */
    void updateUserTempByQQBind(UserTempBdd userTempBdd);

    /**
     * 获取WXid
     *
     * @param wxUnionid
     * @return
     */
    UserTempBdd getUserTempByWXUnionId(String wxUnionid);

    /**
     * 获取WXopenId
     *
     * @param wxopenid
     * @return
     */
    UserTempBdd getUserTempByWXOpenId(String wxopenid);

    /**
     * 修改
     *
     * @param userTempBdd
     */
    void updateWXUnionIdByWXOpenId(UserTempBdd userTempBdd);

    /**
     * 获取QQopenid
     *
     * @param qqopenid
     * @return
     */
    UserTempBdd getUserTempByQQOpenId(String qqopenid);

    /**
     * 获取用户信息
     *
     * @param userid
     * @return
     */
    UserTempBdd getUserTempByUserId(String userid);

    /**
     * 修改
     *
     * @param userTempBdd
     */
    void updateUserTemp(UserTempBdd userTempBdd);


    /**
     * 修改密码
     *
     * @param userTempBdd
     */
    void updatePassWdByUserIdAndPhone(UserTempBdd userTempBdd);

    /**
     * 获取总数
     *
     * @param userphone
     * @return
     */
    Integer getTotalByUserPhone(String userphone);

    /**
     * 查询用户与角色信息
     * @param param
     * @return
     */
    List<SysUser> queryUserInfo(SysUser param);

    /**
     * 验证用户是否已存在
     * @param param
     * @return
     */
    List<String> verifyUserInfo(Map param);

    /**
     * 新增用户信息
     * @param sysUser
     */
    void insertUserInfo(SysUser sysUser);

    /**
     * 新增用户角色关联
     * @param sysUserRole
     */
    Integer insertUserRole(SysUserRole sysUserRole);

    /**
     * 检验邀请码是否有效
     * @param sysUser
     * @return
     */
    Integer verifyInviterCode(SysUser sysUser);


    String existInviterCodeByUserId(SysUser sysUser);

    /**
     * 变更用户信息
     * @param sysUser
     */
    Integer updateSysUser(SysUser sysUser);

    /**
     * 更新用户密码
     * @param sysUser
     * @return
     */
    Integer updateUserPwd(SysUser sysUser);


    /**
     * 根据手机号查询用户信息
     * @param phoneNo
     * @return
     */
    List<SysUser> queryUserByPhoneNo(String phoneNo);

    /**
     * 更新unionid by openid
     * @param sysUser
     * @return
     */
    Integer updateSysUserUnionId(SysUser sysUser);

    /**
     * 删除用户的角色关系
     * @param sysUserRole
     * @return
     */
    Integer deleteRoleByUserId(SysUserRole sysUserRole);
}