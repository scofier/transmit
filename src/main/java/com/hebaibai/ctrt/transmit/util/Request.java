package com.hebaibai.ctrt.transmit.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public interface Request extends Support {

    String CONTENT_TYPE = "Content-Type";

    /**
     * 发起请求
     *  @param client
     * @param param
     * @param path
     * @param handler
     */
    void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler);

}
