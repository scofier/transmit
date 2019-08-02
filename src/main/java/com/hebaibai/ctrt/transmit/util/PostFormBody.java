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
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hjx
 */
public class PostFormBody implements ParamGet, Request {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.FROM;
    }

    @Override
    public Map<String, Object> get(RouterVo routerVo) {
        Map<String, Object> root = new HashMap();
        Map<String, Object> params = new HashMap();
        root.put(DataReader.ROOT_NAME, params);
        String requestBody = routerVo.getBody();
        if (StringUtils.isBlank(requestBody)) {
            return root;
        }
        String[] split = requestBody.split("&");
        for (String nameParam : split) {
            String[] p = nameParam.split("=");
            if (p.length == 2) {
                params.put(p[0], p[1]);
            } else {
                params.put(p[0], null);
            }
        }
        return root;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {

    }


}
