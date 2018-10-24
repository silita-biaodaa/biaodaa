package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.ArticlesMapper;
import com.silita.biaodaa.dao.NoticeMapper;
import com.silita.biaodaa.dao.TbClickStatisticsMapper;
import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.model.TbClickStatistics;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHAOBIAO;
import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHONGBIAO;
import static com.silita.biaodaa.utils.RouteUtils.HUNAN_SOURCE;
import static org.apache.commons.collections.MapUtils.getString;

/**
 * Created by dh on 2018/4/9.
 */
@Service
public class NoticeService {

    private Log logger = LogFactory.getLog(NoticeService.class);

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private ArticlesMapper articlesMapper;
    @Autowired
    private TbClickStatisticsMapper tbClickStatisticsMapper;
    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    /**
     * 资质id拼接
     *
     * @param uuid
     * @param rank
     * @return
     */
    public static String spellUuid(String uuid, String rank) {
        String str = "";
        //特级
        if ("0".equals(rank)) {
            str += uuid + "/0";
        }
        //一级
        else if ("1".equals(rank)) {
            str += uuid + "/1";
        }
        //二级
        else if ("2".equals(rank)) {
            str += uuid + "/2";
        }
        //三级
        else if ("3".equals(rank)) {
            str += uuid + "/3";
        }
        //一级及以上
        else if ("u1".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1";
        }
        //二级及以上
        else if ("u2".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1" + "," + uuid + "/2";
        }
        //三级及以上
        else if ("u3".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1" + "," + uuid + "/2" + "," + uuid + "/3";
        }
        //特级及以下
        else if ("d0".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1" + "," + uuid + "/2" + "," + uuid + "/3";
        }
        //一级及以下
        else if ("d1".equals(rank)) {
            str += uuid + "/1" + "," + uuid + "/2" + "," + uuid + "/3";
        }
        //二级及以下
        else if ("d2".equals(rank)) {
            str += uuid + "/2" + "," + uuid + "/3";
        }
        //一级及以上
        else if ("11".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1";
        }
        //二级及以上
        else if ("21".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1" + "," + uuid + "/2";
        }
        //三级及以上
        else if ("31".equals(rank)) {
            str += uuid + "/0" + "," + uuid + "/1" + "," + uuid + "/2" + "," + uuid + "/3";
        }
        //无等级
        else if ("no".equals(rank)) {
            str += uuid + "/";
        }

        //甲级
        else if ("-1".equals(rank)) {
            str += uuid + "/-1";
        }
        //乙级
        else if ("-2".equals(rank)) {
            str += uuid + "/-2";
        }
        //丙级
        else if ("-3".equals(rank)) {
            str += uuid + "/-3";
        }
        //乙级及以上
        else if ("-21".equals(rank)) {
            str += uuid + "/-1" + "," + uuid + "/-2";
        }
        //丙级及以上
        else if ("-31".equals(rank)) {
            str += uuid + "/-1" + "," + uuid + "/-2" + "," + uuid + "/-3";
        }
        //乙级及以上
        else if ("u-2".equals(rank)) {
            str += uuid + "/-1" + "," + uuid + "/-2";
        }
        //丙级及以上
        else if ("u-3".equals(rank)) {
            str += uuid + "/-1" + "," + uuid + "/-2" + "," + uuid + "/-3";
        }
        return str;
    }


    public PageInfo queryNoticeList(Page page, Map params) throws Exception {
        buildNoticeDetilTable(params);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map> list = noticeMapper.queryNoticeList(params);
        sortingResult(list);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * (非湖南省的公告)获取结果集中的关注状态
     *
     * @param list
     */
    public void addCollStatusByRoute(List list) {
        String userId = VisitInfoHolder.getUid();
        if (MyStringUtils.isNull(userId)) {
            return;
        }
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                Map map = (Map) obj;
                map.put("collected", false);
            }
        }
    }

    /**
     * 单独获取结果集中的关注状态
     *
     * @param list
     * @param params
     */
    public void addCollStatus(List list, Map params) {
        String userId = VisitInfoHolder.getUid();
        if (MyStringUtils.isNull(userId)) {
            return;
        }
        if (list != null && list.size() > 0) {
            Set idSet = new HashSet();
            if (list.get(0) instanceof TbCompany) {
                for (Object obj : list) {
                    idSet.add(((TbCompany) obj).getComId());
                }
            } else {
                for (Object obj : list) {
                    Map map = (Map) obj;
                    idSet.add(map.get(params.get("idKey")));
                }
            }

            Map argMap = new HashMap();
            argMap.put("userId", userId);
            argMap.put("idSet", idSet);
            List collIds = null;
            if ("notice".equals(params.get("collType"))) {
                collIds = this.queryNoticeCollStatus(argMap);
            } else if ("company".equals(params.get("collType"))) {
                collIds = this.queryCompanyCollStatus(argMap);
            }

            if (list.get(0) instanceof TbCompany) {//企业信息
                if (collIds != null && collIds.size() > 0) {
                    for (Object obj : list) {
                        TbCompany comObj = ((TbCompany) obj);
                        if (collIds.contains(comObj.getComId())) {
                            comObj.setCollected(true);
                        } else {
                            comObj.setCollected(false);
                        }
                    }
                }
            } else {//公告信息
                if (collIds != null && collIds.size() > 0) {
                    for (Object obj : list) {
                        Map map = (Map) obj;
                        if (collIds.contains(Long.parseLong(map.get(params.get("idKey")).toString()))) {
                            map.put("collected", true);
                        } else {
                            map.put("collected", false);
                        }
                    }
                } else {
                    for (Object obj : list) {
                        Map map = (Map) obj;
                        map.put("collected", false);
                    }
                }
            }
        }
    }

    /**
     * 根据公告ID查询资质，然后根据资质查询企业列表
     *
     * @param argMap
     * @return
     */
    public PageInfo queryCompanyListById(Page page, Map argMap) {
        String zzRes = noticeMapper.queryNoticeZZById(argMap);
        //获取公告省份
        String scource = getString(argMap, "source");
        argMap.put("area", RouteUtils.parseSourceToProv(scource));

        if (zzRes != null && zzRes.trim().length() > 1) {
            String[] zzStr = zzRes.split(",");
            if (zzStr != null && zzStr.length > 0) {
                Set<String> zzSet = new HashSet<String>();
                zzSet.addAll(Arrays.asList(zzStr));
                if (zzSet.size() > 0) {
                    List zzList = new ArrayList(zzSet.size());
                    zzList.addAll(zzSet);
                    argMap.put("zzList", zzList);
                    PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
                    List<TbCompany> list = noticeMapper.queryComInfoByZZ(argMap);
                    if (null == list || list.size() <= 0) {
                        PageInfo pageInfo = new PageInfo(list);
                        return pageInfo;
                    }
                    Map idsMap = new HashMap();
                    idsMap.put("comList", list);
                    List<TbCompany> companyCertList = noticeMapper.selectCompanyCert(idsMap);
                    List<TbCompany> companyCertBasicList = noticeMapper.selectCompanyCertBasic(idsMap);
                    TbCompanyInfo tbCompanyInfo = null;
                    String tabCode = null;
                    for (TbCompany company : list) {
                        tabCode = CommonUtil.getCode(company.getRegisAddress());
                        tbCompanyInfo = tbCompanyInfoMapper.queryDetailByComName(company.getComName(), tabCode);
                        if (null != tbCompanyInfo) {
                            if (null != tbCompanyInfo.getPhone()) {
                                company.setPhone(tbCompanyService.solPhone(tbCompanyInfo.getPhone().trim(), "replace").toLowerCase());
                            }
                            if (null == company.getRegisCapital() && null != tbCompanyInfo.getRegisCapital()) {
                                company.setRegisCapital(tbCompanyInfo.getRegisCapital());
                            }
                        }
                        String comid = company.getComId();
                        for (TbCompany cert : companyCertList) {
                            if (cert.getComId().equals(comid)) {
                                company.setCertNo(cert.getCertNo());
                                company.setCertDate(cert.getCertDate());
                                company.setValidDate(cert.getValidDate());
                                companyCertList.remove(cert);
                                break;
                            }
                        }
                        for (TbCompany certBase : companyCertBasicList) {
                            if (certBase.getComId().equals(comid)) {
                                company.setComRange(certBase.getComRange());
                                company.setSubsist("存续");//根据要求统一修改
                                companyCertBasicList.remove(certBase);
                                break;
                            }
                        }
                    }
                    PageInfo pageInfo = new PageInfo(list);
                    return pageInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据ID查询资质ID列表（去重）
     *
     * @param argMap
     * @return
     */
    public List queryNoticeZZById(Map argMap) {
        String zzRes = noticeMapper.queryNoticeZZById(argMap);
        if (zzRes != null && zzRes.trim().length() > 1) {
            String[] zzStr = zzRes.split(",");
            if (zzStr != null && zzStr.length > 0) {
                Set<String> zzSet = new HashSet<String>();
                zzSet.addAll(Arrays.asList(zzStr));
                if (zzSet.size() > 0) {
                    List zzList = new ArrayList(zzSet.size());
                    zzList.addAll(zzSet);
                    return zzList;
                }
            }
        }
        return null;
    }

    /**
     * 根据公告id获取关联的企业列表数量
     *
     * @param argMap
     * @return
     */
    public Integer queryCompanyCountById(Map argMap) {
        List zzList = queryNoticeZZById(argMap);
        if (zzList != null && zzList.size() > 0) {
            String source = getString(argMap, "source");
            argMap.put("area", RouteUtils.parseSourceToProv(source));
            argMap.put("list", zzList);
            return noticeMapper.queryCompanyCountByZZ(argMap);
        }
        return null;
    }

    /**
     * 公告详情（多标段）
     *
     * @param params
     * @return
     */
    public List queryNoticeDetail(Map params) throws Exception {
        buildNoticeDetilTable(params);
        List<Map> resList = noticeMapper.queryNoticeDetail(params);
        String type = MapUtils.getString(params, "type");
        if (type.equals(SNATCHURL_ZHONGBIAO)) {
            CompanyLawEs companyLawEs;
            String key;
            if (null != resList && resList.size() > 0) {
                Map<String, Object> param = new HashMap<>();
                for (Map<String, Object> map : resList) {
                    if(null != map.get("oneName")){
                        param.put("comName", map.get("oneName"));
                        key =  RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                        companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
//                        companyLawEs = lawService.queryZhongbiaoCompanyLaw(param);
                        if(null != companyLawEs){
                            map.put("oneLaw", companyLawEs.getTotal());
                        }
                    }
                    if(null != map.get("twoName")){
                        param.put("comName", map.get("twoName"));
//                        companyLawEs = lawService.queryZhongbiaoCompanyLaw(param);
                        key =  RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                        companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
                        if(null != companyLawEs){
                            map.put("twoLaw", companyLawEs.getTotal());
                        }
                    }
                    if (null != map.get("threeName")){
                        param.put("comName", map.get("threeName"));
//                        companyLawEs = lawService.queryZhongbiaoCompanyLaw(param);
                        key =  RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                        companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
                        if(null != companyLawEs){
                            map.put("threeLaw", companyLawEs.getTotal());
                        }
                    }
                }
            }
        }
        if (resList != null && resList.size() > 1) {
            for (int i = 1; i < resList.size(); i++) {
                Map res = resList.get(i);
                res.remove("content");
            }
        }

        return resList;
    }

    private void buildNoticeDetilTable(Map params) throws Exception {
        String type = getString(params, "type");
        if (type == null) {
            throw new Exception("prameter type is null![type+" + type + "]");
        }
        if (type.equals(SNATCHURL_ZHAOBIAO)) {
            if("hunan".equals(params.get("source"))){
                params.put("detailTable", "zhaobiao_detail");
            }else {
                params.put("detailTable", "zhaobiao_detail_others");
            }
        } else if (type.equals(SNATCHURL_ZHONGBIAO)) {
            if("hunan".equals(params.get("source"))){
                params.put("detailTable", "zhongbiao_detail");
            }else {
                params.put("detailTable", "zhongbiao_detail_others");
            }
        }
    }

    /**
     * 查询相关公告数量
     *
     * @param id
     * @return
     */
    public Long queryRelCount(Long id) {
        return noticeMapper.queryRelCount(id);
    }

    /**
     * 查询相关公告数量
     *
     * @param param
     * @return
     */
    public Long queryRelCountParam(Map<String,Object> param) {
        return noticeMapper.queryRelCountByParam(param);
    }

    /**
     * 查询招标文件列表
     *
     * @param id
     * @return
     */
    public List<Map> queryNoticeFile(Long id) {
        return noticeMapper.queryNoticeFile(id);
    }

    /**
     * 招标，中标结果分拣
     */
    private void sortingResult(List<Map> relList) {
        Map<String,Object> param = new HashMap<>();
        CompanyLawEs companyLawEs;
        if (relList != null && relList.size() > 0) {
            //招标，中标结果划分
            for (Map result : relList) {
                String type = result.get("type").toString();
                if (type.equals(SNATCHURL_ZHAOBIAO)) {
                    if (result.get("zhaobiao_pbMode") != null) {
                        result.put("pbMode", result.get("zhaobiao_pbMode"));
                        result.remove("zhaobiao_pbMode");
                    }
                } else if (type.equals(SNATCHURL_ZHONGBIAO)) {
                    if (null != result.get("oneName")){
                        param.put("comName",result.get("oneName"));
//                        companyLawEs = lawService.queryZhongbiaoListCompanyLaw(param);
                        String key = RedisConstantInterface.NOTIC_LAW + ObjectUtils.buildMapParamHash(param);
                        companyLawEs = (CompanyLawEs) myRedisTemplate.getObject(key);
                        if (null != companyLawEs){
                            result.put("oneLaw",companyLawEs.getTotal());
                        }
                    }
                    if (result.get("zhongbiao_pbMode") != null) {
                        result.put("pbMode", result.get("zhongbiao_pbMode"));
                        result.remove("zhongbiao_pbMode");
                    }
                }
            }
        }
    }

    public PageInfo searchNoticeList(Page page, Map params) throws Exception {
        buildNoticeDetilTable(params);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map> list = noticeMapper.searchNoticeList(params);
        sortingResult(list);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public List<Map> queryRelations(Map map) {
        List<Map> relList = noticeMapper.queryRelations(map);
        sortingResult(relList);
        return relList;
    }

    /**
     * 查询公告文章列表
     *
     * @param page
     * @param params
     * @return
     */
    public PageInfo queryArticleList(Page page, Map params) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = articlesMapper.queryArticleList(params);
        if (null == list) {
            list = new ArrayList<>();
        } else {
            String content = "";
            for (Map<String, Object> map : list) {
                if (null != map.get("content")) {
                    content = map.get("content").toString();
//                    content.substring(content.lastIndexOf("&nbsp;"),20);
                    map.put("content", this.removeHtml(content).substring(0, 20) + ".....");

                }
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 根据id查询公告文章详细
     *
     * @param id
     * @return
     */
    public Map queryArticleDetail(Integer id) {
        return articlesMapper.queryArticleDetail(id);
    }

    public List<String> queryCompanyCollStatus(Map map) {
        return noticeMapper.queryCompanyCollStatus(map);
    }

    public List<Long> queryNoticeCollStatus(Map map) {
        return noticeMapper.queryNoticeCollStatus(map);
    }

    /**
     * 获取公告点击次数
     *
     * @param params
     * @return
     */
    public Integer getClickCountBySourceAndTypeAndInnertId(Map<String, Object> params) {
        TbClickStatistics tbClickStatistics = new TbClickStatistics();

        String source = String.valueOf(params.get("source"));
        if (StringUtils.isEmpty(source) || "hunan".equals(source) || "null".equals(source)) {
            tbClickStatistics.setSource(HUNAN_SOURCE);
        } else {
            tbClickStatistics.setSource(String.valueOf(params.get("source")));
        }
        tbClickStatistics.setType("content");
        tbClickStatistics.setInnertId(Math.toIntExact((Long) params.get("id")));

        //判断是否存在
        Integer curDateTotal = tbClickStatisticsMapper.getTotalBySourceAndTypeAndInnertId(tbClickStatistics);
        if (curDateTotal > 0) {
            tbClickStatisticsMapper.updateClickStatisticsBySourceAndTypeAndInnertId(tbClickStatistics);
        } else {
            tbClickStatisticsMapper.insertClickStatistics(tbClickStatistics);
        }
        return tbClickStatisticsMapper.getClickCountByBySourceAndTypeAndInnertId(tbClickStatistics);
    }

    /**
     * 去掉HTML标签
     * created by zhushuai
     *
     * @param content
     * @return
     */
    private String removeHtml(String content) {
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        String regEx_html = "<[^>]+>";
        //去掉style标签
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(content);
        content = m_style.replaceAll("");
        //去掉Html
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(content);
        content = m_html.replaceAll(""); //过滤html标签
        if (content.contains("&nbsp;")) {
            content = content.replaceAll("&nbsp;", "");
        }
        return content.trim();
    }

    /**
     * 根据企业名称查询资质
     *
     * @param argMap
     * @return
     */
    public List<Map> queryComAptitudeByName(Map argMap) {
        return this.noticeMapper.queryComAptitudeByName(argMap);
    }

    /**
     * 统计招标公告匹配的企业数据
     *
     * @param argMap
     * @param comType 0:湖南本地 1：入湘
     */
    public void bulidStatComCount(Map argMap, String comType) {
        String batchNum = MyDateUtils.getTime(null);
        String orgStartDay = MapUtils.getString(argMap, "startDay");
        String orgEndDay = MapUtils.getString(argMap, "endDay");
        int stepBy = 2;//步长（天数）
        int allDay = 1;
        String startDay = orgStartDay;
        String endDay = MyDateUtils.parseDateAddDay(orgStartDay, stepBy);
        int queryTotal = 0;
        int intervalDay = MyDateUtils.compareTime(endDay, orgEndDay);
        while (intervalDay > 0) {
            if (allDay > 1) {
                if (intervalDay > stepBy) {
                    startDay = MyDateUtils.parseDateAddDay(endDay, 1);
                    endDay = MyDateUtils.parseDateAddDay(startDay, stepBy);
                    allDay += 1;
                } else if (intervalDay <= stepBy) {
                    startDay = MyDateUtils.parseDateAddDay(endDay, 1);
                    endDay = orgEndDay;
                    allDay -= (stepBy - intervalDay);
                }
            }
            HashMap map = new HashMap(2);
            map.put("startDay", startDay);
            map.put("endDay", endDay);

            List<Map> matchComList = null;
            if (comType.equals("0")) {//本地企业
                matchComList = this.noticeMapper.queryMatchComCount(map);
            } else if (comType.equals("1")) {//入湘
                matchComList = this.noticeMapper.queryMatchIntoComCount(map);
            }
            if (matchComList != null && matchComList.size() > 0) {
                queryTotal += matchComList.size();
                batchInertMatchComCount(matchComList, batchNum, comType);
            }

            allDay += stepBy;
            logger.info("##execute...[comType:" + comType + "][queryTotal:" + queryTotal + "][startDay:" + startDay + "][endDay:" + endDay + "][allDay:" + allDay + "][batchNum:" + batchNum + "]");
            intervalDay = MyDateUtils.compareTime(endDay, orgEndDay);
        }
        logger.info("##bulidStatComCount success: [comType:" + comType + "][queryTotal:" + queryTotal + "][orgStartDay:" + orgStartDay + "][orgEndDay:" + orgEndDay + "][allDay:" + allDay + "][batchNum:" + batchNum + "]");
    }

    @Autowired
    @Qualifier("mySqlSessionTemplate")
    private SqlSessionTemplate mySqlSessionTemplate;

    /**
     * 批量插入统计记录
     *
     * @param matchComList
     * @param batchNum
     */
    public void batchInertMatchComCount(List<Map> matchComList, String batchNum, String comType) {
        SqlSession session = mySqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        NoticeMapper batchNoticeMapper = session.getMapper(NoticeMapper.class);
        int batchCount = 100;
        try {
            for (int i = 0; i < matchComList.size(); i++) {
                Map oneMatchCom = matchComList.get(i);
                oneMatchCom.put("batchNum", batchNum);
                oneMatchCom.put("comType", comType);
                batchNoticeMapper.inertMatchComCount(oneMatchCom);
                if (i % batchCount == 0 || i == matchComList.size() - 1) {
                    //手动提交，提交后无法回滚
                    session.commit();
                    //清理缓存，防止溢出
                    session.clearCache();
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        } finally {
            session.clearCache();
            session.close();
        }
    }
}
