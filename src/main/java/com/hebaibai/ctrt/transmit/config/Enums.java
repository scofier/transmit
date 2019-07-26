package com.hebaibai.ctrt.transmit.config;

import io.vertx.core.http.HttpMethod;

public class Enums {

    public static DataType dataType(String dataType) {
        if (dataType.equalsIgnoreCase(DataType.from.name())) {
            return DataType.from;
        }
        if (dataType.equalsIgnoreCase(DataType.json.name())) {
            return DataType.json;
        }
        if (dataType.equalsIgnoreCase(DataType.xml.name())) {
            return DataType.xml;
        }
        throw new UnsupportedOperationException("不支持的参数:" + dataType);
    }

    public static HttpMethod method(String method) {
        if (method.equalsIgnoreCase(HttpMethod.GET.name())) {
            return HttpMethod.GET;
        }
        if (method.equalsIgnoreCase(HttpMethod.POST.name())) {
            return HttpMethod.POST;
        }
        throw new UnsupportedOperationException("不支持的参数:" + method);
    }
}
