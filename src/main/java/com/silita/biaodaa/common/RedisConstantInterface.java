package com.silita.biaodaa.common;

public interface RedisConstantInterface {
	String GG_LIST = "inter_gg_list_";//公告表单详情

	String GG_LIST_COMPANY = "inter_company_list_";//公告表单详情

	String GG_SEARCH_LIST = "inter_gg_search_list_";//公告表单详情

	String GG_DETAIL = "inter_gg_detail_";//公告表单详情

	String GG_REL_LIST = "inter_gg_rel_";//相关公告列表

	String GG_REL_COM_LIST = "inter_gg_relCom_";//公告关联的企业列表

	String COM_NAME_MATCH = "com_name_match";//企业名称模糊匹配列表

	String COM_NAME_APTITUDE = "com_name_aptitude";//企业名称模糊匹配列表

	String PROJECT_LIST = "project_list"; //业绩列表

	String PERSON_LIST = "person_list_";  //人员缓存

	String NOTIC_LAW = "notic_law_";  //公告缓存

	int DETAIL_OVER_TIME = 30*60;

	int LIST_OVER_TIME = 3600;

//	int LIST_OVER_TIME = 3600*24;

	int COM_OVER_TIME = 60*60;

	int PRO_OVER_TIME = 3600*24;

	int LAW_LIST_OVER_TIME = 3600*24*7;
}
