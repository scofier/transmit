package com.hebaibai.ctrt.transmit.util.request;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * @author hjx
 */
public class PostFormRequest implements Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.FORM;
    }

    /**
     * @param client
     * @param param   key1=value1
     *                key2=value2
     *                ...
     * @param path
     * @param timeout
     * @param handler
     */
    @Override
    public void request(WebClient client, String param, String path, int timeout, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        Scanner scanner = new Scanner(param);
        MultiMap body = MultiMap.caseInsensitiveMultiMap();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String[] split = line.split("=");
            String key = split[0].trim();
            if (split.length == 2) {
                String value = split[1].trim();
                body.add(key, value);
            } else {
                body.add(key, "");
            }
        }
        client.requestAbs(HttpMethod.POST, path)
                .putHeader(CONTENT_TYPE, "application/x-www-form-urlencoded")
                .timeout(timeout)
                .sendForm(body, handler);
    }
}
