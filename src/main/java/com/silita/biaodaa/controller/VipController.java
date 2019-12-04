package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.ReportService;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.to.OpenMemberTO;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.pay.service.OrderInfoService;
import com.silita.pay.vo.MyPage;
import com.silita.pay.vo.OrderInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员接口controller
 */
@RequestMapping("/vip")
@Controller
public class VipController extends BaseController {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private VipService vipService;
    @Autowired
    private ReportService reportService;

    @Autowired
    OrderInfoService orderInfoService;

    private String preQueryFeeStd(TbVipFeeStandard tbVipFeeStandard) {
        if (MyStringUtils.isNull(tbVipFeeStandard.getChannel())) {
            return Constant.ERR_NULL_CHANNEL;
        }
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/queryFeeStandard", produces = "application/json;charset=utf-8")
    public Map<String, Object> queryFeeStandard(@RequestBody TbVipFeeStandard tbVipFeeStandard) {
        Map result = new HashMap();
        try {
            String errCode = preQueryFeeStd(tbVipFeeStandard);
            if (errCode == null) {
                List<TbVipFeeStandard> list = vipService.queryFeeStandard(tbVipFeeStandard.getChannel());
                successMsg(result, list);
            } else {
                errorMsg(result, errCode, "必填参数出错。");
            }
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(result, Constant.EXCEPTION_CODE, e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getFeeStandard", produces = "application/json;charset=utf-8")
    public Map<String, Object> getFeeStandard(@RequestBody Map<String, Object> paran) {
        return successMsgs(vipService.getFeeStandards());
    }


    @ResponseBody
    @RequestMapping(value = "/queryMyProfits", produces = "application/json;charset=utf-8")
    public Map<String, Object> queryMyProfits(@RequestBody Map param) {
        Map result = new HashMap();
        try {
            Page page = buildPage(param);
            String userId = VisitInfoHolder.getUid();
            PageInfo info = vipService.queryProfitInfo(page, userId);
            Integer total = 0;
            if (info != null && info.getList() != null) {
                total = vipService.queryProfitTotal(userId);
            }
            result.put("totalDays", total);
            buildReturnMap(result, info);
            successMsg(result);
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(result, e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/openMemberRights", produces = "application/json;charset=utf-8")
    public Map<String, Object> openMemberRights(@RequestBody OpenMemberTO toOpenMember) {
        Map result = new HashMap();
        try {
            toOpenMember.setUserId(VisitInfoHolder.getUid());
            String errMsg = vipService.openMemberRights(toOpenMember);
            if (errMsg == null) {
                successMsg(result);
            } else {
                errorMsg(result, errMsg);
            }
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(result, e.getMessage());
        }
        return result;

    }


    @ResponseBody
    @RequestMapping(value = "/queryOrderList", produces = "application/json;charset=utf-8")
    public Map<String, Object> queryOrderList(@RequestBody Map param) {
        Map result = new HashMap();
        int pageNo = MapUtils.getIntValue(param, "pageNo");
        int pageSize = MapUtils.getIntValue(param, "pageSize");
        String channelNo = MapUtils.getString(param, "channelNo");
        Integer orderStatus = MapUtils.getInteger(param, "orderStatus");
        try {
            Map pMap = new HashMap();
            pMap.put("userId", VisitInfoHolder.getUid());
            if (StringUtils.isNotEmpty(channelNo)) {
                pMap.put("channelNo", channelNo);
            }
            pMap.put("isDelete", 0);
            if (orderStatus != null) {
                if (orderStatus == 11) {//非支付成功
                    pMap.put("orderStatus_ne", 9);
                } else {
                    pMap.put("orderStatus", orderStatus);
                }
            }
            MyPage<OrderInfo> myPage = orderInfoService.queryOrderPages(pMap, pageNo - 1, pageSize);
            if (myPage != null) {
                myPage.setInfoList(setReport(myPage, orderStatus));
                successMsg(result, myPage.getInfoList());
                result.put("pageNo", myPage.getPageNo() + 1);
                result.put("pageSize", pageSize);
                result.put("pages", myPage.getPages());
                result.put("total", myPage.getTotal());
            } else {
                errorMsgs(result, "1", "订单查询为空");
            }
        } catch (Exception e) {
            logger.error(e, e);
            errorMsg(result, "订单查询异常：" + e.getMessage());
        }
        return result;
    }

    private List<OrderInfo> setReport(MyPage<OrderInfo> page, Integer orderStatus) {
        List<OrderInfo> resuList = new ArrayList<>();
        List<OrderInfo> list = page.getInfoList();
        Map<String, Object> param = new HashedMap() {{
            put("payStatus", orderStatus);
        }};
        for (OrderInfo orderInfo : list) {
            if ("report_com".equals(orderInfo.getStdCode()) || "report_vip".equals(orderInfo.getStdCode()) || "special_query_com".equals(orderInfo.getStdCode()) ||
                    "special_query_vip".equals(orderInfo.getStdCode())) {
                param.put("orderNo", orderInfo.getOrderNo());
                Map<String, Object> map = reportService.getReportMap(param);
                if (null != map) {
                    // map.put("createTime",MyDateUtils.getDate(orderInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    // map.put("updateTime",MyDateUtils.getDate(orderInfo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
                    orderInfo.setReport(map);
                    resuList.add(orderInfo);
                } else {
                    resuList.add(orderInfo);
                }
            } else {
                resuList.add(orderInfo);
            }
        }
        return resuList;
    }

    private int setPages(Integer total, Integer pageSize) {
        int pages = 0;
        if (total % pageSize == 0) {
            pages = total / pageSize;
        } else {
            pages = (total / pageSize) + 1;
        }
        return pages;
    }
}
