package com.hebaibai.ctrt.transmit.util.request;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class GetRequest implements Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.GET && dataType == DataType.QUERY;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        client.getAbs(path).sendBuffer(Buffer.buffer(param), handler);
    }
}
