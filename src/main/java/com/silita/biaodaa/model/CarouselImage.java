package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarouselImage {

    private Integer id;

    private String imagesrc;

    /**
     *  0IOS 1Android 2都显示
     */
    private Integer type;

    private String uploaddate;

    private String imageurl;

    private Integer ordernum;

    private Integer showtype;
}