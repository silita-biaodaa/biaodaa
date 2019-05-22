package com.silita.biaodaa.common;

public class Constant {
    //渠道号
    public static final String CHANNEL_ANDROID = "1001";
    public static final String CHANNEL_IOS = "1002";
    public static final String CHANNEL_PC = "1003";

    public static final String SPLIT_STRING = "@@####@@";
    public static final String DEFAULT_STRING = "___";

    public static final String PRIZE_LUBAN = "鲁班奖";
    public static final String PRIZE_BUILD = "全国建设工程项目施工安全生产标准化工地";
    public static final String PRIZE_DECORATE = "全国建筑工程装饰奖";
    public static final String PRIZE_LOTUS = "芙蓉奖";
    public static final String PRIZE_SUPER = "省优工程";
    public static final String PRIZE_SUPER2 = "优质工程";


    public static final String EXCELL_PROVINCE = "省级优秀";
    public static final String QUAL_PROVINCE = "省级合格";
    public static final String EXCELL_CITY = "市级优秀";
    public static final String QUAL_CITY = "市级合格";

    public static final String SCORE_COMM = "一般";
    public static final String SCORE_SEV = "严重";

    public static final String JOIN_TYPE_CAN = "参建单位";
    public static final String JOIN_TYPE_CHENG = "承建单位";

    public static final String SUCCESS_CODE = "1";
    public static final String FAIL_CODE = "0";
    public static final String THIRD_USER_NOTEXIST = "2";//第三方用户不存在

    public static final String STR_ENCODING = "utf-8";
    public static final String TOKEN_SPLIT = "\\.";

    //返回错误码
    public static final String ERR_USER_EXIST = "201";//用户已存在
    public static final String ERR_VERIFY_PHONE_CODE = "202";//手机验证码
    public static final String ERR_VERIFY_IVITE_CODE = "203";//推荐人邀请码验证失败
    public static final String ERR_VERIFY_USER_ID_CODE = "204";//用户ID获取失败
    public static final String ERR_EXISTS_IVITE_CODE = "205";//推荐人邀请码已存在

    public static final String ERR_VERIFY_USER_PERMISSIONS = "400";//用户权限不足
    public static final String ERR_VERIFY_USER_TOKEN = "401";//用户登录失效

    public static final String ERR_SEX_CODE = "100";//性别码错误
    public static final String ERR_IMGURL_CODE = "101";//头像链接
    public static final String ERR_NICE_NAME_CODE = "102";//昵称值错误
    public static final String ERR_PHONE_NO_CODE = "103";//手机号错误
    public static final String ERR_EMAIL_CODE = "104";//邮箱错误
    public static final String ERR_USER_NAME_CODE = "105";//用户姓名错误
    public static final String ERR_BIR_DATE_CODE = "106";//生日
    public static final String ERR_CITY_CODE = "107";//城市
    public static final String ERR_COMPANY_CODE = "108";//公司
    public static final String ERR_POS_CODE = "109";//职位
    public static final String ERR_LOGIN_NAME = "110";//登录账号名
    public static final String ERR_LOGIN_PWD = "111";//密码输入有误
    public static final String ERR_NULL_CHANNEL = "112";//渠道编码不能为空

    public static final String HINT_IS_REGIST = "301";//用户已注册
    public static final String HINT_NOT_REGIST = "302";//用户还未注册
    public static final String ERR_USER_NOT_UNIQUE = "303";//用户不唯一

    public static final String ERR_COMMTENT_MINGAN = "501";//敏感词汇

    public static final String EXCEPTION_CODE = "999";//程序异常
    public static final String ERR_VIEW_CODE = "888";//前端必填字段为空

    public static final String info_female = "0";
    public static final String info_male = "1";
    public static final String info_others = "2";

    public static final String LOGIN_HASH_KEY = "loginRecords";
    public static final String LOGIN_STATS_HASH_KEY = "loginStats";

    public static final String ROLE_CODE_NORMAL = "normal";
    public static final String ROLE_CODE_VIP = "vip1";

    //活动编码
    public static final String PROFIT_S_CODE_INVITE = "a-inviter";
    public static final String PROFIT_S_CODE_FIRST = "a-first";

    public static String buildLoginChanelKey(String userid, String channel) {
        StringBuilder sb = new StringBuilder(userid);
        if (CHANNEL_ANDROID.equals(channel) || CHANNEL_IOS.equals(channel)) {
            channel = "appChannel";
        }else if(CHANNEL_PC.equals(channel)) {
            channel = "webChannel";
        }else {
            channel = "wapChannel";
        }
        return sb.append("|").append(channel).toString();
    }

    /**
     * 获奖等级:国家级
     */
    public static String PRIZE_TYPE_COUNTRY = "gjjhj";

    /**
     * 获奖等级:省级
     */
    public static String PRIZE_TYPE_PROVINCE = "sjhj";

    /**
     * 报告生成格式:pdf
     */
    public static String REPORT_PATTERN_PDF = "pdf";

    /**
     * 支付状态：查询状态
     */
    public static Integer PAY_STATUS_QUERY = 0;

    /**
     * 报告
     */
    public static String REPORT_CHANNEL_COMMON = "report_com";
    public static String REPORT_CHANNEL_VIP = "report_vip";

    public static Integer QUESTION_TYPE_RADIO = 1;
    public static Integer QUESTION_TYPE_CHECKBOX = 2;
    public static Integer QUESTION_TYPE_JUDGE = 3;
    public static Integer QUESTION_TYPE_CASE = 4;
    public static Integer QUESTION_TYPE_ANSWER = 5;
    public static Integer WORK_TYPE_ERROR = 1;

    /**
     * 评论状态
     */
    public static Integer COMMENT_STATES_PINGBI = 3;
}
