package com.hebaibai.ctrt.transmit.router;

import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.client.HttpRequest;

import java.io.File;
import java.util.Map;

/**
 * 从post请求中获取请求体,并以post形式转发
 * <p>
 * 说明:
 * <p>
 * 1: 校验请求格式           checkContentType()
 * 1: 从请求体中获取数据     readRequest()
 * 2: 根据模板转换请求数据
 * 3: 转发转换后的数据       请求格式为 relayContentType()方法返回值
 * 3: 读取接口返回的数据     readResponse()
 * 4: 根据模板转换数据
 * 5: 返回
 */
public abstract class AbstractPostBodyTransmitRouter extends AbstractTransmitRouter {

    @Override
    public void init(Vertx vertx) {
        super.init(vertx);
        this.reqMethod = HttpMethod.POST;
        this.relayMethod = HttpMethod.POST;
    }

    @Override
    public void transmit(HttpServerRequest request, HttpServerResponse response) {
        String requestFileText = super.reqFileText;
        File requestFile = super.reqFile;

        Handler<Buffer> bodyHandler = (event) -> {
            try {
                //获取请求参数
                String body = event.toString(CHARSET_NAME);
                Map<String, Object> requestData = readRequest(body);
                //转换请求参数
                String requestString = FreeMarkerUtils.format(requestData, new FreeMarkerFtl() {{
                    setTemplateText(requestFileText);
                    setTemplateName(requestFile.getName());
                }});
                //发送请求参数
                HttpRequest<Buffer> relayRequest = webClient.requestAbs(relayMethod, relayPath);
                relayRequest.putHeader(CONTENT_TYPE, relayContentType());
                relayRequest.sendBuffer(Buffer.buffer(requestString), res -> {
                    if (!res.succeeded()) {
                        response.end(errorMsg(res.cause()), CHARSET_NAME);
                        return;
                    }
                    String value = res.result().bodyAsString(CHARSET_NAME);
                    try {
                        //转换接口返回参数
                        Map<String, Object> reponseData = readResponse(value);
                        String responseString = FreeMarkerUtils.format(reponseData, new FreeMarkerFtl() {{
                            setTemplateText(relayFileText);
                            setTemplateName(relayFile.getName());
                        }});
                        //返回响应参数
                        response.end(responseString, CHARSET_NAME);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.end(e.getMessage(), CHARSET_NAME);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                response.end(e.getMessage(), CHARSET_NAME);
            }

        };
        String header = request.getHeader(CONTENT_TYPE);
        if (checkContentType(header)) {
            request.bodyHandler(bodyHandler);
            return;
        }
        response.end("Content-Type error", CHARSET_NAME);
    }

    abstract boolean checkContentType(String header);

    abstract String relayContentType();

    abstract Map<String, Object> readRequest(String body) throws Exception;

    abstract Map<String, Object> readResponse(String value) throws Exception;

}
