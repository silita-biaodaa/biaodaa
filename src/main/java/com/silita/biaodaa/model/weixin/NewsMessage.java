package com.silita.biaodaa.model.weixin;

import java.util.List;

/**
 * Created by zhangxiahui on 17/6/14.
 */
public class NewsMessage extends BaseMessage {
    /**
     * 图文消息个数，限制为10条以内
     */
    private int ArticleCount;
    /**
     * 多条图文消息信息，默认第一个item为大图,注意，如果图文数超过10，则将会无响应
     */
    private List<Articles> Articles;
    /**
     * @return the articleCount
     */
    public int getArticleCount() {
        return ArticleCount;
    }
    /**
     * @param articleCount the articleCount to set
     */
    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }
    /**
     * @return the articles
     */
    public List<Articles> getArticles() {
        return Articles;
    }
    /**
     * @param articles the articles to set
     */
    public void setArticles(List<Articles> articles) {
        Articles = articles;
    }
}