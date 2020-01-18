package org.furion.core.constants;

/**
 * 公用常量类。
 *
 * @author LEO
 * @since 1.0, Sep 12, 2009
 */
public abstract class Constants {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIMEZONE = "GMT+8";

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String CHARSET_GBK = "GBK";

    public static final String FORMAT_JSON = "json";

    public static final String FORMAT_XML = "xml";

    // 签名
    public static final String SECRET = "12345qwer!@#";

    public static final String TOKEN = "X-TOKEN";

    public static final String PROP = "x-prop";

    // request中的key : userId
    public static final String USER_ID = "userId";

    // 分隔符
    public static final String SEGMENTATION = ",";

    /** session中存放的 图片验证码key */
    public static final String IMAGECODE = "IMAGECODE";

    /** session中存放的手机验证码key */
    public static final String PHONECODE = "PHONECODE";

    /** session中存放的手机验证码 时效性key */
    public static final String PHONESTATUS = "PHONESTATUS";

    /** session中存放的手机号码key */
    public static final String PHONE = "PHONE";

}
