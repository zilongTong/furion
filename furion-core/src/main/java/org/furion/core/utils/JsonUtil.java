package org.furion.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;

public class JsonUtil {
    static ThreadLocal<ObjectMapper> objectMapper = new ThreadLocal<ObjectMapper>() {
        @Override
        protected ObjectMapper initialValue() {
            return new ObjectMapper();
        }
    };

    public static <T> T getObject(byte[] src, Class<T> clazz) {
        try {
            return objectMapper.get().readValue(src, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getObject(String str, Class<T> clazz) {
        try {
            return objectMapper.get().readValue(str, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


