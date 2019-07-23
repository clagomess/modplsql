package com.github.clagomess.modplsql.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConfigDto {
    private String dbUrl;
    private String dbUser;
    private String dbPass;
    private List<Param> params;

    @Data
    public static class Param {
        String key;
        String value;
    }

    public Map<String, String> getParamsAsMap(){
        Map<String, String> map = new HashMap<>();

        if(params == null){
            return map;
        }

        for(Param param : params){
            map.put(param.getKey(), param.getValue());
        }

        return map;
    }
}
