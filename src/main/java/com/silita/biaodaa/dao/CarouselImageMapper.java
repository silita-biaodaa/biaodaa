package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface CarouselImageMapper extends MyMapper<CarouselImage> {
    /**
     *
     * @return
     */
    List<CarouselImage> listCarouselImageByTypeAndShowType(Map<String, Object> params);
}