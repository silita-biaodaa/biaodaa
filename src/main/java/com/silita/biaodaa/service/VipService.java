package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbVipRightsChangesMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.dao.VipInfoMapper;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.to.ToOpenMember;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会员逻辑
 */
@Service
public class VipService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private VipInfoMapper vipInfoMapper;

    @Autowired
    private UserTempBddMapper userTempBddMapper;

    @Autowired
    private TbVipRightsChangesMapper tbVipRightsChangesMapper;

    public List<TbVipFeeStandard> queryFeeStandard(String channel){
        return vipInfoMapper.queryFeeStandard(channel);
    }

    /**
     * 查询收益列表（分页）
     * @param page
     * @param userId
     * @return
     */
    public PageInfo queryProfitInfo(Page page, String userId){
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbVipProfits> list = vipInfoMapper.queryProfitInfo(userId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 获取用户的收益天数总和
     * @param userId
     * @return
     */
    public Integer queryProfitTotal(String userId){
        return vipInfoMapper.queryProfitTotal(userId);
    }

    private String preOpenMember(ToOpenMember toOpenMember){
        if(MyStringUtils.isNull(toOpenMember.getUserId())){
            return "用户id不能为空";
        }
        if(MyStringUtils.isNull(toOpenMember.getFeeStandard())
                || MyStringUtils.isNull(toOpenMember.getFeeStandard().getStdCode())){
            return "收费标准不能为空";
        }
        if(MyStringUtils.isNull(toOpenMember.getChannel())){
            return "渠道信息不能为空";
        }
        return null;
    }

    public String openMemberRights(ToOpenMember toOpenMember){
        toOpenMember.setUserId(VisitInfoHolder.getUid());
        String paramCheck = preOpenMember(toOpenMember);
        if(paramCheck != null){
            return paramCheck;
        }
        try {
            TbVipInfo tbVipInfo = vipInfoMapper.queryVipInfoById(toOpenMember.getUserId());
            TbVipRightsChanges vipRightsChanges = new TbVipRightsChanges();
            TbVipFeeStandard feeStandard = vipInfoMapper.queryFeeStandardByCode(toOpenMember.getFeeStandard().getStdCode());
            String vid = null;

            int vipInfoRecord = -1;
            if (tbVipInfo != null) {
                vid = tbVipInfo.getVId();
                Date hisExpiredDate = tbVipInfo.getExpiredDate();
                vipRightsChanges.setHisExpiredDate(hisExpiredDate);
                Date newExpiredDate = MyDateUtils.addDays(hisExpiredDate, feeStandard.getVipDays());
                tbVipInfo.setExpiredDate(newExpiredDate);
                tbVipInfo.setUpdateBy(toOpenMember.getChannel());
                vipInfoRecord = vipInfoMapper.updateVipInfo(tbVipInfo);
            } else {
                tbVipInfo = new TbVipInfo();
                vid = CommonUtil.getUUID();
                tbVipInfo.setVId(vid);
                tbVipInfo.setUserId(toOpenMember.getUserId());
                tbVipInfo.setCreateBy(toOpenMember.getChannel());
                tbVipInfo.setExpiredDate(MyDateUtils.addDays(new Date(), feeStandard.getVipDays()));
                tbVipInfo.setLevel(1);
                vipInfoRecord = vipInfoMapper.insertVipInfo(tbVipInfo);
            }

            int roleUpdateCount = -1;
            int rightsChangeCount = -1;
            if (vipInfoRecord == 1) {//会员主表更新成功
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(toOpenMember.getUserId());
                sysUserRole.setRoleCode("vip1");
                sysUserRole.setPkid(CommonUtil.getUUID());
                roleUpdateCount = userTempBddMapper.deleteRoleByUserId(sysUserRole);
                if (roleUpdateCount > 0) {
                    roleUpdateCount = userTempBddMapper.insertUserRole(sysUserRole);
                } else {
                    logger.debug("角色无需变更，[userId:" + sysUserRole.getUserId() + "]");
                }

                //会员
                vipRightsChanges.setVRightsId(CommonUtil.getUUID());
                vipRightsChanges.setVId(vid);
                vipRightsChanges.setVFeeStdId(feeStandard.getFeeStdId());
                vipRightsChanges.setRightsNum(1);
                vipRightsChanges.setModType("0");
                vipRightsChanges.setIncreaseDays(feeStandard.getVipDays());
                rightsChangeCount = tbVipRightsChangesMapper.saveVipRightsChange(vipRightsChanges);
                if (rightsChangeCount != 1) {
                    logger.warn("会员权益变更信息保存失败。[userId:" + sysUserRole.getUserId() + "]");
                }
                return null;
            } else {
                return "会员主表更新失败, 请重试。";
            }
        }catch (Exception e){
            logger.error(e,e);
            return "服务异常："+e.getMessage();
        }
    }

}
