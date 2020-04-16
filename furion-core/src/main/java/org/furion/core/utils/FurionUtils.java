package org.furion.core.utils;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Properties;

public class FurionUtils {



    public static boolean extractBooleanDefaultFalse(final Properties props,
                                                     final String key) {
        final String throttle = props.getProperty(key);
        if (StringUtils.isNotBlank(throttle)) {
            return throttle.trim().equalsIgnoreCase("true");
        }
        return false;
    }

    public static boolean extractBooleanDefaultTrue(final Properties props,
                                                    final String key) {
        final String throttle = props.getProperty(key);
        if (StringUtils.isNotBlank(throttle)) {
            return throttle.trim().equalsIgnoreCase("true");
        }
        return true;
    }

    public static int extractInt(final Properties props, final String key) {
        return extractInt(props, key, -1);
    }

    public static int extractInt(final Properties props, final String key, int defaultValue) {
        final String readThrottleString = props.getProperty(key);
        if (StringUtils.isNotBlank(readThrottleString) &&
                NumberUtils.isNumber(readThrottleString)) {
            return Integer.parseInt(readThrottleString);
        }
        return defaultValue;
    }

    public static boolean isCONNECT(HttpObject httpObject) {
        return httpObject instanceof HttpRequest
                && HttpMethod.CONNECT.equals(((HttpRequest) httpObject)
                .getMethod());
    }

}
