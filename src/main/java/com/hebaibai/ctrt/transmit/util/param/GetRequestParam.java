package com.hebaibai.ctrt.transmit.util.param;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import com.hebaibai.ctrt.transmit.util.Param;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRequestParam implements Param {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.GET && dataType == DataType.QUERY;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Map<String, Object> params(RouterVo routerVo) {
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

}
