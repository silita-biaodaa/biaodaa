package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.CarouselImageMapper;
import com.silita.biaodaa.dao.FeedbackMapper;
import com.silita.biaodaa.dao.TbHotWordsMapper;
import com.silita.biaodaa.dao.VersionMapper;
import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.model.TbHotWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 91567 on 2018/4/12.
 */
@Service("foundationService")
public class FoundationService {
    @Autowired
    private CarouselImageMapper carouselImageMapper;
    @Autowired
    private TbHotWordsMapper hotWordsMapper;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 根据showType取得全部的banner图
     * @param params
     * @return
     */
    public List<CarouselImage> queryCarouselImageList(Map<String, Object> params) {
        return carouselImageMapper.listCarouselImageByTypeAndShowType(params);
    }

    public List<TbHotWords> queryHotWordsByTypeList(Map<String, Object> params) {
        return hotWordsMapper.listHotWordsByType(params);
    }

    /**
     * 获得版本信息
     * @param loginChannel  登录频道
     * @return
     */
    public String getVersion(String loginChannel) {
        return versionMapper.getVersion(loginChannel);
    }

    /**
     * 添加意见反馈
     * @param params
     */
    public void addFeedback(Map<String, Object> params) {
        feedbackMapper.insertFeedback(params);
    }
}
