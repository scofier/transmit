package com.hebaibai.ctrt.transmit.config;

import io.vertx.core.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Config {

    @Getter
    @Setter
    private int port;

    /**
     * 请求方式 对应数据转换
     */
    private Map<String, ConvertMapping> convertMappings = new HashMap<>();

    /**
     * 添加对应关系
     *
     * @param source
     * @param target
     * @return
     */
    public boolean put(ConvertData source, ConvertData target) {
        ConvertMapping convertMapping = new ConvertMapping();
        convertMapping.setSource(source);
        convertMapping.setTarget(target);
        String key = key(source, target);
        if (convertMappings.containsKey(key)) {
            return false;
        }
        convertMappings.put(key, convertMapping);
        return true;
    }


    /**
     * 获取key
     *
     * @return
     */
    public String key(ConvertData source, ConvertData target) {
        return source.getMethod() + "::" + source.getPath();
    }

    /**
     * 获取映射
     *
     * @param uri
     * @param method
     * @return
     */
    public ConvertMapping getMapping(String uri, HttpMethod method) {
        String key = method.name() + "::" + uri;
        if (convertMappings.containsKey(key)) {
            return convertMappings.get(key);
        }
        return null;
    }

}
