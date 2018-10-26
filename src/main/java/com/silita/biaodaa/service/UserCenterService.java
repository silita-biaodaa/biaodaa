package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.ObjectUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import com.silita.biaodaa.utils.SignConvertUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
        CollecNotice vo = collecNoticeMapper.getCollecNoticeByUserIdAndNoticeId(collecNotice);
        //已关注
        if (vo != null) {
            return "您已关注该公告！";
        }
        collecNoticeMapper.insertCollecNotice(collecNotice);
        return "";
    }

    public void deleteCollectionNotice(CollecNotice collecNotice) {
        collecNoticeMapper.deleteCollecNoticeByUserIdAndNoticeId(collecNotice);
    }

    public PageInfo queryCollectionNotice(Page page, Map<String, Object> params) {
        List<Map<String, Object>> notices = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        if ("0".equals(params.get("type"))) {
            notices = collecNoticeMapper.listZhaoBiaoCollecNoticeByUserId(params);
        } else {
            notices = collecNoticeMapper.listZhongBiaoCollecNoticeByUserId(params);
            if (null != notices && notices.size() > 0) {
                CompanyLawEs companyLawEs;
                Map<String, Object> param = new HashMap<>();
                String key;
                for (Map<String, Object> map : notices) {
                    param.put("comName", map.get("oneName"));
                    key = RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                    companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
                    if (null != companyLawEs){
                        map.put("oneLaw", companyLawEs.getTotal());
                    }
                }
            }
        }
        PageInfo pageInfo = new PageInfo(notices);
        return pageInfo;
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
                        map.put("phone", tbCompanyService.solPhone(companyInfo.getPhone().trim(), "replace"));
                    }
                    if (null == map.get("regisCapital") && null != companyInfo.getRegisCapital()) {
                        map.put("regisCapital", companyInfo.getRegisCapital());
                    }
                }
            }
            if (!StringUtils.isEmpty(map.get("comName")) && !StringUtils.isEmpty(map.get("businessNum"))) {
                CertBasic certBasic = certBasicMap.get(map.get("comName") + "|" + map.get("businessNum"));
                if (certBasic != null) {
                    map.put("runScope", certBasic.getRunscope());
                }
                TbSafetyCertificate tbSafetyCertificate = safetyCertificateMap.get(map.get("comName"));
                if (tbSafetyCertificate != null) {
                    map.put("certNo", tbSafetyCertificate.getCertNo());
                    map.put("certDate", tbSafetyCertificate.getCertDate());
                    map.put("validDate", tbSafetyCertificate.getValidDate());
                }
                // TODO: 18/4/26  存续状态暂时写死
                map.put("subsist", "存续");
            }
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
