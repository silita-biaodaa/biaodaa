/**
 * Created by mangang.feng at 8/21/15
 */
package com.silita.biaodaa.controller.vo;

import java.io.Serializable;

public class Page implements Serializable {

    public static final int PAGE_SIZE_DEFAULT = 20;
    /**
     * 当前界面
     */
    private Integer currentPage = 1;
    /**
     * 每页显示的记录数,默认20条
     */
    private Integer pageSize = PAGE_SIZE_DEFAULT;

    public static int getPageSizeDefault() {
        return PAGE_SIZE_DEFAULT;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null) {
            this.pageSize = this.getPageSizeDefault();
        } else {
            this.pageSize = pageSize;
        }
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        if (currentPage == null) {
            this.currentPage = 1;
        } else {
            this.currentPage = currentPage;
        }
    }

    //按照升序排列
    public static final String SORT_METHOD_ASC = " ASC";

    //按照降序排列
    public static final String SORT_METHOD_DESC = " DESC";

    //排序的类型,升序是0,降序是1
    private Integer sortType;

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
}
