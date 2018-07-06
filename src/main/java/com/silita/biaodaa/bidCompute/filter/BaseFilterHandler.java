package com.silita.biaodaa.bidCompute.filter;/**
 * Created by zhangxiahui on 16/9/13.
 */

import com.silita.biaodaa.bidCompute.BlockConfig;
import com.silita.biaodaa.bidCompute.Handler;
import com.silita.biaodaa.dao.TbBidDetailMapper;
import com.silita.biaodaa.dao.TbBidResultMapper;
import com.silita.biaodaa.model.TbBidDetail;
import com.silita.biaodaa.model.TbBidResult;
import com.silita.biaodaa.utils.DoubleUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxiahui
 * @version 1.0
 * @date 2016/09/13 下午3:20
 */
public abstract class BaseFilterHandler<T extends BlockConfig> implements Handler<T> {

    private static Logger logger = LoggerFactory.getLogger(BaseFilterHandler.class);

    public T config;

    protected Handler successor;

    @Autowired
    TbBidResultMapper  tbBidResultMapper;
    @Autowired
    TbBidDetailMapper tbBidDetailMapper;

    @Override
    public void init(T config) {
        this.config = config;
    }

    @Override
    public void setSuccessor(Handler successor){
        this.successor = successor;
    }

    @Override
    public Map<String, Object> handlerRequest(Map resourceMap) {
        logger.info("==进入["+getFilterName()+"]过滤器===");
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",0);
        resultMap.put("msg","["+getFilterName()+"]过滤器,不满足条件.");
        doHandler(resourceMap);
        if(successor==null){
            //入库
            this.saveDatas(resourceMap);
            resultMap.put("code",1);
            resultMap.put("msg","操作成功");
            return resultMap;
        }else{
            return successor.handlerRequest(resourceMap);
        }

    }

    abstract String getFilterName();

    abstract Double doHandler(Map resourceMap);

    // TODO: 保存
    private void saveDatas(Map resourceMap){
        //获取公司基本信息
        Map<String,Object> comMap = MapUtils.getMap(resourceMap,"comInfo");
        //保存公司
        Double lowerRate = MapUtils.getDouble(comMap,"lowerRate");
        Double total = MapUtils.getDouble(resourceMap,"total");
        total = total == null ? 0 : total;
        Double repCount = MapUtils.getDouble(comMap,"repCount");
        repCount = repCount == null ? 0 : repCount;
        total = DoubleUtils.mul(total,repCount);
        TbBidResult bidResult = new TbBidResult();
        bidResult.setBidPkid(MapUtils.getInteger(comMap, "pkid"));
        bidResult.setComName(MapUtils.getString(comMap,"comName"));
        bidResult.setBidPrice(MapUtils.getDouble(comMap,"comPrice"));
        bidResult.setBidRate(DoubleUtils.round(DoubleUtils.mul(lowerRate,100),2)+"%");
        bidResult.setOfferScore(MapUtils.getDouble(comMap,"bidCount"));
        bidResult.setCreditScore(total);
        bidResult.setTotal(
                DoubleUtils.round(DoubleUtils.add(MapUtils.getDouble(comMap,"bidCount"),total,0D),2));
        bidResult.setBidStatus(1);
        tbBidResultMapper.insertBidResult(bidResult);

        //保存获奖
        List<TbBidDetail> detailList = new ArrayList<>();
        TbBidDetail tbBidDetail = null;
        if(null != resourceMap.get("saveList")){
            List<Integer> prizeList = (List<Integer>) resourceMap.get("saveList");
            for (Integer id :prizeList ){
                tbBidDetail = new TbBidDetail();
                tbBidDetail.setCertId(id.toString());
                tbBidDetail.setCertType(1);
                tbBidDetail.setForePkid(bidResult.getPkid());
                detailList.add(tbBidDetail);
            }
        }
        //不良行为
        if(null != resourceMap.get("undesList")){
            List<Map<String,Object>> undesList = (List<Map<String, Object>>) resourceMap.get("undesList");
            for (Map map : undesList){
                tbBidDetail = new TbBidDetail();
                tbBidDetail.setCertId(MapUtils.getString(map,"id"));
                tbBidDetail.setCertType(2);
                tbBidDetail.setForePkid(bidResult.getPkid());
                detailList.add(tbBidDetail);
            }
        }
        //安全认证
        if(null != resourceMap.get("safety")){
            Map<String,Object> safety = MapUtils.getMap(resourceMap,"safety");
            tbBidDetail = new TbBidDetail();
            tbBidDetail.setCertId(MapUtils.getString(safety,"srcUuid"));
            tbBidDetail.setCertType(3);
            tbBidDetail.setForePkid(bidResult.getPkid());
            detailList.add(tbBidDetail);
        }

        //保存
        if(null != detailList && detailList.size() > 0){
            tbBidDetailMapper.batchInsertBidDetail(detailList);
        }
    }
}
