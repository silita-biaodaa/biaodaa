package com.silita.biaodaa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.silita.biaodaa.dao.TbVipRightsChangesMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.dao.VipInfoMapper;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.MessageService;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.to.OpenMemberTO;
import com.silita.biaodaa.to.UpdateVipDayTO;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.Constant.PROFIT_S_CODE_FIRST;

/**
 * 会员逻辑
 */
@Service("vipService")
public class VipServiceImpl implements VipService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private VipInfoMapper vipInfoMapper;

    @Autowired
    private UserTempBddMapper userTempBddMapper;

    @Autowired
    private TbVipRightsChangesMapper tbVipRightsChangesMapper;

    @Autowired
    private MessageService messageService;

    /**
     * 查询用户的活动收益次数
     *
     * @param sCode
     * @param userId
     * @return
     */
    public Integer queryUserProfitCount(String sCode, String userId) {
        return vipInfoMapper.queryUserProfitCount(sCode, userId);
    }

    @Async
    @Override
    public void sendUserVipMessage(String userId, String stdCode) {
        TbVipFeeStandard standard = this.queryFeeStdInfoByCode(stdCode);
        if (null == standard) {
            return;
        }
        TbVipInfo vipInfo = vipInfoMapper.queryVipInfoById(userId);
        if (null == vipInfo) {
            vipInfo.setExpiredDate(new Date());
        }
        int addDays = standard.getVipDays();
        Date hisExpiredDate = vipInfo.getExpiredDate();
        Date today = new Date();
        if (hisExpiredDate.before(today)) {
            hisExpiredDate = today;
        }
        Date newExpiredDate = MyDateUtils.addDays(hisExpiredDate, addDays);
        String expiredStr = MyDateUtils.getTime(newExpiredDate, "yyyy-MM-dd");
        StringBuffer msg = new StringBuffer("");
        if ("month".equals(stdCode)) {
            msg.append("一个月");
        } else if ("quarter".equals(stdCode)) {
            msg.append("三个月");
        } else if ("hlafYear".equals(stdCode)) {
            msg.append("半年");
        } else if ("year".equals(stdCode)) {
            msg.append("一年");
        }
        messageService.sendMessageSyetem(userId, "恭喜您，已成功开通会员服务", "您已成功开通" + msg.toString() + "标大大会员服务，有效期至" + expiredStr + "，快来体验吧!", "system", null);
    }

    /**
     * 增加用户会员权益（活动类）
     */
    @Transactional
    public synchronized String addUserProfit(String channel, String userId, String sCode, String... others) {
        String errMsg = null;
        if (sCode == null) {
            return "权益编码为空";
        }
        String inviterCode = ((others != null && others.length > 0) ? others[0] : null);
        try {
            if (userId == null) {
                userId = userTempBddMapper.queryUserByInviteCode(inviterCode).getPkid();
            }
            if (userId == null) {
                return "用户id为空（或根据推荐人邀请码匹配赠送用户id失败）。";
            }
            TbVipInfo tbVipInfo = vipInfoMapper.queryVipInfoById(userId);
            TbVipProfitSettings profitSettings = vipInfoMapper.queryProfitSettingsByCode(channel, sCode);
            if (PROFIT_S_CODE_FIRST.equals(sCode)) {
                if (vipInfoMapper.queryUserProfitCount(sCode, userId) > 0) {
                    logger.warn("权益赠送存在疑似并发请求。addUserProfit[sCode:" + sCode + "][userId:" + userId + "]");
                    return "权益赠送取消，已存在权益收益！";
                }
            }
            if (profitSettings != null) {
                //会员天数叠加
                UpdateVipDayTO to = updateVipInfos(tbVipInfo, userId, profitSettings.getVipDays());
                if (to.getVipInfoRecord() == 1) {//会员主表更新成功
                    //会员收益活动明细记录
                    TbVipProfits tbVipProfits = new TbVipProfits();
                    tbVipProfits.setVProfitsId(CommonUtil.getUUID());
                    tbVipProfits.setHisExpiredDate(to.getHisExpiredDate());
                    tbVipProfits.setSettingsCode(profitSettings.getSettingsCode());
                    tbVipProfits.setVId(to.getVid());
                    tbVipProfits.setIncreaseDays(profitSettings.getVipDays());
                    tbVipProfits.setCreateBy(channel);
                    tbVipProfits.setInviterCode(inviterCode);
                    int insertCount = vipInfoMapper.insertVipProfits(tbVipProfits);
                } else {
                    errMsg = "会员主表更新失败, 请重试。[userId:" + userId + "]";
                }
            } else {
                errMsg = "会员信息或活动权益配置为空！[userId:" + userId + "][profitSettings:" + profitSettings + "]";
            }

        } catch (Exception e) {
            logger.error(e, e);
            errMsg = "服务异常：" + e.getMessage();
        }
        return errMsg;
    }

    public List<TbVipFeeStandard> queryFeeStandard(String channel) {
        return vipInfoMapper.queryFeeStandard(channel);
    }

    @Override
    public List<TbVipFeeStandard> getFeeStandards(String channel) {
        return vipInfoMapper.queryFeeStandardReport(channel);
    }

    /**
     * 查询收益列表（分页）
     *
     * @param page
     * @param userId
     * @return
     */
    public PageInfo queryProfitInfo(Page page, String userId) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbVipProfits> list = vipInfoMapper.queryProfitInfo(userId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 获取用户的收益天数总和
     *
     * @param userId
     * @return
     */
    public Integer queryProfitTotal(String userId) {
        return vipInfoMapper.queryProfitTotal(userId);
    }

    private String preOpenMember(OpenMemberTO toOpenMember) {
        if (MyStringUtils.isNull(toOpenMember.getUserId())) {
            return "用户id不能为空";
        }
        if (MyStringUtils.isNull(toOpenMember.getFeeStandard())
                || MyStringUtils.isNull(toOpenMember.getFeeStandard().getStdCode())) {
            return "收费标准不能为空";
        }
        if (MyStringUtils.isNull(toOpenMember.getChannel())) {
            return "渠道信息不能为空";
        }
        return null;
    }

    /**
     * 增加vip权益天数,同时检查角色匹配程度
     *
     * @param tbVipInfo
     * @param userId
     * @param addDays
     * @return
     */
    private UpdateVipDayTO updateVipInfos(TbVipInfo tbVipInfo, String userId, int addDays) {
        UpdateVipDayTO updateVipDayTO = new UpdateVipDayTO();
        int vipInfoRecord = -1;
        String vid = null;
        Date hisExpiredDate = null;
        Date today = Calendar.getInstance().getTime();
        if (tbVipInfo != null) {
            vid = tbVipInfo.getVId();
            hisExpiredDate = tbVipInfo.getExpiredDate();
            if (hisExpiredDate == null || hisExpiredDate.before(today)) {
                hisExpiredDate = today;
            }
            Date newExpiredDate = MyDateUtils.addDays(hisExpiredDate, addDays);
            tbVipInfo.setExpiredDate(newExpiredDate);
            vipInfoRecord = vipInfoMapper.updateVipInfo(tbVipInfo);
        } else {
            tbVipInfo = new TbVipInfo();
            vid = CommonUtil.getUUID();
            tbVipInfo.setVId(vid);
            tbVipInfo.setUserId(userId);
            tbVipInfo.setExpiredDate(MyDateUtils.addDays(today, addDays));
            tbVipInfo.setLevel(1);
            vipInfoRecord = vipInfoMapper.insertVipInfo(tbVipInfo);
        }

        updateVipDayTO.setVid(vid);
//        updateVipDayTO.setHisExpiredDate(hisExpiredDate);
        updateVipDayTO.setHisExpiredDate(tbVipInfo.getExpiredDate());
        updateVipDayTO.setVipInfoRecord(vipInfoRecord);

        //检查角色关联关系
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setRoleCode("vip1");
        sysUserRole.setPkid(CommonUtil.getUUID());
        int delRoleCount = userTempBddMapper.deleteRoleByUserId(sysUserRole);
        if (delRoleCount > 0) {
            int roleUpdateCount = userTempBddMapper.insertUserRole(sysUserRole);
            updateVipDayTO.setRoleUpdateCount(roleUpdateCount);
        } else {
            logger.debug("角色无需变更，[userId:" + sysUserRole.getUserId() + "]");
        }

        return updateVipDayTO;
    }

    /**
     * 开通会员
     *
     * @param toOpenMember
     * @return
     */
    @Transactional
    public String openMemberRights(OpenMemberTO toOpenMember) {
        String paramCheck = preOpenMember(toOpenMember);
        if (paramCheck != null) {
            return paramCheck;
        }
        try {
            TbVipInfo tbVipInfo = vipInfoMapper.queryVipInfoById(toOpenMember.getUserId());
            TbVipRightsChanges vipRightsChanges = new TbVipRightsChanges();
            TbVipFeeStandard feeStandard = vipInfoMapper.queryFeeStandardByCode(toOpenMember.getFeeStandard().getStdCode());
            if (feeStandard != null) {
                UpdateVipDayTO to = updateVipInfos(tbVipInfo, toOpenMember.getUserId(), feeStandard.getVipDays());
                int vipInfoRecord = to.getVipInfoRecord();
                String vid = to.getVid();
                if (vipInfoRecord == 1) {//会员主表更新成功
                    //会员权益记录
                    vipRightsChanges.setHisExpiredDate(to.getHisExpiredDate());
                    vipRightsChanges.setVRightsId(CommonUtil.getUUID());
                    vipRightsChanges.setVId(vid);
                    vipRightsChanges.setVFeeStdId(feeStandard.getFeeStdId());
                    vipRightsChanges.setRightsNum(1);
                    vipRightsChanges.setModType("0");
                    vipRightsChanges.setIncreaseDays(feeStandard.getVipDays());
                    int rightsChangeCount = tbVipRightsChangesMapper.saveVipRightsChange(vipRightsChanges);
                    if (rightsChangeCount != 1) {
                        logger.warn("会员权益变更信息保存失败。[userId:" + toOpenMember.getUserId() + "]");
                    }
                    return null;
                } else {
                    return "会员主表更新失败, 请重试。";
                }
            } else {
                return "会员信息或收费标准配置为空！[userId:" + toOpenMember.getUserId() + "][feeStandard:" + feeStandard + "]";
            }
        } catch (Exception e) {
            logger.error(e, e);
            return "服务异常：" + e.getMessage();
        }
    }

    @Override
    public TbVipFeeStandard queryFeeStdInfoByCode(String stdCode) {
        return vipInfoMapper.queryFeeStandardByCode(stdCode);
    }

}
