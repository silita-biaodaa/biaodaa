package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbDownloadRecord {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 文件 id
     */
    private Integer fileId;

    /**
     * 下载链接
     */
    private String url;

    /**
     * 下载文件类型;zhaobiao:招标；zhongbiao:中标
     */
    private String downType;

    /**
     * 下载次数
     */
    private Integer record;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 修改时间
     */
    private Date updated;

}