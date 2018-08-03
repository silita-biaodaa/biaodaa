package com.silita.biaodaa.dao;

import java.util.Map;

/**
 * 意见反馈
 */
public interface FeedbackMapper {
    /**
     * 添加意见反馈
     * @param params
     */
     void insertFeedback(Map<String, Object> params);
}
