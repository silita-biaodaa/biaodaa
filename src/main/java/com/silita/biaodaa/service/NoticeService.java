package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.ArticlesMapper;
import com.silita.biaodaa.dao.NoticeMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHAOBIAO;
import static com.silita.biaodaa.common.SnatchContent.SNATCHURL_ZHONGBIAO;

/**
 * Created by dh on 2018/4/9.
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private ArticlesMapper articlesMapper;

    /**
     * 资质id拼接
     * @param uuid
     * @param rank
     * @return
     */
    public static String spellUuid(String uuid,String rank) {
        String str="";
        //特级
        if("0".equals(rank)){
            str +=uuid+"/0";
        }
        //一级
        else if("1".equals(rank)){
            str +=uuid+"/1";
        }
        //二级
        else if("2".equals(rank)){
            str +=uuid+"/2";
        }
        //三级
        else if("3".equals(rank)){
            str +=uuid+"/3";
        }
        //一级及以上
        else if("u1".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1";
        }
        //二级及以上
        else if("u2".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2";
        }
        //三级及以上
        else if("u3".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
        }
        //特级及以下
        else if("d0".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
        }
        //一级及以下
        else if("d1".equals(rank)){
            str +=uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
        }
        //二级及以下
        else if("d2".equals(rank)){
            str +=uuid+"/2"+","+uuid+"/3";
        }
        //一级及以上
        else if("11".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1";
        }
        //二级及以上
        else if("21".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2";
        }
        //三级及以上
        else if("31".equals(rank)){
            str +=uuid+"/0"+","+uuid+"/1"+","+uuid+"/2"+","+uuid+"/3";
        }
        //无等级
        else if("no".equals(rank)){
            str +=uuid+"/";
        }

        //甲级
        else if("-1".equals(rank)){
            str +=uuid+"/-1";
        }
        //乙级
        else if("-2".equals(rank)){
            str +=uuid+"/-2";
        }
        //丙级
        else if("-3".equals(rank)){
            str +=uuid+"/-3";
        }
        //乙级及以上
        else if("-21".equals(rank)){
            str +=uuid+"/-1"+","+uuid+"/-2";
        }
        //丙级及以上
        else if("-31".equals(rank)){
            str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3";
        }
        //乙级及以上
        else if("u-2".equals(rank)){
            str +=uuid+"/-1"+","+uuid+"/-2";
        }
        //丙级及以上
        else if("u-3".equals(rank)){
            str +=uuid+"/-1"+","+uuid+"/-2"+","+uuid+"/-3";
        }
        return str;
    }



    public PageInfo queryNoticeList(Page page, Map params){
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
        buildNoticeDetilTable(params);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map> list = noticeMapper.queryNoticeList(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 单独获取结果集中的关注状态
     * @param list
     * @param params
     */
    public void addCollStatus(List list, Map params){
        String userId = VisitInfoHolder.getUid();
        if(MyStringUtils.isNull(userId)) {
            return;
        }
        if(list !=null && list.size()>0) {
            Set idSet = new HashSet();
            if(list.get(0) instanceof TbCompany){
                for (Object obj :list){
                    idSet.add(((TbCompany)obj).getComId());
                }
            }else{
                for (Object obj :list){
                    Map map = (Map)obj;
                    idSet.add(map.get(params.get("idKey")));
                }
            }

            Map argMap = new HashMap();
            argMap.put("userId",userId);
            argMap.put("idSet",idSet);
            List<Long> collIds = null;
            if("notice".equals(params.get("collType"))) {
                collIds = this.queryNoticeCollStatus(argMap);
            }else if("company".equals(params.get("collType"))){
                collIds = this.queryCompanyCollStatus(argMap);
            }

            if(list.get(0) instanceof TbCompany){//企业信息
                if (collIds != null && collIds.size() > 0) {
                    for (Object obj : list) {
                        TbCompany comObj = ((TbCompany)obj);
                        if (collIds.contains(Long.parseLong(comObj.getComId().toString()))) {
                            comObj.setCollected(true);
                        } else {
                            comObj.setCollected(false);
                        }
                    }
                }
            }else {//公告信息
                if (collIds != null && collIds.size() > 0) {
                    for (Object obj : list) {
                        Map map = (Map)obj;
                        if (collIds.contains(Long.parseLong(map.get(params.get("idKey")).toString()))) {
                            map.put("collected", true);
                        } else {
                            map.put("collected", false);
                        }
                    }
                } else {
                    for (Object obj : list) {
                        Map map = (Map)obj;
                        map.put("collected", false);
                    }
                }
            }
        }
    }

    /**
     * 根据公告ID查询资质，然后根据资质查询企业列表
     * @param argMap
     * @return
     */
    public PageInfo queryCompanyListById(Page page,Map argMap){
        String zzRes = noticeMapper.queryNoticeZZById(argMap);
        if(zzRes !=null && zzRes.trim().length()>1){
            String[] zzStr = zzRes.split(",");
            if(zzStr!=null && zzStr.length>0) {
                Set<String> zzSet = new HashSet<String>();
                zzSet.addAll(Arrays.asList(zzStr));
                if (zzSet.size() > 0) {
                    List zzList = new ArrayList(zzSet.size());
                    zzList.addAll(zzSet);
                    argMap.put("zzList",zzList);
                    PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
                    List<TbCompany> list = noticeMapper.queryComInfoByZZ(argMap);
                    Map idsMap = new HashMap();
                    idsMap.put("comList",list);
                    List<TbCompany> companyCertList = noticeMapper.selectCompanyCert(idsMap);
                    List<TbCompany> companyCertBasicList = noticeMapper.selectCompanyCertBasic(idsMap);
                    for(TbCompany company: list){
                        int comid = company.getComId().intValue();
                        for(TbCompany cert:companyCertList){
                                if(cert.getComId().intValue()==comid){
                                    company.setCertNo(cert.getCertNo());
                                    company.setCertDate(cert.getCertDate());
                                    company.setValidDate(cert.getValidDate());
                                    companyCertList.remove(cert);
                                    break;
                                }
                        }
                        for(TbCompany certBase:companyCertBasicList){
                            if(certBase.getComId().intValue()==comid){
                                company.setComRange(certBase.getComRange());
                                company.setSubsist(certBase.getSubsist());
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
     * @param argMap
     * @return
     */
    public List queryNoticeZZById(Map argMap){
        String zzRes = noticeMapper.queryNoticeZZById(argMap);
        if(zzRes !=null && zzRes.trim().length()>1) {
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
     * @param argMap
     * @return
     */
    public Integer queryCompanyCountById(Map argMap){
        List zzList = queryNoticeZZById(argMap);
        if(zzList!=null && zzList.size()>0) {
            return noticeMapper.queryCompanyCountByZZ(zzList);
        }
        return null;
    }

    /**
     * 公告详情（多标段）
     * @param params
     * @return
     */
    public List queryNoticeDetail(Map params){
        buildNoticeDetilTable(params);
        List<Map> resList = noticeMapper.queryNoticeDetail(params);
        if(resList!=null &&resList.size()>1){
            for(int i=1;i<resList.size();i++){
                Map res = resList.get(i);
                res.remove("content");
            }
        }
        return resList;
    }

    private void buildNoticeDetilTable(Map params){
        String type= MapUtils.getString(params, "type");
        if(type.equals(SNATCHURL_ZHAOBIAO)){
            params.put("detailTable","zhaobiao_detail");
        }else if(type.equals(SNATCHURL_ZHONGBIAO)){
            params.put("detailTable","zhongbiao_detail");
        }
    }

    /**
     * 查询相关公告数量
     * @param id
     * @return
     */
    public Long queryRelCount(Long id){
        return noticeMapper.queryRelCount(id);
    }

    /**
     * 查询招标文件列表
     * @param id
     * @return
     */
    public List<Map> queryNoticeFile(Long id){
        return noticeMapper.queryNoticeFile(id);
    }

    /**
     * 招标，中标结果分拣
     */
    private void sortingResult(List<Map> relList){
        if(relList!=null && relList.size()>0) {
            //招标，中标结果划分
            for (Map result:relList){
                String type = result.get("type").toString();
                if(type.equals(SNATCHURL_ZHAOBIAO)){
                    if(result.get("zhaobiao_pbMode")!=null) {
                        result.put("pbMode", result.get("zhaobiao_pbMode"));
                        result.remove("zhaobiao_pbMode");
                    }
                }else if(type.equals(SNATCHURL_ZHONGBIAO)){
                    if(result.get("zhongbiao_pbMode") !=null) {
                        result.put("pbMode", result.get("zhongbiao_pbMode"));
                        result.remove("zhongbiao_pbMode");
                    }

                }
            }
        }
    }

    public PageInfo searchNoticeList(Page page, Map params){
        //地区筛选
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
        //项目金额筛选
        if(MyStringUtils.isNotNull(params.get("projSumStart"))){
            params.put("projSumStart",Integer.parseInt(params.get("projSumStart").toString()));
        }
        if(MyStringUtils.isNotNull(params.get("projSumEnd"))){
            params.put("projSumEnd",Integer.parseInt(params.get("projSumEnd").toString()));
        }
        buildNoticeDetilTable(params);
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map> list = noticeMapper.searchNoticeList(params);
        sortingResult(list);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public List<Map> queryRelations(Map map){
        List<Map> relList =noticeMapper.queryRelations(map);
        sortingResult(relList);
        return relList;
    }

    /**
     * 查询公告文章列表
     * @param page
     * @param params
     * @return
     */
    public PageInfo queryArticleList(Page page, Map params) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List list = articlesMapper.queryArticleList(params);
        if (null == list) {
            list = new ArrayList<>();
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 根据id查询公告文章详细
     * @param id
     * @return
     */
    public Map queryArticleDetail(Integer id) {
        return articlesMapper.queryArticleDetail(id);
    }

    public List<Long> queryCompanyCollStatus(Map map){
        return noticeMapper.queryCompanyCollStatus(map);
    }

    public List<Long> queryNoticeCollStatus(Map map){
        return noticeMapper.queryNoticeCollStatus(map);
    }
}
