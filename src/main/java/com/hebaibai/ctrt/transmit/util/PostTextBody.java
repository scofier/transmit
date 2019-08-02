package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.HashMap;
import java.util.Map;

public class PostTextBody implements ParamGet, Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.TEXT;
    }

    @Override
    public Map<String, Object> get(RouterVo routerVo) {
        Map<String, Object> root = new HashMap();
        root.put(DataReader.ROOT_NAME, routerVo.getBody());
        return root;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {

    }
}
