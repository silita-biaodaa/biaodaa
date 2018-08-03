package com.silita.biaodaa.controller;

/**
 * Created by 91567 on 2018/4/12.
 */

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.ColleCompany;
import com.silita.biaodaa.model.CollecNotice;
import com.silita.biaodaa.model.MessagePush;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.service.UserCenterService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    private CompanyController companyController;


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
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * TODO: 修改密码
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
            result.put("data", pageInfo.getList());
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
            colleCompany.setCompanyid(companyController.getComId(colleCompany.getCompanyid()));
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
            colleCompany.setCompanyid(companyController.getComId(colleCompany.getCompanyid()));
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
