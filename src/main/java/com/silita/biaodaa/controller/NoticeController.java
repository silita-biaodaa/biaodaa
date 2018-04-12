package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.common.SnatchContent;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

//    @RequestParam json对象参数
    @ResponseBody
    @RequestMapping(value = "/queryList",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Map<String,Object> queryList(@RequestBody Map params){
        Map resultMap = new HashMap();
        try {
//        search.setTypeToInt(type);
//        encodingConvert(search);
            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
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
                Page page = new Page();
                page.setPageSize(pageSize);
                page.setCurrentPage(pageNo);

                int paramHash = ObjectUtils.buildMapParamHash(params);
                String listKey = RedisConstantInterface.GG_LIST + paramHash;
                PageInfo pageInfo = (PageInfo) myRedisTemplate.getObject(listKey);
                if (pageInfo == null) {
                    pageInfo = noticeService.searchNoticeList(page, params);
                    if(pageInfo!=null &&  pageInfo.getList()!=null &&  pageInfo.getList().size()>0) {
                        myRedisTemplate.setObject(listKey, pageInfo, RedisConstantInterface.LIST_OVER_TIME);
                    }
                }
                resultMap.put("data", pageInfo.getList());
                resultMap.put("pageNum", pageInfo.getPageNum());
                resultMap.put("pageSize", pageInfo.getPageSize());
                resultMap.put("total", pageInfo.getTotal());
                resultMap.put("pages", pageInfo.getPages());
                resultMap.put(this.CODE_FLAG, SUCCESS_CODE);
                resultMap.put(this.MSG_FLAG, SUCCESS_MSG);
            }
            //        recordAccessCount(request,params); 记录访问数
        }catch (Exception e){
            logger.error(e,e);
            resultMap.put(this.CODE_FLAG,this.FAIL_CODE);
            resultMap.put(this.MSG_FLAG,e.getMessage());
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
//
//    private void encodingConvert(SnatchUrl search)throws UnsupportedEncodingException{
//        if (search.getTitle()
//                .equals(new String(search.getTitle().getBytes("iso8859-1"),
//                        "iso8859-1"))) {
//            search.setTitle(new String(search.getTitle().getBytes("ISO8859_1"),
//                    "utf-8"));// 解码:用什么字符集编码就用什么字符集解码
//        }
//        //招标的评标办法
//        if (search.getZhaobiaodetail() != null) {
//            if (search
//                    .getZhaobiaodetail()
//                    .getPbMode()
//                    .equals(new String(search.getZhaobiaodetail().getPbMode()
//                            .getBytes("iso8859-1"), "iso8859-1"))) {
//                search.getZhaobiaodetail().setPbMode(
//                        new String(search.getZhaobiaodetail().getPbMode()
//                                .getBytes("ISO8859_1"), "utf-8"));// 解码:用什么字符集编码就用什么字符集解码
//                search.setZhaobiaodetail(search.getZhaobiaodetail());
//            }
//        }
//
//        //中标的第一候选人
//        if (search.getZhongbiaodetail() != null) {
//            if (search
//                    .getZhongbiaodetail()
//                    .getOneName()
//                    .equals(new String(search.getZhongbiaodetail().getOneName()
//                            .getBytes("iso8859-1"), "iso8859-1"))) {
//                search.getZhongbiaodetail().setOneName(
//                        new String(search.getZhongbiaodetail().getOneName()
//                                .getBytes("ISO8859_1"), "utf-8"));// 解码:用什么字符集编码就用什么字符集解码
//                search.setZhongbiaodetail(search.getZhongbiaodetail());
//            }
//        }
//    }

}
