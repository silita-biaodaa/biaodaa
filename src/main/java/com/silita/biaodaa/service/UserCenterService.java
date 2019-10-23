package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.silita.biaodaa.utils.RouteUtils.HUNAN_SOURCE;

/**
 * Created by 91567 on 2018/4/12.
 */
@Service("userCenterService")
public class UserCenterService {

    @Autowired
    private InvitationBddMapper invitationBddMapper;
    @Autowired
    private MessagePushMapper messagePushMapper;
    @Autowired
    private UserTempBddMapper userTempBddMapper;
    @Autowired
    private CollecNoticeMapper collecNoticeMapper;
    @Autowired
    private ColleCompanyMapper colleCompanyMapper;
    @Autowired
    private ReadedRecordMapper readedRecordMapper;
    @Autowired
    private TbCompanyInfoMapper tbCompanyInfoMapper;
    @Autowired
    private TbCompanyService tbCompanyService;
    @Autowired
    private MyRedisTemplate myRedisTemplate;

    public Integer updateUserInfo(SysUser sysUser) {
        return userTempBddMapper.updateSysUser(sysUser);
    }

    /**
     * 验证推荐人邀请码
     *
     * @param sysUser
     * @return
     */
    public boolean verifyInviterCode(SysUser sysUser) {
        Integer count = userTempBddMapper.verifyInviterCode(sysUser);
        if (count == null || count != 1) {
            return false;
        } else {
            String inviterCode = userTempBddMapper.existInviterCodeByUserId(sysUser);
            if (MyStringUtils.isNotNull(inviterCode)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public UserTempBdd updateUserTemp(UserTempBdd userTempBdd) {
        userTempBddMapper.updateUserTemp(userTempBdd);
        UserTempBdd vo = userTempBddMapper.getUserTempByUserId(userTempBdd.getUserid());
        //权限token
        if (vo != null) {
            Map<String, String> parameters = new HashedMap();
            parameters.put("name", vo.getUsername());
            parameters.put("password", vo.getUserpass());
            parameters.put("phone", vo.getUserphone());
            parameters.put("userId", vo.getUserid());
            try {
                String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                String parameterJson = JSONObject.toJSONString(parameters);
                String asB64 = Base64.getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                String xtoken = sign + "." + asB64;
                vo.setXtoken(xtoken);
            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }
        return vo;
    }

    public Integer updatePwd(SysUser sysUser) {
        return userTempBddMapper.updateUserPwd(sysUser);
    }

    public String verifyPhoneCode(Map params) {
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码失效或错误！";
        } else if ("1".equals(invitationVo.getInvitationState())) {
            return "验证码失效或错误！";
        }
        return null;
    }

    public void updateInvitationBddByCodeAndPhone(Map params) {
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
    }

    public String updatePassWord(UserTempBdd userTempBdd) {
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码错误或无效！";
        } else if ("1".equals(invitationVo.getInvitationState())) {
            return "验证码失效！";
        }
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if (userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        } else if (userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        userTempBddMapper.updatePassWdByUserIdAndPhone(userTempBdd);
        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
        return "";
    }

    public String insertCollectionNotice(CollecNotice collecNotice) {
        if (null != collecNotice && MyStringUtils.isNull(collecNotice.getSource())) {
            collecNotice.setSource(HUNAN_SOURCE);
        }
        CollecNotice vo = collecNoticeMapper.getCollecNoticeByUserIdAndNoticeId(collecNotice);
        //已关注
        if (vo != null) {
            return "您已关注该公告！";
        }
        collecNoticeMapper.insertCollecNotice(collecNotice);
        return "";
    }

    public void deleteCollectionNotice(CollecNotice collecNotice) {
        if (null != collecNotice && MyStringUtils.isNull(collecNotice.getSource())) {
            collecNotice.setSource(HUNAN_SOURCE);
        }
        collecNoticeMapper.deleteCollecNoticeByUserIdAndNoticeId(collecNotice);
    }

    public PageInfo queryCollectionNotice(Page page, Map<String, Object> params) {
        if (null == params.get("source")) {
            params.put("source", HUNAN_SOURCE);
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<CollecNotice> noticeList = collecNoticeMapper.queryCollecList(params);
        PageInfo pageInfo = new PageInfo(noticeList);
        return pageInfo;
    }

    public List<Map<String, Object>> getNoticeList(List<CollecNotice> list, Map<String, Object> params) {
        List<Map<String, Object>> notices = new ArrayList<>();
        if (null == list || list.size() <= 0) {
            return notices;
        }
        Map<String, Object> map;
        Map<String, Object> result;
        for (CollecNotice coll : list) {
            map = new HashMap<>();
            map.put("noticeId", coll.getNoticeid());
            map.put("source", coll.getSource());
            map.put("userId", params.get("userid"));
            map.put("pdModeType",coll.getSource()+"_pbmode");
            if ("0".equals(params.get("type")) || "1".equals(params.get("type"))) {
                result = collecNoticeMapper.listZhaoBiaoCollecNoticeById(map);
            } else {
                result = collecNoticeMapper.listZhongBiaoCollecNoticeById(map);
            }
            if (null != result && MapUtils.isNotEmpty(result)) {
                result.put("collected",true);
                notices.add(result);
            }
        }
        if ("2".equals(params.get("type")) && null != notices && notices.size() > 0) {
            CompanyLawEs companyLawEs;
            Map<String, Object> param = new HashMap<>();
            String key;
            for (Map<String, Object> mapNtic : notices) {
                param.put("comName", mapNtic.get("oneName"));
                key = RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
                if (null != companyLawEs) {
                    mapNtic.put("oneLaw", companyLawEs.getTotal());
                }
            }
        }
        return notices;
    }

    public String insertCollectionCompany(ColleCompany colleCompany) {
        ColleCompany vo = colleCompanyMapper.getCollectionCompanyByUserIdAndCompanyId(colleCompany);
        //已关注
        if (vo != null) {
            return "您已关注该公司！";
        }
        colleCompanyMapper.insertCollectionCompany(colleCompany);
        return "";
    }

    public void deleteCollectionCompany(ColleCompany colleCompany) {
        colleCompanyMapper.deleteCollectionCompany(colleCompany);
    }

    public PageInfo querCollectionCompany(Page page, Map<String, Object> params) {
        Integer isVip = MapUtils.getInteger(params, "isVip");
        List<Map<String, Object>> companys = new ArrayList<>();
        Map<String, CertBasic> certBasicMap = tbCompanyService.getCertBasicMap();
        Map<String, TbSafetyCertificate> safetyCertificateMap = tbCompanyService.getSafetyCertMap();
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        companys = colleCompanyMapper.listCollectionCompany(params);
        TbCompanyInfo companyInfo = null;
        for (Map<String, Object> map : companys) {
            if (null != map.get("comName")) {
                companyInfo = tbCompanyInfoMapper.queryDetailByComName(MapUtils.getString(map, "comName"), CommonUtil.getCode(MapUtils.getString(map, "regisAddress")));
                if (null != companyInfo) {
                    if (null != companyInfo.getPhone()) {
//                        map.put("phone",companyInfo.getPhone().split(";")[0].trim());
                        if (null != isVip && 1 == isVip) {
                            map.put("phone", tbCompanyService.solPhone(companyInfo.getPhone().trim(), "show"));
                        } else {
                            map.put("phone", tbCompanyService.solPhone(companyInfo.getPhone().trim(), "replace"));
                        }
                    }
                    if (null == map.get("regisCapital") && null != companyInfo.getRegisCapital()) {
                        map.put("regisCapital", companyInfo.getRegisCapital());
                    }
                }
            }
            // TODO: 18/4/26  存续状态暂时写死
            map.put("subsist", "存续");
        }
        PageInfo pageInfo = new PageInfo(companys);
        return pageInfo;
    }

    public PageInfo queryMessageList(Page page, Map<String, Object> params) {
        List<MessagePush> messagePushes = new ArrayList<>();

        List<MessagePush> tempMessagePushes = messagePushMapper.listMessageByUserIdAndType(params);
        if (tempMessagePushes.size() > 0) {
            int msgId;
            Map<String, Object> temp;
            ReadedRecord readedRecord;
            for (int i = 0; i < tempMessagePushes.size(); i++) {
                msgId = tempMessagePushes.get(i).getId();
                temp = new HashMap<>();
                temp.put("msgId", msgId);
                temp.put("userid", tempMessagePushes.get(i).getUserid());
                Integer count = readedRecordMapper.getTotalByMsgId(temp);
                if (count == 0) {
                    readedRecord = new ReadedRecord();
                    readedRecord.setMsgId(msgId);
                    readedRecord.setUserid(tempMessagePushes.get(i).getUserid());
                    readedRecordMapper.insertReadedRecord(readedRecord);
                }
            }
        }
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        messagePushes = messagePushMapper.listMessage(params);
        PageInfo pageInfo = new PageInfo(messagePushes);
        return pageInfo;
    }

    public void updateAllReadedRecordToRead(Map<String, Object> params) {
        List<Map<String, Object>> msgIds = messagePushMapper.listIdByUserId(params);
        if (null != msgIds && msgIds.size() > 0) {
            readedRecordMapper.batchUpdateReadOrReadedByUserIds(msgIds);
        }
    }

    public MessagePush getMessagePushById(Map<String, Object> params) {
        readedRecordMapper.updateReadOrReadedById(params);
        String id = String.valueOf(params.get("msgId"));
        return messagePushMapper.getMessageById(id);
    }

    public UserTempBdd getUserTempByUserId(String userid) {
        return userTempBddMapper.getUserTempByUserId(userid);
    }

}
