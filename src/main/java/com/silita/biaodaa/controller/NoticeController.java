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
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.silita.biaodaa.common.RedisConstantInterface.*;
import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHAOBIAO;
import static com.silita.biaodaa.utils.RouteUtils.HUNAN_SOURCE;
import static org.apache.commons.collections.MapUtils.getString;

/**
 * 公告
 * Created by dh on 2018/4/9.
 */
@RequestMapping("/notice")
@Controller
public class NoticeController extends BaseController {

    private Log logger = LogFactory.getLog(NoticeController.class);

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CommonService commonService;

    // TODO: 设置userid
    private void settingUserId(Map params) {
        String userId = VisitInfoHolder.getUid();
        if (MyStringUtils.isNotNull(userId)) {
            params.put("userId", userId);
        }
    }

    /**
     * TODO: 过滤查询条件，不满足条件的直接返回
     *
     * @param params
     * @return
     */
    private Map accessFilter(Map params) {
        Map resultMap = new HashMap();
        Page page = buildPage(params);
        Integer pageNo = page.getCurrentPage();
        Integer pageSize = page.getPageSize();
        String kbDateStart = MapUtils.getString(params, "kbDateStart");
        String kbDateEnd = MapUtils.getString(params, "kbDateEnd");
        if (MyStringUtils.isNotNull(kbDateStart)
                && MyDateUtils.getDistanceOfTwoDate(kbDateStart, this.minDate) > 0) {
            resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
            resultMap.put(this.MSG_FLAG, "开标开始时间超出访问范围！");
        } else if (MyStringUtils.isNotNull(kbDateEnd)
                && MyDateUtils.getDistanceOfTwoDate(kbDateEnd, this.minDate) > 0) {
            resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
            resultMap.put(this.MSG_FLAG, "开标结束时间超出访问范围！");
        } else if (pageSize > maxPageSize || pageNo > maxPageNum) {
            resultMap.put(this.CODE_FLAG, INVALIDATE_PARAM_CODE);
            resultMap.put(this.MSG_FLAG, "超出访问范围！");
        }
        return resultMap;
    }

    // TODO: 设置source
    private void parseRouteSource(Map params) {
        String province = MapUtils.getString(params, "province");
        params.put("source", RouteUtils.parseProvinceToSource(province));
        settingRouteTable(params);
    }

    //TODO: 设置表名
    private void settingRouteTable(Map params) {
        String source = MapUtils.getString(params, "source");
        if (MyStringUtils.isNotNull(source)) {
            params.put("snatchurl_tbn", RouteUtils.routeTableName("mishu.snatchurl", source));
            params.put("snatchurlcontent_tbn", RouteUtils.routeTableName("mishu.snatchurlcontent", source));
            params.put("snatchpress_tbn", RouteUtils.routeTableName("mishu.snatchpress", source));
        }
    }

    /**
     * TODO: 解析/转换前端参数，返回是否执行
     *
     * @param params
     * @return
     */
    private boolean parseViewParams(Map params) {
        int defaultDay = 100;//查询公告的范围：默认为前100天
        String type = MapUtils.getString(params, "type");
        //1.地区条件获取
        String dqsStr = MapUtils.getString(params, "regions");
        if (MyStringUtils.isNotNull(dqsStr)) {
            String[] dqsStrList = MyStringUtils.splitParam(dqsStr);
            if (dqsStrList != null && dqsStrList.length > 0) {
                String prov = dqsStrList[0].substring(0, 2);
                if (dqsStrList.length == 1) {
                    params.put("province", prov);
                } else if (dqsStrList.length == 2) {
                    params.put("province", prov);
                    if (MyStringUtils.isNotNull(dqsStrList[1])) {
                        params.put("city", Arrays.asList(dqsStrList[1].replaceAll("市", "").split(",")));
                    }
                } else if (dqsStrList.length == 3) {
                    params.put("province", prov);
                    if (MyStringUtils.isNotNull(dqsStrList[1])) {
                        params.put("city", Arrays.asList(dqsStrList[1].replaceAll("市", "").split(",")));
                    }
                    if (MyStringUtils.isNotNull(dqsStrList[2])) {
                        params.put("county", dqsStrList[2]);
                    }
                }
            }
        }
        //2.全国公告路由：从地区解析出source，设置表名，必须在地区字段之后
        parseRouteSource(params);

        //TODO:全国公告关联到维度信息之后，此处需要放开
        //全国公告暂无维度信息，涉及维度筛选条件时，直接返回空
//        if (!countryNoticeFilter(params)) {
//            return false;
//        }

        //3.招、中标条件区分判断
        if (SnatchContent.SNATCHURL_ZHAOBIAO.equals(type)) {//招标
            //企业名称匹配的资质
            String comName = MapUtils.getString(params, "com_name");
            if (MyStringUtils.isNotNull(comName)) {
                List<String> aptList = queryComAptitudeByName(comName);
                if (MyStringUtils.isNotNull(aptList)) {
                    //企业资质合并进入筛选队列
                    Set threeAptSet = new HashSet(aptList);
                    params.put("comThreeAptList", new ArrayList(threeAptSet));
                } else {
                    //企业无匹配资质，返回空
                    return false;
                }
            }

            //报名地址条件判断
            String bmSite = MapUtils.getString(params, "bmSite");
            if (MyStringUtils.isNotNull(bmSite)) {
                switch (bmSite) {
                    case "0"://全部
                        break;
                    case "1"://网上报名
                        params.put("bmSiteStr", "%网上%");
                        break;
                    case "2"://现场报名
                        params.put("bmSiteStr", "线下");
                        break;
                    default:
                        params.put("bmSiteStr", "%" + bmSite + "%");
                }
            }

            //资质条件处理，支持多选(用半角逗号分隔)
            String zztype = MapUtils.getString(params, "zzType");
            if (MyStringUtils.isNotNull(zztype)) {
                parseZzType(params, ",");
            }
        } else if (SnatchContent.SNATCHURL_ZHONGBIAO.equals(type)) {//中标
            //第一中标候选人
            String comName = MapUtils.getString(params, "com_name");
            if (MyStringUtils.isNotNull(comName)) {
                params.put("oneName", comName);
                defaultDay = 400;//中标查询候选人条件时，公告范围扩展到400天（约一年）
            }
        }

        //4.其他公共条件判断
        //评标办法
        String pbModes = MapUtils.getString(params, "pbModes");
        String[] pbModesList = MyStringUtils.splitParam(pbModes);
        if (pbModesList != null && pbModesList.length > 0) {
//            StringBuffer modeStr = new StringBuffer();
//            for(String mode: pbModesList){
//                modeStr.append("'"+mode+"',");
//            }
//            modeStr.deleteCharAt(modeStr.length()-1);
//            params.put("modeStr",modeStr.toString());
            params.put("modeStr", Arrays.asList(pbModesList));
        }
        if (MyStringUtils.isNotNull(params.get("projSumStart"))) {
            params.put("projSumStart", Integer.parseInt(params.get("projSumStart").toString()));
        }
        if (MyStringUtils.isNotNull(params.get("projSumEnd"))) {
            params.put("projSumEnd", Integer.parseInt(params.get("projSumEnd").toString()));
        }
        //设置公告查询的范围，多少天内的公告
        params.put("queryDays", defaultDay);
        return true;
    }

    /**
     * TODO: 全国公告，筛选条件判断过滤
     *
     * @param params
     * @return
     */
    private boolean countryNoticeFilter(Map params) {
        boolean flag = true;
        String source = MapUtils.getString(params, "source");
        String pbModes = MapUtils.getString(params, "pbModes");
        String kbDateStart = MapUtils.getString(params, "kbDateStart");
        String projSumStart = MapUtils.getString(params, "projSumStart");
        String projSumEnd = MapUtils.getString(params, "projSumEnd");
        String kbDateEnd = MapUtils.getString(params, "kbDateEnd");
        String zzType = MapUtils.getString(params, "zzType");
        String bmSite = MapUtils.getString(params, "bmSite");
        String com_name = MapUtils.getString(params, "com_name");
        if (MyStringUtils.isNotNull(source) && !source.equals(HUNAN_SOURCE)) {//非湖南公告
            if (MyStringUtils.isNotNull(pbModes)
                    || MyStringUtils.isNotNull(kbDateStart)
                    || MyStringUtils.isNotNull(projSumStart)
                    || MyStringUtils.isNotNull(projSumEnd)
                    || MyStringUtils.isNotNull(kbDateEnd)
                    || MyStringUtils.isNotNull(zzType)
                    || MyStringUtils.isNotNull(bmSite)
                    || MyStringUtils.isNotNull(com_name)) {
                flag = false;
            }
        }

        return flag;
    }

    /**
     * TODO: 解析资质多选条件
     *
     * @param params
     * @param zzSplit
     */
    private void parseZzType(Map params, String zzSplit) {
        String zztype = MapUtils.getString(params, "zzType");
        String[] multiType = zztype.split(zzSplit);
        List<Map> aptMapList = new ArrayList<Map>();
        List<String> zzTypeOneList = new ArrayList<String>();
        for (String zType : multiType) {
            Map zzTypeMap = new HashMap();
            String[] zztypeList = MyStringUtils.splitParam(zType);
            Map<String, List> threeAptMap = null;
            if (zztypeList != null && zztypeList.length > 0) {
                if (zztypeList.length == 1) {
                    zzTypeOneList.add(zztypeList[0]);
                } else if (zztypeList.length == 2) {
                    zzTypeMap.put("zzTypeTwo", zztypeList[1]);
                } else if (zztypeList.length == 3) {
                    threeAptMap = AptitudeUtils.parseThreeAptCode(zztypeList[2]);
                    zzTypeMap.put("threeAptList", threeAptMap.get("hasAptList"));
                    zzTypeMap.put("notThreeAptList", threeAptMap.get("notHasAptList"));
                }
                if (zzTypeMap.size() > 0) {
                    aptMapList.add(zzTypeMap);
                }
            }
        }
        params.put("zzTypeOneList", zzTypeOneList);
        if (aptMapList.size() > 0) {
            params.put("aptMapList", aptMapList);
        }
    }

    /**
     * TODO: 查询企业具备的资质（同名企业，资质一并返回）
     *
     * @param
     * @return
     */
    private List<String> queryComAptitudeByName(String comName) {
        List<String> aptList = null;
        if (MyStringUtils.isNotNull(comName)) {
            Map argMap = new HashMap();
            argMap.put("com_name", comName);

            int paramHash = ObjectUtils.buildMapParamHash(argMap);
            String listKey = RedisConstantInterface.COM_NAME_APTITUDE + paramHash;
            List<Map> resList = (List<Map>) myRedisTemplate.getObject(listKey);
            if (resList == null) {
                resList = this.noticeService.queryComAptitudeByName(argMap);
                if (resList != null) {
                    myRedisTemplate.setObject(listKey, resList, COM_OVER_TIME);
                }
            }
            if (MyStringUtils.isNotNull(resList)) {
                aptList = new ArrayList<String>();
                String range = null;
                String tmp = null;
                for (Map res : resList) {
                    range = (String) res.get("range");
                    if (MyStringUtils.isNotNull(range)) {
                        String[] apts = range.split(",");
                        for (String apt : apts) {
                            tmp = apt.substring(apt.indexOf("/") + 1);//三级资质
                            if (MyStringUtils.isNotNull(tmp)) {
                                aptList.add(tmp);
                            }
                        }
                    }
                }
            }
        }
        return aptList;
    }

    /**
     * TODO: 性能问题，禁止查询所有类别公告。
     *
     * @param params
     */
    private void forbidAllType(Map params) {
        Integer type = MapUtils.getInteger(params, "type");
        if (MyStringUtils.isNull(type) || type == 99) {
            params.put("type", 0);
        }

    }

    /**
     * TODO: 查询公告
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/searchList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> searchList(@RequestBody Map params) {
        forbidAllType(params);//性能问题，暂禁止查询所有type公告
        Map resultMap = null;
        try {
//        search.setTypeToInt(type);
//        encodingConvert(search);
            settingUserId(params);

            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            resultMap = accessFilter(params);
            if (resultMap.size() > 0) {
                return resultMap;
            } else {
                if (pageNo >= maxPageNum) {
                    resultMap.put("isLastPage", true);
                } else {
                    resultMap.put("isLastPage", false);
                }

                int paramHash = ObjectUtils.buildMapParamHash(params);
                String listKey = RedisConstantInterface.GG_SEARCH_LIST + paramHash;
                PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
                if (pageInfo == null) {
                    boolean isExecute = parseViewParams(params);
                    if (isExecute) {
                        pageInfo = noticeService.searchNoticeList(page, params);
                    } else {
                        pageInfo = new PageInfo(new ArrayList());
                    }
                    if (pageInfo != null && pageInfo.getList() != null) {
                        myRedisTemplate.setObject(listKey, pageInfo, LIST_OVER_TIME);
                    }
                }
                settingNoticeCollFlag(pageInfo, params);
                buildReturnMap(resultMap, pageInfo);
                successMsg(resultMap);
            }
            //        recordAccessCount(request,params); 记录访问数
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }

    /**
     * TODO: 查询公告
     *
     * @param params
     * @return
     */
//    @RequestParam json对象参数
    @ResponseBody
    @RequestMapping(value = "/queryList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryList(@RequestBody Map params) {
        Map resultMap = null;
        forbidAllType(params);//性能问题，暂禁止查询所有type公告
        try {
//        search.setTypeToInt(type);
//        encodingConvert(search);
            Page page = buildPage(params);
            Integer pageNo = page.getCurrentPage();
            resultMap = accessFilter(params);
            if (resultMap.size() > 0) {
                return resultMap;
            } else {
                if (pageNo >= maxPageNum) {
                    resultMap.put("isLastPage", true);
                } else {
                    resultMap.put("isLastPage", false);
                }
                int paramHash = ObjectUtils.buildMapParamHash(params);
                String listKey = RedisConstantInterface.GG_LIST + paramHash;
                logger.info("公告列表key:" + listKey + "--------------------");
                PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
                settingUserId(params);
                if (pageInfo == null) {
                    boolean isExecute = parseViewParams(params);
                    if (isExecute) {
                        pageInfo = noticeService.queryNoticeList(page, params);
                    } else {
                        pageInfo = new PageInfo(new ArrayList());
                    }
                    if (pageInfo != null && pageInfo.getList() != null) {
                        myRedisTemplate.setObject(listKey, pageInfo, LIST_OVER_TIME);
                    }
                }
//                settingNoticeCollFlag(pageInfo, params);
                buildReturnMap(resultMap, pageInfo);
                successMsg(resultMap);
            }
            //        recordAccessCount(request,params); 记录访问数
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }

    //TODO: 设置公告关注状态
    private void settingNoticeCollFlag(PageInfo pageInfo, Map params) {
        if (pageInfo != null && pageInfo.getList() != null) {
            addNoticeCollStatus(pageInfo.getList(), params);
        }
    }

    //TODO: 设置公司关注
    private void settingCompanyCollFlag(PageInfo pageInfo, Map params) {
        if (pageInfo != null && pageInfo.getList() != null) {
            addCompanyCollStatus(pageInfo.getList(), params);
        }
    }

    /**
     * TODO: 补充公告信息的关注状态
     *
     * @param list
     * @param params
     */
    private void addNoticeCollStatus(List<Map> list, Map params) {
        params.put("idKey", "id");
        params.put("collType", "notice");
        params.put("source",params.get("source"));
        noticeService.addCollStatus(list, params);
    }

    /**
     * TODO: 补充企业信息的关注状态
     *
     * @param list
     * @param params
     */
    private void addCompanyCollStatus(List list, Map params) {
        params.put("collType", "company");
        noticeService.addCollStatus(list, params);
    }


    /**
     * TODO: 根据source，转换出省名称
     *
     * @param params
     */
    private void settingProvName(Map params) {
        String source = MapUtils.getString(params, "source");
        params.put("provName", RouteUtils.parseSourceToProv(source));
    }

    /**
     * TODO: 公告详情
     *
     * @param id
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryDetail(@PathVariable Long id, @RequestBody Map params) {
        Map resultMap = new HashMap();
        try {
            params.put("id", id);
            if (null == params.get("source")) {
                params.put("source", HUNAN_SOURCE);
            }
            int paramHash = ObjectUtils.buildMapParamHash(params);
            String listKey = RedisConstantInterface.GG_DETAIL + paramHash;
            List<Map> detailList = (List<Map>) myRedisTemplate.getObject(listKey);
            settingUserId(params);
            if (detailList == null) {
                settingRouteTable(params);
                settingProvName(params);
                detailList = noticeService.queryNoticeDetail(params);
                if (detailList != null && detailList.size() > 0) {
                    myRedisTemplate.setObject(listKey, detailList, DETAIL_OVER_TIME);
                }
            }

            addNoticeCollStatus(detailList, params);
            resultMap.put(DATA_FLAG, detailList);
            //添加点击次数
            resultMap.put("clickCount", noticeService.getClickCountBySourceAndTypeAndInnertId(params));

            Long relNoticeCount = noticeService.queryRelCountParam(params);

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
            successMsg(resultMap);
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }

    /**
     * TODO: 关联列表
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryRelNotice/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryRelNotice(@PathVariable Long id) {
        Map resultMap = new HashMap();
        try {
            Map argMap = new HashMap();
            argMap.put("id", id);
            String cacheKey = ObjectUtils.buildCacheKey(GG_REL_LIST, argMap);
            settingUserId(argMap);
            List<Map> relationNotices = (List<Map>) myRedisTemplate.getObject(cacheKey);
            if (relationNotices == null) {
                relationNotices = noticeService.queryRelations(argMap);
                if (relationNotices != null && relationNotices.size() > 0) {
                    myRedisTemplate.setObject(cacheKey, relationNotices, LIST_OVER_TIME);
                }
            }
            addNoticeCollStatus(relationNotices, argMap);
            resultMap.put(DATA_FLAG, relationNotices);
            successMsg(resultMap);
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }

    /**
     * TODO: 公告文件
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryNoticeFile/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryNoticeFile(@PathVariable Long id) {
        Map resultMap = new HashMap();
        try {
            List<Map> fileList = noticeService.queryNoticeFile(id);
            resultMap.put(DATA_FLAG, fileList);
            successMsg(resultMap);
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }


    /**
     * TODO: 公告企业
     *
     * @param id
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCompanyList/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> queryCompanyList(@PathVariable Long id, @RequestBody Map params) {
        Map resultMap = new HashMap();
        try {
            settingUserId(params);
            params.put("id", id);
            Page page = buildPage(params);
            String cacheKey = ObjectUtils.buildCacheKey(GG_REL_COM_LIST, params);
            PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(cacheKey);
            if (pageInfo == null) {
                pageInfo = noticeService.queryCompanyListById(page, params);
                if (pageInfo != null && pageInfo.getList() != null && pageInfo.getList().size() > 0) {
                    myRedisTemplate.setObject(cacheKey, pageInfo, LIST_OVER_TIME);
                }
            }
            settingCompanyCollFlag(pageInfo, params);
            buildReturnMap(resultMap, pageInfo);
            successMsg(resultMap);
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(resultMap, e.getMessage());
        }
        return resultMap;
    }

    /**
     * TODO: 记录访问数
     *
     * @param request
     * @param params
     */
    private void recordAccessCount(HttpServletRequest request, Map params) {
        String type = getString(params, "type");
        Map<String, String> preMap = parseRequest(request);
        String userId = preMap.get("userId");
        String ipAddr = preMap.get("ipAddr");
        params.put("userId", userId);
        params.put("ipAddr", ipAddr);
        //记录访问数
        if (userId != null) {
            if (SnatchContent.ZHONG_BIAO_TYPE.equals(type)) {//中标
                commonService.insertRecordLog("zhongbiao", userId, ipAddr);
            } else {
                commonService.insertRecordLog("zhaobiaoxinxi", userId, ipAddr);
            }
        }
    }

    //TODO: 解析http信息
    private Map<String, String> parseRequest(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute("userId");
        String ipAddr = request.getHeader("X-real-ip");//获取用户真实ip
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("ipAddr", ipAddr);
        return map;
    }

    /**
     * TODO: 查询公告文章列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryArticleList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
     * TODO: 查询公告文章详细
     *
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
