package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * carousel_image Mapper
 */
public interface CarouselImageMapper extends MyMapper<CarouselImage> {

    /**
     * 获取banner图
     *
     * @return
     */
    List<CarouselImage> listCarouselImageByTypeAndShowType(Map<String, Object> params);
}