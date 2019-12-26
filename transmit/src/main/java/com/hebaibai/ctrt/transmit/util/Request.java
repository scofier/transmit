package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

public interface Request {

    String CONTENT_TYPE = "Content-Type";

    boolean support(HttpMethod method, DataType dataType);

    /**
     * 发起请求
     *
     * @param client
     * @param header
     * @param param
     * @param path
     * @param timeout
     * @param handler
     */
    void request(
            WebClient client,
            Map<String, String> header,
            String param,
            String path,
            int timeout,
            Handler<AsyncResult<HttpResponse<Buffer>>> handler
    );

}
