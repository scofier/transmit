package com.hebaibai.ctrt.transmit.util.request;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

/**
 * @author hjx
 */
public class PostFormRequest implements Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.FROM;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        client.requestAbs(HttpMethod.POST, path)
                .putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded")
                .sendBuffer(Buffer.buffer(param), handler);
    }
}
