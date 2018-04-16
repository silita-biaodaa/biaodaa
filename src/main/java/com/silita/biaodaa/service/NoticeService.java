package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.NoticeMapper;
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
    private CommonService commonService;

    @Autowired
    private NoticeMapper noticeMapper;
//
//    @Cacheable(value = "fenleiData", key="'fenleiData'+#search.cert+#search.title" +
//                    "+#search.type+#search.rangeDate+#search.range+#search.isShow" +
//                    "+#search.startDate+#search.endDate+#search.aptitude+#search.zhaobiaodetail" +
//                    "+#search.zhongbiaodetail+#search.pageNumber")
//    public Map<String,Object> fenleiData(Page page, SnatchUrl search){
//        Map<String,Object> resultMap = new HashMap();
//
//        // 获取当前年份，数据分区用
//        Calendar a = Calendar.getInstance();
//        search.setRange(a.get(Calendar.YEAR));
//
//        PageInfo pageInfo = this.querysSnatchUrlByType(search);
////        List<Map<String, Object>> aptList = this.querysAptitudeDictionary();
////        List<Map<String, Object>> dqList = this.querysDq();
//        //List<Map<String, Object>> rangeList = this.querysRange();获取近三年年份
//        //获取当前年份近三年
//        List<Integer> range = new ArrayList<Integer>();
//        range.add(a.get(Calendar.YEAR));
//        range.add(a.get(Calendar.YEAR)-1);
//        range.add(a.get(Calendar.YEAR)-2);
//
////        //省份代码，用于全国筛选
////        List<Map<String, Object>> provinceList = this.dao.getAreaProvinceCode();
////
////        search.setTotal(pageInfo.getTotal());
////        search.setTotalPages(pageInfo.getPages());
////        resultMap.put("dataList", pageInfo.getList());
////        resultMap.put("search", search);
////        resultMap.put("type_title", search.getTypeTitle());
////        resultMap.put("aptList", aptList);
////        resultMap.put("dqList", dqList);
//        resultMap.put("rangeList", range);
//        resultMap.put("type", search.getCert());
////        resultMap.put("provinceList", provinceList);
//
//        return resultMap;
//    }
//
//
//    public PageInfo querysSnatchUrlByType(SnatchUrl s) {
//        String sql = "";
//        String con = "";
//
//        // 根据企业查询可投标项目
//        if (s.getCert() != null) {
//            // 获取企业的资质id
//            List<Integer> list = noticeMapper.queryNoticesByCompanyName(s.getCert().getCompanyName());
//            if (list!=null && list.size() > 0) {
//                con = list.toString().replaceAll("\\[", "")
//                        .replaceAll("\\]", "");
//            }
//        }
//
//        if ("0".equals(s.getType())) {
//            sql += "select s.*,(SELECT MAX(pbMode) FROM mishu.zhaobiao_detail WHERE snatchUrlId =s.id ) pbMode,(SELECT IF(COUNT(certificate)>1,CONCAT(certificate,'(多资质)'),certificate) FROM mishu.snatch_url_cert WHERE contId =s.id ) certificate from mishu.snatchurl s where 1=1";
//        } else {
//            sql += "select s.*,(SELECT IF(COUNT(oneName)>1,CONCAT(oneName,'(等)'),oneName) oneName FROM mishu.zhongbiao_detail WHERE snatchUrlId =s.id) oneName from mishu.snatchurl s where 1=1";
//        }
//
//        List<String> params = new ArrayList<String>();
//        if (!"".equals(con)) {
//            sql += " and s.id IN( " + con + ")";
//        }
//
//        if (MyStringUtils.isNotNull(s.getRangeDate())) {
//            // 判断是近xx范围,还是年份
//            if (Integer.parseInt(s.getRangeDate()) < 2000) {
//                sql += " and TO_DAYS(NOW())-? < TO_DAYS(openDate) ";
//                params.add(s.getRangeDate());
//                //sql += " and s.range = ?";
//                //params.add(String.valueOf(s.getRange()));
//            } else {
//                sql += " and s.range = ? ";
//                params.add(s.getRangeDate());
//            }
//        }
//        else {
//            sql += " and s.range = ? ";
//            params.add(String.valueOf(s.getRange()));
//            s.setRangeDate(String.valueOf(s.getRange()));
//        }
//        if (MyStringUtils.isNotNull(s.getTitle())) {
//            sql += " and s.title like ? ";
//            params.add("%" + s.getTitle().replaceAll(" ", "%") + "%");
//        }
//        if (MyStringUtils.isNotNull(s.getType())) {
//            sql += " and s.type = ? ";
//            params.add(String.valueOf(s.getType()));
//        }
//        if(MyStringUtils.isNotNull(s.getIsShow())){
//            sql += " and s.isShow = ? ";
//            params.add(String.valueOf(s.getIsShow()));
//        }
//        if (MyStringUtils.isNotNull(s.getStartDate())) {
//            sql += " and s.openDate >= ? ";
//            params.add(s.getStartDate());
//        }
//        if (MyStringUtils.isNotNull(s.getEndDate())) {
//            sql += " and s.openDate <= ? ";
//            params.add(s.getEndDate());
//        }
//
//        // 资质条件
//        if (s.getAptitude() != null) {
//            if (MyStringUtils.isNotNull(s.getAptitude().getMajorUuid())) {
//                String uuids[] = s.getAptitude().getMajorUuid().split(",");
//                String ranks[] = s.getAptitude().getZzRank().split(",");
//                String alluuid = "";
//                if (MyStringUtils.isNotNull(s.getAptitude().getZzRank())) {
//                    for (int i = 0; i < ranks.length; i++) {
//                        if ("".equals(alluuid)) {
//                            alluuid = spellUuid(uuids[0], ranks[i]);
//                        } else {
//                            alluuid += "','"
//                                    + spellUuid(uuids[0], ranks[i]);
//                        }
//                    }// 服务器查询缓慢，特加的 FORCE INDEX(snatchurlcert_contid_index)
//                    sql += " and s.id in (select contId from snatch_url_cert FORCE INDEX(snatchurlcert_contid_index) where certificateUuid in('"
//                            + alluuid + "') and certificateUuid <>'')"; // 模糊匹配所有条件符合搜索条件的公告
//                } else {
//                    sql += " and s.id in (select contId from snatch_url_cert FORCE INDEX(snatchurlcert_contid_index) where   certificateUuid <>'')"; // 模糊匹配所有条件符合搜索条件的公告
//                }
//
//            }
//        }
//        // 招标明细搜索
//        if (s.getZhaobiaodetail() != null) {
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getPbMode())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmStartDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndTime())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndTime())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjSum())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjDq())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjXs())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmSite())) {
//                sql += " and s.id in (select snatchUrlId from zhaobiao_detail where 1=1";
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getPbMode())) {
//                sql += " and pbMode in('"
//                        + s.getZhaobiaodetail().getPbMode().replace(",", "','")
//                        + "')";
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmStartDate())) {
//                sql += " and bmStartDate >= ? and bmStartDate <>'/' and bmStartDate <>''";
//                params.add(s.getZhaobiaodetail().getBmStartDate());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndDate())) {
//                sql += " and bmEndDate = ? and bmEndDate <>'/' and bmEndDate <>''";
//                params.add(s.getZhaobiaodetail().getBmEndDate());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndTime())) {
//                sql += " and bmEndTime <= ? and bmEndTime <>''";
//                params.add(s.getZhaobiaodetail().getBmEndTime());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndDate())) {
//                sql += " and tbEndDate = ? and tbEndDate <>''";
//                params.add(s.getZhaobiaodetail().getTbEndDate());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndTime())) {
//                sql += " and tbEndTime >= ? and tbEndTime <>''";
//                params.add(s.getZhaobiaodetail().getTbEndTime());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjSum())) {
//                sql += " and projSum > " + s.getZhaobiaodetail().getProjSum()
//                        + " and projSum <>''";
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjDq())) {
//                sql += " and projDq = (SELECT `name` FROM mishu.area WHERE id = ?)";
//                params.add(s.getZhaobiaodetail().getProjDq());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjXs())) {
//                sql += " and projXs = (SELECT `name` FROM mishu.area WHERE id = ?)";
//                params.add(s.getZhaobiaodetail().getProjXs());
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmSite())) {
//                if ("1".equals(s.getZhaobiaodetail().getBmSite())) {
//                    sql += " and bmSite = '网上下载'";
//                } else {
//                    sql += " and bmSite <> '网上下载'";
//                }
//            }
//            if (MyStringUtils.isNotNull(s.getZhaobiaodetail().getPbMode())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmStartDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmEndTime())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndDate())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getTbEndTime())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjSum())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjDq())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getProjXs())
//                    || MyStringUtils.isNotNull(s.getZhaobiaodetail().getBmSite())) {
//                sql += ")";
//            }
//        }
//
//        // 中标单位
//        if (s.getZhongbiaodetail() != null && "2".equals(s.getType())) {
//            if (MyStringUtils.isNotNull(s.getZhongbiaodetail().getOneName())
//                    || MyStringUtils.isNotNull(s.getZhongbiaodetail().getProjSum())) {
//                sql += " and s.id in (select zd.snatchUrlId from zhongbiao_detail zd left join cert_src cs on zd.oneUuid=cs.uuid where 1=1";
//            }
//            if (MyStringUtils.isNotNull(s.getZhongbiaodetail().getOneName())) {
//                sql += " and cs.companyName like ?";
//                params.add("%" + s.getZhongbiaodetail().getOneName() + "%");
//            }
//            if (MyStringUtils.isNotNull(s.getZhongbiaodetail().getProjSum())) {
//                sql += " and zd.projSum >= "
//                        + s.getZhongbiaodetail().getProjSum()
//                        + " and zd.projSum <>''";
//            }
//            if (MyStringUtils.isNotNull(s.getZhongbiaodetail().getOneName())
//                    || MyStringUtils.isNotNull(s.getZhongbiaodetail().getProjSum())) {
//                sql += ")";
//            }
//        }
//
//        sql += " order by s.openDate desc,lect s.*,(SELECT MAX(pbMode) " +
//                "FROM mishu.zhaobiao_detail WHERE snatchUrlId =s.id ) pbMode," +
//                "(SELECT IF(COUNT(certificate)>1,CONCAT(certificate,'(多资质)'),certificate) " +
//                "FROM mishu.snatch_url_cert WHERE contId =s.id ) certificate " +
//                "from mishu.snatchurl s where 1=1 and s.range = ?  and s.type = ?  " +
//                "and s.isShow = ?  " +
//                "order by s.openDate desc,s.edit desc,s.biddingType ASC,s.id descs.edit desc,s.biddingType ASC,s.id desc";
//            //// TODO: 2018/4/10 数据库方式待后续优化，优先改造es查询
////        PageInfo pageInfo = this.queryForPage(sql,params,s);
//        PageInfo pageInfo = new PageInfo();
//        return pageInfo;
//    }

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

    public PageInfo searchNoticeList(Page page,Map params){
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
     * 根据公告ID查询资质，然后根据资质查询企业列表
     * @param id
     * @return
     */
    public PageInfo queryCompanyListById(Page page,Long id){
        String zzRes = noticeMapper.queryNoticeZZById(id);
        if(zzRes !=null && zzRes.trim().length()>1){
            String[] zzStr = zzRes.split(",");
            if(zzStr!=null && zzStr.length>0) {
                Set<String> zzSet = new HashSet<String>();
                zzSet.addAll(Arrays.asList(zzStr));
                if (zzSet.size() > 0) {
                    List zzList = new ArrayList(zzSet.size());
                    zzList.addAll(zzSet);
                    PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
                    List list = noticeMapper.queryComInfoByZZ(zzList);
                    PageInfo pageInfo = new PageInfo(list);
                    return pageInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据ID查询资质ID列表（去重）
     * @param id
     * @return
     */
    public List queryNoticeZZById(Long id){
        String zzRes = noticeMapper.queryNoticeZZById(id);
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
     * @param id
     * @return
     */
    public Integer queryCompanyCountById(Long id){
        List zzList = queryNoticeZZById(id);
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
        return noticeMapper.queryNoticeDetail(params);
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

    public List<Map> queryRelations(Long id){
        List<Map> relList =noticeMapper.queryRelations(id);
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
        return relList;
    }

}
