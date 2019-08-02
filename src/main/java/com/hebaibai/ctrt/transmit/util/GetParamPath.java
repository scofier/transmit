package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetParamPath implements ParamGet, Request, Convert {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.GET && dataType == DataType.QUERY;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Map<String, Object> get(RouterVo routerVo) {
        Map<String, Object> map = new HashMap<>();
        MultiMap params = routerVo.getParams();
        List<Map.Entry<String, String>> entries = params.entries();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            map.put(key, value);
        }
        return map;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {

    }

    @Override
    public String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception {
        return null;
    }
}
