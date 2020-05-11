package org.furion.core.constants;

import io.netty.util.AsciiString;

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

    public static final AsciiString REQUEST_ID = AsciiString.cached("requestId");
    // 分隔符
    public static final String SEGMENTATION = ";";

    public static final String ROUTE = "route";

    public static final String HTTP_PREFIX = "http://";

    public static final String CONFIG_PATH = "/config";
    public static final String CONFIG_PATH_SYSTEM = "/config/system";
    public static final String CONFIG_PATH_FURION = "/config/route";
    public static final String CONFIG_PATH_FIELTER = "/config/filter";
    public static final String MONITOR_PATH = "/config/filter";


}
