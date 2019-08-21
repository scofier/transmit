package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.util.*;
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

import static com.hebaibai.ctrt.transmit.util.CrtrUtils.CHARSET_NAME;

public class TransmitVerticle extends AbstractVerticle {

    /**
     * 路由上下文中的, 接口相应数据
     */
    private static final String RETURN_KEY = "return_key";


    /**
     * 接口响应参数中, 验签是否成功的KEY
     */
    private static final String VERIFY_KEY = "VERIFY";

    /**
     * 验签 成功
     */
    private static final int VERIFY_SUCCESS = 1;

    /**
     * 验签 失败
     */
    private static final int VERIFY_ERROR = 0;


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
        //开启路由
        httpServer.requestHandler(router).listen(config.getPort());

        log.info("start success");

    }


    @Override
    public void stop() {
        log.info("stop success");
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
        Param param = CrtrUtils.param(routerVo.getMethod(), transmitConfig.getReqType());
        Convert convert = CrtrUtils.convert(transmitConfig.getReqType(), transmitConfig.getApiReqType());
        Map<String, Object> map = param.params(routerVo);
        log.info("request {} befor: {}", routerVo.getUuid(), map);
        try {
            //转换请求参数,使其符合目标接口
            String value = convert.convert(map, transmitConfig.getApiReqFtlText(), transmitConfig.getCode() + "REQ");
            //签名
            Sign sign = CrtrUtils.sign(transmitConfig.getSignCode());
            value = sign.sign(value);
            log.info("request {} after: {}", routerVo.getUuid(), value);
            //转发数据
            Request request = CrtrUtils.request(transmitConfig.getApiMethod(), transmitConfig.getApiReqType());
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
        Param param = CrtrUtils.param(HttpMethod.POST, transmitConfig.getApiResType());
        Convert convert = CrtrUtils.convert(transmitConfig.getApiResType(), transmitConfig.getResType());
        Map<String, Object> map = param.params(routerVo);
        //验签
        Sign sign = CrtrUtils.sign(transmitConfig.getSignCode());
        boolean verify = sign.verify(map);
        //在参数中添加签名是否验证成功的标示, 以便在模板文件中查看
        map.put(VERIFY_KEY, verify ? VERIFY_SUCCESS : VERIFY_ERROR);
        log.info("response {} befor: {}", routerVo.getUuid(), map);
        try {
            String value = convert.convert(map, transmitConfig.getApiResFtlText(), transmitConfig.getCode() + "RES");
            log.info("response {} after: {}", routerVo.getUuid(), value);
            //返回响应结果
            routingContext.response().end(value);
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error: {}", e);
            routingContext.fail(e);
        }
    }


}
