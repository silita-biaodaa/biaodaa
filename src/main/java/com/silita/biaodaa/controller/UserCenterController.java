package com.silita.biaodaa.controller;

/**
 * Created by 91567 on 2018/4/12.
 */

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.service.AuthorizeService;
import com.silita.biaodaa.service.UserCenterService;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import com.silita.biaodaa.utils.RegexUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.silita.biaodaa.common.Constant.*;
import static com.silita.biaodaa.controller.BaseController.bulidUserPwd;

/**
 * 用户信息模块
 * Created by 91567 on 2018/4/12.
 */
@RequestMapping("/userCenter")
@Controller
public class UserCenterController {
    private static final Logger logger = Logger.getLogger(CompanyController.class);
    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private VipService vipService;

    /**
     * TODO: 修改用户信息
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUserTemp", produces = "application/json;charset=utf-8")
    public Map<String, Object> updateUserTemp(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try {
            userTempBdd.setUserid(VisitInfoHolder.getUid());
            UserTempBdd vo = userCenterService.updateUserTemp(userTempBdd);
            if (vo != null) {
                result.put("msg", "更新账号基本信息成功！");
                result.put("data", vo);
            }
        } catch (Exception e) {
            logger.error("更新账号基本信息异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", "更新账号基本信息失败");
        }
        return result;
    }

    /**
     * 过滤不修改的字段
     * @param sysUser
     */
    private void filterParams(SysUser sysUser){
        sysUser.setCreated(null);
        sysUser.setCreateBy(null);
        sysUser.setPhoneNo(null);
        sysUser.setLoginPwd(null);
        sysUser.setOwnInviteCode(null);
        sysUser.setChannel(null);
    }

    private void clearBlank(SysUser sysUser){
        if(sysUser.getInviterCode()!=null){
            sysUser.setInviterCode(sysUser.getInviterCode().trim());
        }
        if(sysUser.getLoginName()!=null){
            sysUser.setLoginName(sysUser.getLoginName().trim());
        }
        if(sysUser.getBirthYear()!=null){
            sysUser.setBirthYear(sysUser.getBirthYear().trim());
        }
        if(sysUser.getNikeName()!=null){
            sysUser.setNikeName(sysUser.getNikeName().trim());
        }
        if(sysUser.getEmail()!=null){
            sysUser.setEmail(sysUser.getEmail().trim());
        }
        if(sysUser.getInCity()!=null){
            sysUser.setInCity(sysUser.getInCity().trim());
        }
        if(sysUser.getInCompany()!=null){
            sysUser.setInCompany(sysUser.getInCompany().trim());
        }
        if(sysUser.getPosition()!=null){
            sysUser.setPosition(sysUser.getPosition().trim());
        }
        if(sysUser.getLoginPwd()!=null){
            sysUser.setLoginPwd(sysUser.getLoginPwd().trim());
        }
    }

    /**
     * (新)修改密码
     *
     * @param sysUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updatePwd", produces = "application/json;charset=utf-8")
    public Map<String, Object> updatePwd(@RequestBody  SysUser sysUser) {
        Map result = new HashMap();
        try {
            Map paramResult =validateUserPwdInfo(sysUser);
            if(paramResult!= null){
                return paramResult;
            }
            bulidUserPwd(sysUser);
            Integer records = userCenterService.updatePwd(sysUser);
            if(records ==1) {
                result.put("code", Constant.SUCCESS_CODE);
                result.put("msg", "密码修改成功！");
            }else{
                result.put("code", Constant.FAIL_CODE);
                result.put("msg", "更新密码失败，请重试！");
            }
        } catch (Exception e) {
            logger.error("更改密码异常！" + e.getMessage(), e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 用户信息变更
     * @param sysUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUserInfo", produces = "application/json;charset=utf-8")
    public Map<String, Object> updateUserInfo(@RequestBody SysUser sysUser) {
        Map result = new HashMap();
        try {
            sysUser.setPkid(VisitInfoHolder.getUid());
            clearBlank(sysUser);
            filterParams(sysUser);
            String errCode = preUpdateUserInfo(sysUser);
            if(errCode == null) {
                Integer records = userCenterService.updateUserInfo(sysUser);
                if(records ==1) {
                    SysUser vo = authorizeService.memberLogin(sysUser);
                    if(MyStringUtils.isNotNull(sysUser.getInviterCode())){
                        String errMsg = vipService.addUserProfit(vo.getChannel(), null, PROFIT_S_CODE_INVITE, sysUser.getInviterCode());
                        if (errMsg != null) {
                            logger.error("会员权益赠送出错[sCode:"+PROFIT_S_CODE_INVITE+"]：" + errMsg);
                        }
                    }
                    result.put("code", Constant.SUCCESS_CODE);
                    result.put("msg", "更新用户信息成功！");
                    result.put("data", vo);
                }else{
                    result.put("code", errCode);
                    result.put("msg", "更新用户失败，请重试！");
                }
            }else {
                result.put("code", errCode);
                String errMsg = null;
                if (errCode.equals(Constant.ERR_VERIFY_IVITE_CODE)) {
                    errMsg = "推荐人邀请码有误或用户已存在推荐人,请确认。";
                } else if (errCode.equals(Constant.ERR_VERIFY_USER_ID_CODE)){
                    errMsg = "用户信息获取失败，请重新登录。";
                }else{
                    errMsg="输入数据有误。";
                }

                result.put("msg", errMsg);
            }
        } catch (Exception e) {
            String err = "更新账号基本信息异常！" + e.getMessage();
            logger.error(err, e);
            result.put("code", Constant.FAIL_CODE);
            result.put("msg", err);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/refreshUserInfo", produces = "application/json;charset=utf-8")
    public Map<String, Object> refreshUserInfo() {
        Map result = new HashMap();
        try {
            SysUser sysUser = new SysUser();
            sysUser.setPkid(VisitInfoHolder.getUid());
            SysUser vo = authorizeService.refreshUserInfo(sysUser);
            if(vo !=null){
                result.put("code", Constant.SUCCESS_CODE);
                result.put("msg", "刷新用户信息成功！");
                result.put("data", vo);
            }else{
                result.put("code", Constant.FAIL_CODE);
                result.put("msg", "刷新用户信息失败，请重试！");
            }
        } catch (Exception e) {
            String err = "刷新用户信息异常！" + e.getMessage();
            logger.error(err, e);
            result.put("code", Constant.FAIL_CODE);
            result.put("msg", err);
        }
        return result;
    }

    /**
     * 更新密码数据校验
     * @param sysUser
     * @return
     */
    private Map validateUserPwdInfo(SysUser sysUser){
        Map result = new HashMap();
        if(MyStringUtils.isNull(sysUser.getLoginPwd())){
            result.put("code",Constant.ERR_LOGIN_PWD);
            result.put("msg","新密码不能为空.");
            return result;
        }
        if(MyStringUtils.isNull(sysUser.getVerifyCode())){
            result.put("code",Constant.ERR_VIEW_CODE);
            result.put("msg","手机验证码为空.");
            return result;
        }
        if(MyStringUtils.isNull(sysUser.getPhoneNo())){
            result.put("code",Constant.ERR_VIEW_CODE);
            result.put("msg","手机号为空.");
            return result;
        }

        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", sysUser.getPhoneNo());
        params.put("invitationCode", sysUser.getVerifyCode());
        String eMsg =userCenterService.verifyPhoneCode(params);
        if(eMsg !=null){
            result.put("code",Constant.ERR_VERIFY_PHONE_CODE);
            result.put("msg",eMsg);
            return result;
        }
        //更新验证码状态
        userCenterService.updateInvitationBddByCodeAndPhone(params);

        return  null;
    }

    /**
     * 用户数据校验
     * @param sysUser
     * @return
     */
    private String preUpdateUserInfo(SysUser sysUser){
        if(MyStringUtils.isNull(sysUser.getPkid())){//用户id
            return Constant.ERR_VERIFY_USER_ID_CODE;
        }
        if(MyStringUtils.isNotNull(sysUser.getInviterCode())){//推荐人邀请码校验
            if(sysUser.getInviterCode().length()!=6){
                return Constant.ERR_VERIFY_IVITE_CODE;
            }else{
                if(!userCenterService.verifyInviterCode(sysUser)){
                    return Constant.ERR_VERIFY_IVITE_CODE;
                }
            }
        }

        if(MyStringUtils.isNotNull(sysUser.getImageUrl())){//头像链接
            if(sysUser.getImageUrl().length()>200){
                return ERR_IMGURL_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getNikeName())){//昵称
            if(sysUser.getNikeName().length()>50){
                return ERR_NICE_NAME_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getPhoneNo())){//手机号
            if(!RegexUtils.matchExists(sysUser.getPhoneNo()
                    ,"^0{0,1}(13[0-9]|15[7-9]|153|156|18[7-9]|1[0-9][0-9])[0-9]{8}$")){
                return ERR_PHONE_NO_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getEmail())){//邮箱
            if(!RegexUtils.matchExists(sysUser.getEmail()
                    ,"^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{1,6}$")){
                return ERR_EMAIL_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getUserName())){//用户姓名
            if(sysUser.getUserName().length()>100){
                return ERR_USER_NAME_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getSex())){//性别
            if(sysUser.getSex()>5){
                return Constant.ERR_SEX_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getBirthYear())){//生日
            if(!RegexUtils.matchExists(sysUser.getBirthYear()
                    ,"^[12][0-9]{3}-[0-1][0-9]-[0-3][0-9]$")){
                return ERR_BIR_DATE_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getInCity())){//城市
            if(sysUser.getInCity().length()>100){
                return ERR_CITY_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getInCompany())){//公司名
            if(sysUser.getInCompany().length()>100){
                return ERR_COMPANY_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getPosition())){//职位
            if(sysUser.getPosition().length()>50){
                return ERR_POS_CODE;
            }
        }
        if(MyStringUtils.isNotNull(sysUser.getLoginName())){//登录账号名
            if(sysUser.getLoginName().length()>50){
                return ERR_LOGIN_NAME;
            }
        }
        return null;
    }

    /**
     * 修改密码
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updatePassWord", produces = "application/json;charset=utf-8")
    public Map<String, Object> updatePassWord(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            userTempBdd.setUserid(VisitInfoHolder.getUid());
            String msg = userCenterService.updatePassWord(userTempBdd);
            if ("".equals(msg)) {
                result.put("msg", "更改密码成功！");
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("更改密码异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 文件上传
     *
     * @param files
     * @return
     */
    @ResponseBody
//    @RequestMapping(value = "/updateHeadPortrait", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> updateHeadPortrait(@RequestParam("files") MultipartFile[] files) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("msg", "文件上传成功！");
        try {
            MultipartFile headPortraitImg = files[0];
            File uploadFile = new File(PropertiesUtils.getProperty("HEAD_PORTRAIT_PATH") + headPortraitImg.getOriginalFilename());
            headPortraitImg.transferTo(uploadFile);
            result.put("imaPath", uploadFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("文件上传异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
            result.put("imgPath", "");
        }
        return result;
    }


    /**
     * TODO: 文件上传
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
//    @RequestMapping(value = "/updateHeadPortrait2", method = RequestMethod.POST)
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("msg", "文件上传成功！");

        response.addHeader("key", "value");

        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession()
                .getServletContext());
        if (commonsMultipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile headPortraitImg = multipartHttpServletRequest.getMultiFileMap().get("file").get(0);

            File uploadFile = new File(PropertiesUtils.getProperty("HEAD_PORTRAIT_PATH") + headPortraitImg.getOriginalFilename());
            try {
                headPortraitImg.transferTo(uploadFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.put("imaPath", uploadFile.getAbsolutePath());

        }
        return result;
    }


    /**
     * TODO: 关注公告
     *
     * @param collecNotice
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/collectionNotice", produces = "application/json;charset=utf-8")
    public Map<String, Object> collectionNotice(@RequestBody CollecNotice collecNotice) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            collecNotice.setUserid(VisitInfoHolder.getUid());
            String msg = userCenterService.insertCollectionNotice(collecNotice);
            if ("".equals(msg)) {
                result.put("msg", "关注公告成功！");
            } else {
                result.put("msg", msg);
                result.put("code", 2);
            }
        } catch (Exception e) {
            logger.error("关注公告异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 取消关注公告
     *
     * @param collecNotice
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cancelCollectionNotice", produces = "application/json;charset=utf-8")
    public Map<String, Object> cancelCollectionNotice(@RequestBody CollecNotice collecNotice) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            collecNotice.setUserid(VisitInfoHolder.getUid());
            userCenterService.deleteCollectionNotice(collecNotice);
            result.put("msg", "取消关注成功！");
        } catch (Exception e) {
            logger.error("取消关注异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 关注公告列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listCollectionNotice", produces = "application/json;charset=utf-8")
    public Map<String, Object> listCollectionNotice(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取公告关注列表成功!");
        result.put("pageNum", 1);
        result.put("pageSize", 0);
        result.put("total", 0);
        result.put("pages", 1);

        try {
            params.put("userid", VisitInfoHolder.getUid());
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            Page page = new Page();
            page.setPageSize(MapUtils.getInteger(params, "pageSize"));
            page.setCurrentPage(MapUtils.getInteger(params, "pageNo"));

            PageInfo pageInfo = userCenterService.queryCollectionNotice(page, params);
            result.put("data", userCenterService.getNoticeList(pageInfo.getList(),params));
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error("获取公告关注列表异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    /**
     * TODO: 关注企业
     *
     * @param colleCompany
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/collectionCompany", produces = "application/json;charset=utf-8")
    public Map<String, Object> collectionCompany(@RequestBody ColleCompany colleCompany) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            colleCompany.setUserid(VisitInfoHolder.getUid());
            String msg = userCenterService.insertCollectionCompany(colleCompany);
            if ("".equals(msg)) {
                result.put("msg", "关注企业成功！");
            } else {
                result.put("msg", msg);
                result.put("code", 2);
            }
        } catch (Exception e) {
            logger.error("关注企业异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 取消关注
     *
     * @param colleCompany
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cancelCollectionCompany", produces = "application/json;charset=utf-8")
    public Map<String, Object> cancelcollectionCompany(@RequestBody ColleCompany colleCompany) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            colleCompany.setUserid(VisitInfoHolder.getUid());
            userCenterService.deleteCollectionCompany(colleCompany);
            result.put("msg", "取消关注成功！");
        } catch (Exception e) {
            logger.error("取消关注异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 关注公司列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listCollectionCompany", produces = "application/json;charset=utf-8")
    public Map<String, Object> listCollectionCompany(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取消息列表成功!");
        result.put("pageNum", 1);
        result.put("pageSize", 0);
        result.put("total", 0);
        result.put("pages", 1);

        try {
            params.put("userid", VisitInfoHolder.getUid());
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            Page page = new Page();
            page.setPageSize(MapUtils.getInteger(params, "pageSize"));
            page.setCurrentPage(MapUtils.getInteger(params, "pageNo"));

            PageInfo pageInfo = userCenterService.querCollectionCompany(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error("获消息列表异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    /**
     * TODO: 消息
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listMessageByUserId", produces = "application/json;charset=utf-8")
    public Map<String, Object> listMessageByUserId(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取消息列表成功!");
        result.put("pageNum", 1);
        result.put("pageSize", 0);
        result.put("total", 0);
        result.put("pages", 1);

        try {
            params.put("userid", VisitInfoHolder.getUid());
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            Page page = new Page();
            page.setPageSize(MapUtils.getInteger(params, "pageSize"));
            page.setCurrentPage(MapUtils.getInteger(params, "pageNo"));

            PageInfo pageInfo = userCenterService.queryMessageList(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error("获消息列表异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 标记已读
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/allReadedRecordToRead", produces = "application/json;charset=utf-8")
    public Map<String, Object> allReadedRecordToRead(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("msg", "标记已读成功！");

        try {
            params.put("userid", VisitInfoHolder.getUid());
            userCenterService.updateAllReadedRecordToRead(params);
        } catch (Exception e) {
            logger.error("标记已读异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 消息详情
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMessagePushById", produces = "application/json;charset=utf-8")
    public Map<String, Object> getMessagePushById(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("msg", "获取消息详情失败！");
        result.put("data", null);

        try {
            params.put("userid", VisitInfoHolder.getUid());
            MessagePush vo = userCenterService.getMessagePushById(params);
            if (vo != null) {
                result.put("code", 1);
                result.put("msg", "获取消息详情成功！");
                result.put("data", vo);
            }
        } catch (Exception e) {
            logger.error("获取消息详情异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserTemp", produces = "application/json;charset=utf-8")
    public Map<String, Object> getUserTemp() {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("msg", "获取用户信息失败！");
        result.put("data", null);

        try {
            String userid = VisitInfoHolder.getUid();
            UserTempBdd vo = userCenterService.getUserTempByUserId(userid);
            if (vo != null) {
                result.put("code", 1);
                result.put("msg", "获取用户信息成功！");
                result.put("data", vo);
            }
        } catch (Exception e) {
            logger.error("获取用户信息异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }


}
