package org.furion.core.config;

import java.util.HashMap;
import java.util.Map;

public class RuleConfig {
    private volatile Map<String/**服务名*/, Map<String/**类型*/,String/**条件表达式*/>> readRuleMap = new HashMap<>();
    private volatile Map<String/**服务名*/, Map<String,String>> writeRuleMap = new HashMap<>();

    public Map<String, Map<String, String>> getReadRuleMap() {
        return readRuleMap;
    }

//    public void setWriteRuleMap(Map<String, Map<String, String>> writeRuleMap) {
//        this.writeRuleMap = writeRuleMap;
//    }
    public void updateRule(){

    }

}
