package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.common.SnatchContent;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.service.CommonService;
import com.silita.biaodaa.service.NoticeService;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ObjectUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHAOBIAO;
import static org.apache.commons.collections.MapUtils.getString;

/**
 * Created by dh on 2018/4/9.
 */
@RequestMapping("/notice")
@Controller
public class NoticeController extends BaseController{

    private Log logger = LogFactory.getLog(NoticeController.class);

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CommonService commonService;

    private void settingUserId(Map params){
        String userId = VisitInfoHolder.getUid();
        if(MyStringUtils.isNotNull(userId)) {
            params.put("userId", userId);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/searchList",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> searchList(@RequestBody Map params){
        Map resultMap = new HashMap();
        try {
//        search.setTypeToInt(type);
//        encodingConvert(search);
            settingUserId(params);

            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            Integer pageSize = page.getPageSize();
            String kbDateStart= MapUtils.getString(params, "kbDateStart");
            String kbDateEnd= MapUtils.getString(params, "kbDateEnd");
            if(MyStringUtils.isNotNull(kbDateStart)
                    && MyDateUtils.getDistanceOfTwoDate(kbDateStart,this.minDate)>0){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "开标开始时间超出访问范围！");
            }else if(MyStringUtils.isNotNull(kbDateEnd)
                    && MyDateUtils.getDistanceOfTwoDate(kbDateEnd,this.minDate)>0){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "开标结束时间超出访问范围！");
            }else if(pageSize>maxPageSize || pageNo>maxPageNum){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "超出访问范围！");
            }else {
                if (pageNo >= maxPageNum) {
                    resultMap.put("isLastPage", true);
                } else {
                    resultMap.put("isLastPage", false);
                }

                int paramHash = ObjectUtils.buildMapParamHash(params);
                String listKey = RedisConstantInterface.GG_SEARCH_LIST + paramHash;
                PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
                if (pageInfo == null) {
                    pageInfo = noticeService.searchNoticeList(page, params);
                    if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                        myRedisTemplate.setObject(listKey, pageInfo, RedisConstantInterface.LIST_OVER_TIME);
                    }
                }
                buildReturnMap(resultMap,pageInfo);
                successMsg(resultMap);
            }
            //        recordAccessCount(request,params); 记录访问数
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return resultMap;
    }

//    @RequestParam json对象参数
    @ResponseBody
    @RequestMapping(value = "/queryList",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryList(@RequestBody Map params){
        Map resultMap = new HashMap();
        try {
            settingUserId(params);
//        search.setTypeToInt(type);
//        encodingConvert(search);
            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            Integer pageSize = page.getPageSize();
            String kbDateStart= MapUtils.getString(params, "kbDateStart");
            String kbDateEnd= MapUtils.getString(params, "kbDateEnd");
            if(MyStringUtils.isNotNull(kbDateStart)
                    && MyDateUtils.getDistanceOfTwoDate(kbDateStart,this.minDate)>0){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "开标开始时间超出访问范围！");
            }else if(MyStringUtils.isNotNull(kbDateEnd)
                    && MyDateUtils.getDistanceOfTwoDate(kbDateEnd,this.minDate)>0){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "开标结束时间超出访问范围！");
            }else if(pageSize>maxPageSize || pageNo>maxPageNum){
                resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
                resultMap.put(this.MSG_FLAG, "超出访问范围！");
            }else {
                if (pageNo >= maxPageNum) {
                    resultMap.put("isLastPage", true);
                } else {
                    resultMap.put("isLastPage", false);
                }

                int paramHash = ObjectUtils.buildMapParamHash(params);
                String listKey = RedisConstantInterface.GG_LIST + paramHash;
                PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
                if (pageInfo == null) {
                    pageInfo = noticeService.queryNoticeList(page, params);
                    if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                        myRedisTemplate.setObject(listKey, pageInfo, RedisConstantInterface.LIST_OVER_TIME);
                    }
                }
                buildReturnMap(resultMap,pageInfo);
                successMsg(resultMap);
            }
            //        recordAccessCount(request,params); 记录访问数
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/detail/{id}",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryDetail(@PathVariable Long id,@RequestBody Map params){
        Map resultMap = new HashMap();
        try {
            settingUserId(params);
            params.put("id",id);
            int paramHash = ObjectUtils.buildMapParamHash(params);
            String listKey = RedisConstantInterface.GG_DETAIL + paramHash;
            List<Map> detailList = (List<Map>) myRedisTemplate.getObject(listKey);
            if(detailList ==null) {
                detailList = noticeService.queryNoticeDetail(params);
                if(detailList!=null && detailList.size()>0){
                    myRedisTemplate.setObject(listKey,detailList,RedisConstantInterface.DETAIL_OVER_TIME);
                }
            }
            Long relNoticeCount =  noticeService.queryRelCount(id);
            resultMap.put(DATA_FLAG,detailList);
            resultMap.put("relNoticeCount",relNoticeCount);//相关公告数量

            //招标详情
            String type= MapUtils.getString(params, "type");
            if(type.equals(SNATCHURL_ZHAOBIAO)){
                Integer relCompanySize = noticeService.queryCompanyCountById(params);
                List<Map> fileList = noticeService.queryNoticeFile(id);
                int fileSize =0;
                if(fileList !=null && fileList.size()>0){
                    fileSize = fileList.size();
                }
                resultMap.put("relCompanySize",relCompanySize);//资质相关企业
                resultMap.put("fileCount",fileSize);//文件列表
            }
            successMsg(resultMap);
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return  resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/queryRelNotice/{id}",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryRelNotice(@PathVariable Long id){
        Map resultMap = new HashMap();
        try{
            Map argMap = new HashMap();
            argMap.put("id",id);
            settingUserId(argMap);
            List<Map> relationNotices =  noticeService.queryRelations(argMap);
            resultMap.put(DATA_FLAG,relationNotices);
            successMsg(resultMap);
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/queryNoticeFile/{id}",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryNoticeFile(@PathVariable Long id){
        Map resultMap = new HashMap();
        try{
            List<Map> fileList =  noticeService.queryNoticeFile(id);
            resultMap.put(DATA_FLAG,fileList);
            successMsg(resultMap);
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return resultMap;
    }


    @ResponseBody
    @RequestMapping(value = "/queryCompanyList/{id}",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryCompanyList(@PathVariable Long id,@RequestBody Map params){
        Map resultMap = new HashMap();
        try{
            settingUserId(params);
            params.put("id",id);
            Page page =buildPage(params);
            PageInfo pageInfo = noticeService.queryCompanyListById(page,params);
            buildReturnMap(resultMap,pageInfo);
            successMsg(resultMap);
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(resultMap,e.getMessage());
        }
        return resultMap;
    }

    private void recordAccessCount(HttpServletRequest request,Map params){
        String type =  getString(params,"type");
        Map<String, String> preMap = parseRequest(request);
        String userId = preMap.get("userId");
        String ipAddr = preMap.get("ipAddr");
        params.put("userId",userId);
        params.put("ipAddr",ipAddr);
        //记录访问数
        if (userId != null) {
            if (SnatchContent.ZHONG_BIAO_TYPE.equals(type)) {//中标
                commonService.insertRecordLog("zhongbiao", userId, ipAddr);
            }else {
                commonService.insertRecordLog("zhaobiaoxinxi", userId, ipAddr);
            }
        }
    }

    //解析http信息
    private Map<String,String> parseRequest(HttpServletRequest request){
        String userId= (String) request.getSession().getAttribute("userId");
        String ipAddr = request.getHeader("X-real-ip");//获取用户真实ip
        HashMap map = new HashMap();
        map.put("userId",userId);
        map.put("ipAddr",ipAddr);
        return map;
    }

    /**
     * 查询公告文章列表
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/queryArticleList", method=RequestMethod.POST, produces="application/json;charset=utf-8")
    public Map<String, Object> queryArticleList(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "公告文章列表查询成功!");
        try {
            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            Integer pageSize = page.getPageSize();
            if (null == pageNo || pageNo <= 0) {
                pageNo = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = Page.PAGE_SIZE_DEFAULT;
            }
            params.put("start", (pageNo - 1) * pageSize);
            params.put("size", pageSize);
            PageInfo pageInfo = noticeService.queryArticleList(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error(String.format("公告文章列表查询失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 查询公告文章详细
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryArticleDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryArticleDetail(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "公告文章详情查询成功!");
        try {
            Integer id = (Integer) params.get("id");
            Preconditions.checkArgument(null != id, "id不能为null");
            Map<String, Object> detail = noticeService.queryArticleDetail(id);
            if (null == detail) {
                detail = new HashMap<>();
            }
            result.put("data", detail);
        } catch (Exception e) {
            logger.error(String.format("公告文章详情查询失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
