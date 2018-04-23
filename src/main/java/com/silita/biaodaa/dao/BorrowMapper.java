package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 借款
 */
public interface BorrowMapper {
    /**
     * 申请保证金借款
     * @param params
     */
    public void insertBorrow(Map params);
}
