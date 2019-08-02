package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.util.Convert;
import com.hebaibai.ctrt.transmit.util.CrtrFactory;
import com.hebaibai.ctrt.transmit.util.ParamGet;
import com.hebaibai.ctrt.transmit.util.Request;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class TransmitVerticle extends AbstractVerticle {

    public static final String CHARSET_NAME = "utf-8";

    private static final String RETURN_KEY = "return_key";


    @Getter
    @Setter
    private Config config;

    private HttpServer httpServer;

    private WebClient webClient;

    private LogDelegate log;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        this.webClient = WebClient.create(vertx);
        log = new SLF4JLogDelegateFactory().createDelegate(TransmitVerticle.class.getName());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        //获取所需的所有参数和配置
        router.route().handler(this::startConvert);
        //转换请求数据,并发送请求
        router.route().handler(this::convertAndRequest);
        //转换响应数据,并发返回
        router.route().handler(this::convertAndReturn);

        httpServer.requestHandler(router).listen(config.getPort());
    }


    @Override
    public void stop() {

    }

    /**
     * 获取转换所需要的数据
     *
     * @param routingContext
     */
    private void startConvert(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpMethod method = request.method();
        String path = request.path();
        TransmitConfig transmitConfig = config.get(method, path);
        //没有找到配置
        if (transmitConfig == null) {
            routingContext.fail(404);
            return;
        }
        request.bodyHandler(event -> {
            String requestBody = event.toString(CHARSET_NAME);
            RouterVo routerVo = new RouterVo();
            routerVo.setTransmitConfig(transmitConfig);
            routerVo.setMethod(method);
            routerVo.setParams(request.params());
            routerVo.setBody(requestBody);
            routerVo.setPath(path);
            routingContext.put(RouterVo.class.getName(), routerVo);
            routingContext.next();
        });
    }

    /**
     * 转换请求数据
     *
     * @param routingContext
     */
    private void convertAndRequest(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        //接受请求的参数
        ParamGet paramGet = CrtrFactory.paramGet(routerVo.getMethod(), transmitConfig.getReqType());
        Map<String, Object> map = paramGet.get(routerVo);
        log.info("request {} befor: {}", routerVo.getUuid(), map);
        try {
            //转换请求参数,使其符合目标接口
            Convert convert = CrtrFactory.convert(routerVo.getMethod(), transmitConfig.getApiReqType());
            String value = convert.convert(map, transmitConfig.getApiReqFtlText(), transmitConfig.getApiReqFtl().getName());
            log.info("request {} after: {}", routerVo.getUuid(), value);
            //转发数据
            Request request = CrtrFactory.request(transmitConfig.getApiMethod(), transmitConfig.getApiReqType());
            request.request(webClient, value, transmitConfig.getApiPath(), event -> {
                if (!event.succeeded()) {
                    routingContext.response().end(event.cause().getMessage());
                    return;
                }
                //响应数据
                String body = event.result().bodyAsString(CHARSET_NAME);
                routingContext.put(RETURN_KEY, body);
                routingContext.next();
            });
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error: {}", e);
            routingContext.fail(e);
        }
    }

    /**
     * 转换响应数据,并发返回
     *
     * @param routingContext
     */
    private void convertAndReturn(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        String resBody = routingContext.get(RETURN_KEY);
        //更新body中的值
        routerVo.setBody(resBody);
        //取响应参数 和 转换 按照Post形式(从 body 中解析)
        ParamGet paramGet = CrtrFactory.paramGet(HttpMethod.POST, transmitConfig.getApiResType());
        Convert reqConvert = CrtrFactory.convert(HttpMethod.POST, transmitConfig.getResType());
        Map<String, Object> map = paramGet.get(routerVo);
        log.info("response {} befor: {}", routerVo.getUuid(), map);
        try {
            String value = reqConvert.convert(map, transmitConfig.getApiResFtlText(), transmitConfig.getApiResFtl().getName());
            log.info("response {} after: {}", routerVo.getUuid(), value);
            //返回响应结果
            routingContext.response().end(value);
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error: {}", e);
            routingContext.fail(e);
        }
    }


}
