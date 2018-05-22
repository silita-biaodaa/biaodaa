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
import com.silita.biaodaa.utils.RouteUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.RedisConstantInterface.*;
import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHAOBIAO;
import static com.silita.biaodaa.utils.RouteUtils.HUNAN_SOURCE;
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

    private Map accessFilter(Map params){
        Map resultMap = new HashMap();
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
        }else if(pageSize>maxPageSize || pageNo>maxPageNum) {
            resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
            resultMap.put(this.MSG_FLAG, "超出访问范围！");
        }
        return resultMap;
    }

    private  void parseRouteSource(Map params){
        String province =  MapUtils.getString(params,"province");
        params.put("source",RouteUtils.parseProvinceToSource(province));
        settingRouteTable(params);
    }

    private void settingRouteTable(Map params){
        String source =  MapUtils.getString(params,"source");
        if(MyStringUtils.isNotNull(source)) {
            params.put("snatchurl_tbn", RouteUtils.routeTableName("mishu.snatchurl", source));
            params.put("snatchurlcontent_tbn", RouteUtils.routeTableName("mishu.snatchurlcontent", source));
            params.put("snatchpress_tbn", RouteUtils.routeTableName("mishu.snatchpress", source));
        }
    }

    private void parseViewParams(Map params){
        //报名地址条件判断
        String bmSite =  MapUtils.getString(params,"bmSite");
        if(MyStringUtils.isNotNull(bmSite)) {
            switch (bmSite) {
                case "0"://全部
                    break;
                case "1"://网上报名
                    params.put("bmSiteStr", "%网上%");
                    break;
                case "2"://现场报名
                    params.put("bmSiteStr", "线下");
                    break;
                default:params.put("bmSiteStr", "%" + bmSite + "%");
            }
        }

        //地区条件获取
        String dqsStr =  MapUtils.getString(params,"regions");
        String[] dqsStrList =MyStringUtils.splitParam(dqsStr);
        if(dqsStrList!=null && dqsStrList.length>0){
            if(dqsStrList.length==1){
                params.put("province",dqsStrList[0]);
            }else if(dqsStrList.length==2){
                params.put("province",dqsStrList[0]);
                if(MyStringUtils.isNotNull(dqsStrList[1])) {
                    params.put("city", dqsStrList[1].replace("市",""));
                }
            }
        }

        parseRouteSource(params);//从地区解析出source，设置表名

        String pbModes = MapUtils.getString(params,"pbModes");
        String[] pbModesList =MyStringUtils.splitParam(pbModes);
        if(pbModesList!=null && pbModesList.length>0){
            StringBuffer modeStr = new StringBuffer();
            for(String mode: pbModesList){
                modeStr.append("'"+mode+"',");
            }
            modeStr.deleteCharAt(modeStr.length()-1);
            params.put("modeStr",modeStr.toString());
        }

        String zztype = MapUtils.getString(params,"zzType");
        String[] zztypeList =MyStringUtils.splitParam(zztype);
        if(zztypeList!=null && zztypeList.length>0){
            if(zztypeList.length==1){
                params.put("zzTypeOne",zztypeList[0]);
            }else if(zztypeList.length==2){
                params.put("zzTypeTwo",zztypeList[1]);
            }else if(zztypeList.length==3){
                params.put("zzTypeThree",zztypeList[2]);
            }
        }

        if(MyStringUtils.isNotNull(params.get("projSumStart"))){
            params.put("projSumStart",Integer.parseInt(params.get("projSumStart").toString()));
        }
        if(MyStringUtils.isNotNull(params.get("projSumEnd"))){
            params.put("projSumEnd",Integer.parseInt(params.get("projSumEnd").toString()));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/searchList",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> searchList(@RequestBody Map params){
        Map resultMap =null;
        try {
//        search.setTypeToInt(type);
//        encodingConvert(search);
            settingUserId(params);

            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            resultMap=accessFilter(params);
           if(resultMap.size()>0){
               return resultMap;
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
                    parseViewParams(params);
                    pageInfo = noticeService.searchNoticeList(page, params);
                    if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                        myRedisTemplate.setObject(listKey, pageInfo, LIST_OVER_TIME);
                    }
                }
                settingNoticeCollFlag(pageInfo, params);
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
        Map resultMap = null;
        try {
            settingUserId(params);
//        search.setTypeToInt(type);
//        encodingConvert(search);
            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            resultMap=accessFilter(params);
            if(resultMap.size()>0){
                return resultMap;
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
                    parseViewParams(params);
                    pageInfo = noticeService.queryNoticeList(page, params);
                    if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                        myRedisTemplate.setObject(listKey, pageInfo, LIST_OVER_TIME);
                    }
                }
                settingNoticeCollFlag(pageInfo, params);
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

    private void settingNoticeCollFlag(PageInfo pageInfo,Map params){
        if(pageInfo!=null && pageInfo.getList()!=null) {
            addNoticeCollStatus(pageInfo.getList(), params);
        }
    }

    private void settingCompanyCollFlag(PageInfo pageInfo,Map params){
        if(pageInfo!=null && pageInfo.getList()!=null) {
            addCompanyCollStatus(pageInfo.getList(), params);
        }
    }

    /**
     * 补充公告信息的关注状态
     * @param list
     * @param params
     */
    private void addNoticeCollStatus(List<Map> list, Map params){
        params.put("idKey","id");
        params.put("collType","notice");
        noticeService.addCollStatus(list, params);
    }

    /**
     * 补充企业信息的关注状态
     * @param list
     * @param params
     */
    private void addCompanyCollStatus(List list, Map params){
        params.put("collType","company");
        noticeService.addCollStatus(list, params);
    }


    /**
     * 根据source，转换出省名称
     * @param params
     */
    private void settingProvName(Map params){
        String source =  MapUtils.getString(params,"source");
        params.put("provName",RouteUtils.parseSourceToProv(source));
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
                settingRouteTable(params);
                settingProvName(params);
                detailList = noticeService.queryNoticeDetail(params);
                if(detailList!=null && detailList.size()>0){
                    myRedisTemplate.setObject(listKey,detailList, DETAIL_OVER_TIME);
                }
            }

            String source =  MapUtils.getString(params,"source");
            if(MyStringUtils.isNull(source) || source.equals(HUNAN_SOURCE)) {
                addNoticeCollStatus(detailList, params);
            }else{
                //todo:全国公告暂不支持关注状态，统一返回false
                noticeService.addCollStatusByRoute(detailList);
            }
            resultMap.put(DATA_FLAG,detailList);
            //添加点击次数
            resultMap.put("clickCount", noticeService.getClickCountBySourceAndTypeAndInnertId(params));

            if(MyStringUtils.isNull(source) || source.equals(HUNAN_SOURCE)) {
                Long relNoticeCount = noticeService.queryRelCount(id);
                resultMap.put("relNoticeCount", relNoticeCount);//相关公告数量
                //招标详情
                String type = MapUtils.getString(params, "type");
                if (type.equals(SNATCHURL_ZHAOBIAO)) {
                    Integer relCompanySize = noticeService.queryCompanyCountById(params);
                    List<Map> fileList = noticeService.queryNoticeFile(id);
                    int fileSize = 0;
                    if (fileList != null && fileList.size() > 0) {
                        fileSize = fileList.size();
                    }
                    resultMap.put("relCompanySize", relCompanySize);//资质相关企业
                    resultMap.put("fileCount", fileSize);//文件列表
                }

            }else{
                //todo:全国公告暂不支持
                resultMap.put("relNoticeCount", 0);//相关公告数量
                resultMap.put("relCompanySize", 0);//资质相关企业
                resultMap.put("fileCount", 0);//文件列表
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
            String cacheKey = ObjectUtils.buildCacheKey(GG_REL_LIST,argMap);
            List<Map> relationNotices = (List<Map>) myRedisTemplate.getObject(cacheKey);
            if(relationNotices ==null) {
                relationNotices = noticeService.queryRelations(argMap);
                if(relationNotices!=null && relationNotices.size()>0){
                    myRedisTemplate.setObject(cacheKey,relationNotices,LIST_OVER_TIME);
                }
            }
            addNoticeCollStatus(relationNotices, argMap);
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
            String cacheKey = ObjectUtils.buildCacheKey(GG_REL_COM_LIST,params);
            PageInfo pageInfo =  (PageInfo) myRedisTemplate.getObject(cacheKey);
            if(pageInfo ==null) {
                pageInfo = noticeService.queryCompanyListById(page, params);
                if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                    myRedisTemplate.setObject(cacheKey,pageInfo,LIST_OVER_TIME);
                }
            }
            settingCompanyCollFlag(pageInfo,params);
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
            Object idObject = (Object) params.get("id");
            Integer id = null;
            if (idObject instanceof Integer) {
                id = (Integer) idObject;
            } else if (idObject instanceof String) {
                id = Integer.parseInt((String) idObject);
            }
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
