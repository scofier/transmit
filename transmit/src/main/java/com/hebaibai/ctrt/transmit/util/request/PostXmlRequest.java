package com.hebaibai.ctrt.transmit.util.request;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

public class PostXmlRequest implements Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.XML;
    }

    @Override
    public void request(
            WebClient client,
            Map<String, String> headers,
            String param,
            String path,
            int timeout,
            Handler<AsyncResult<HttpResponse<Buffer>>> handler
    ) {
        VertxHttpHeaders httpHeaders = new VertxHttpHeaders();
        httpHeaders.addAll(headers);
        client.requestAbs(HttpMethod.POST, path)
                .putHeader(CONTENT_TYPE, "application/xml")
                .putHeaders(httpHeaders)
                .timeout(timeout)
                .sendBuffer(Buffer.buffer(param), handler);
    }

}
