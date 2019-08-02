package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import com.hebaibai.ctrt.convert.reader.XmlDataReader;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.Map;

public class PostXmlBody implements ParamGet, Request, Convert {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.XML;
    }

    @Override
    public Map<String, Object> get(RouterVo routerVo) {
        XmlDataReader dataReader = new XmlDataReader();
        String requestBody = routerVo.getBody();
        dataReader.read(requestBody);
        return dataReader.getRequestData();
    }

    @Override
    public String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception {
        String format = FreeMarkerUtils.format(objectMap, new FreeMarkerFtl() {{
            setTemplateName(ftlName);
            setTemplateText(ftl);
        }});
        return format;
    }

    @Override
    public void request(WebClient client, String param, String path, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {

    }
}
