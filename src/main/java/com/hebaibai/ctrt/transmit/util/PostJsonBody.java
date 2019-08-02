package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import com.hebaibai.ctrt.convert.reader.JsonDataReader;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

public class PostJsonBody implements ParamGet, Request, Convert {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.JSON;
    }

    @Override
    public Map<String, Object> get(RouterVo routerVo) {
        JsonDataReader dataReader = new JsonDataReader();
        String requestBody = routerVo.getBody();
        dataReader.read(requestBody);
        return dataReader.getRequestData();
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        client.requestAbs(HttpMethod.POST, path)
                .putHeader(CONTENT_TYPE, "application/json")
                .sendBuffer(Buffer.buffer(param), handler);
    }

    @Override
    public String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception {
        String format = FreeMarkerUtils.format(objectMap, new FreeMarkerFtl() {{
            setTemplateName(ftlName);
            setTemplateText(ftl);
        }});
        return format;
    }
}
