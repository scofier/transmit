package com.hebaibai.ctrt.transmit.util.request;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class GetQueryRequest implements Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.GET && dataType == DataType.QUERY;
    }

    @Override
    public void request(WebClient client, String param, String path, int timeout, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        HttpRequest<Buffer> request = null;
        if (param.endsWith("?")) {
            request = client.requestAbs(HttpMethod.GET, path + param);
        } else {
            request = client.requestAbs(HttpMethod.GET, path + "?" + param);
        }
        request.timeout(timeout).send(handler);

    }
}
