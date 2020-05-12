package org.furion.admin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJson(T object){
        try {
            return objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            System.out.println("object->json error"+e);
        }
        return null;
    }

}
