package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.convert.reader.JsonDataReader;
import com.hebaibai.ctrt.convert.reader.XmlDataReader;
import com.hebaibai.ctrt.transmit.config.*;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

public class TransmitVerticle extends AbstractVerticle {

    private final static String enc = "utf-8";

    @Getter
    @Setter
    private Config config;

    private HttpServer httpServer;

    private WebClient webClient;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        webClient = WebClient.create(vertx);
    }

    @Override
    public void start() throws Exception {
        //监听http
        httpServer.requestHandler(request -> {
            //请求地址
            String uri = request.uri();
            //请求方法
            String method = request.method().toString();
            //映射关系
            ConvertMapping mapping = config.getMapping(uri, Enums.method(method));
            //响应
            HttpServerResponse response = request.response();
            request.bodyHandler(buffer -> {
                String requestBody = buffer.toString(enc);
                MultiMap params = request.params();
                //转换参数,并发起请求
                convertAndSend(params, requestBody, mapping, event -> {
                    if (event.failed()) {
                        response.end(errorMsg(event.cause()));
                        return;
                    }
                    //接口原始返回数据
                    String bodyAsString = event.result().bodyAsString(enc);
                    convertReturn(bodyAsString, mapping, result -> {
                        if (result.failed()) {
                            response.end(errorMsg(result.cause()));
                            return;
                        }
                        response.end(result.result());
                    });
                });
            });

        }).listen(config.getPort());
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

    }

    /**
     * 转换请求参数并转发请求
     *
     * @param params
     * @param body
     * @param mapping
     */
    private void convertAndSend(MultiMap params, String body, ConvertMapping mapping, Handler<AsyncResult<HttpResponse<Buffer>>> handler) {
        ConvertData source = mapping.getSource();
        ConvertData target = mapping.getTarget();
        DataType dataType = source.getDataType();
        //设置请求
        HttpRequest<Buffer> request = webClient.requestAbs(target.getMethod(), target.getPath());
        request.timeout(mapping.getTimeOut());
        //TODO: 表单形式/文本形式 原始信息直接请求
        if (source.getDataType() == DataType.from || source.getDataType() == DataType.text) {
            request.sendForm(params, res -> handler.handle(res));
        }
        // 处理 json/xml 类型数据
        else {
            DataReader reader = getReader(dataType);
            try {
                reader.read(body);
                Map<String, Object> requestData = reader.getRequestData();
                String val = fmt(source, requestData);
                request.putHeader("Content-Type", dataType.val());
                request.sendBuffer(Buffer.buffer(val), res -> handler.handle(res));
            } catch (Exception e) {
                e.printStackTrace();
                handler.handle(Future.failedFuture(e));
            }
        }
    }

    /**
     * 转换返回参数
     *
     * @param body
     * @param mapping
     * @param handler
     */
    private void convertReturn(String body, ConvertMapping mapping, Handler<AsyncResult<String>> handler) {
        ConvertData target = mapping.getTarget();
        DataType dataType = target.getDataType();
        DataReader reader = getReader(target.getDataType());
        if (dataType == DataType.from) {
            handler.handle(Future.succeededFuture(body));
        }
        //TODO: 文本形式 原始信息直接请求
        else if (dataType == DataType.text) {
            handler.handle(Future.succeededFuture(body));
        }
        try {
            reader.read(body);
            Map<String, Object> requestData = reader.getRequestData();
            String val = fmt(target, requestData);
            handler.handle(Future.succeededFuture(val));
        } catch (Exception e) {
            e.printStackTrace();
            handler.handle(Future.failedFuture(e));
        }
    }

    private String fmt(ConvertData data, Map<String, Object> requestData) throws Exception {
        String val = FreeMarkerUtils.format(requestData, new FreeMarkerFtl() {{
            File file = new File(data.getConvertFilePath());
            setTemplateName(file.getName());
            setTemplateText(data.getFtlText());
        }});
        return val;
    }

    private DataReader getReader(DataType dataType) {
        if (dataType == DataType.json) {
            return new JsonDataReader();
        } else if (dataType == DataType.xml) {
            return new XmlDataReader();
        } else {
            return null;
        }
    }

    String errorMsg(Throwable e) {
        if (e == null) {
            return "error";
        }
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {
            return message;
        }
        return e.getClass().getName();
    }
}
