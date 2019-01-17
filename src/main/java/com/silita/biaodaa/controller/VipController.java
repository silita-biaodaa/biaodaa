package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员接口controller
 */
@RequestMapping("/vip")
@Controller
public class VipController extends BaseController{

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private VipService vipService;

    private String preQueryFeeStd(TbVipFeeStandard tbVipFeeStandard){
        if(MyStringUtils.isNull(tbVipFeeStandard.getChannel())){
            return Constant.ERR_NULL_CHANNEL;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/queryFeeStandard",produces = "application/json;charset=utf-8")
    public Map<String,Object> queryFeeStandard(@RequestBody TbVipFeeStandard tbVipFeeStandard){
        Map result = new HashMap();
        try{
            String errCode = preQueryFeeStd(tbVipFeeStandard);
            if(errCode==null) {
                List<TbVipFeeStandard> list = vipService.queryFeeStandard(tbVipFeeStandard.getChannel());
                successMsg(result,list);
            }else{
                errorMsg(result,errCode,"必填参数出错。");
            }
        }catch (Exception e){
            logger.error(e,e);
            errorMsg(result,Constant.EXCEPTION_CODE,e.getMessage());
        }
        return result;
    }
}
