package com.hebaibai.ctrt.transmit.verticle;

import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.RouterVo;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.hebaibai.ctrt.transmit.util.CrtrUtils.CHARSET_NAME;

/**
 * 请求转发Verticle
 *
 * @author hjx
 */
@Slf4j
public class TransmitVerticle extends AbstractVerticle {

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

    private EventBus eventBus;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        this.webClient = WebClient.create(vertx);
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(this::getRequestBody);
        //转换请求数据,并发送请求
        router.route().handler(this::convertAndRequest);
        //转换响应数据,并发返回
        router.route().handler(this::convertAndReturn);
        //开启路由
        httpServer.requestHandler(router).listen(config.getPort());
        //事件总线
        eventBus = vertx.eventBus();
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
    private void getRequestBody(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpMethod method = request.method();
        String path = request.path();
        TransmitConfig transmitConfig = config.get(method, path);
        //没有找到配置
        if (transmitConfig == null) {
            routingContext.response().end(error(new RuntimeException("not find config")));
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
            routerVo.setTypeCode(transmitConfig.getCode());
            //保存请求记录
            eventBus.send(DataBaseVerticle.EXECUTE_SQL_INSERT, routerVo.getInsertJsonStr());
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
        if (param == null || convert == null) {
            routingContext.response().end(error(new RuntimeException("config error")));
            return;
        }
        Map<String, Object> map = param.params(routerVo);
        log.info("request {} befor:\n {}", routerVo.getUuid(), map);
        String value = null;
        try {
            //转换请求参数,使其符合目标接口
            value = convert.convert(map, transmitConfig.getApiReqFtlText(), transmitConfig.getCode() + "REQ");
            //签名
            Sign sign = CrtrUtils.sign(transmitConfig.getSignCode());
            value = sign.sign(value);
            log.info("request {} after:\n {}", routerVo.getUuid(), value);
        } catch (Exception e) {
            log.error("request {} error:\n {}", routerVo.getUuid(), e);
            routingContext.response().end(error(e));
            return;
        }
        //转发数据
        Request request = CrtrUtils.request(transmitConfig.getApiMethod(), transmitConfig.getApiReqType());
        request.request(webClient, value, transmitConfig.getApiPath(), transmitConfig.getTimeout(), event -> {
            if (!event.succeeded()) {
                routingContext.response().end(error(event.cause()));
            } else {
                //响应数据
                String body = event.result().bodyAsString(CHARSET_NAME);
                //更新body中的值, 设置为接口返回值
                routerVo.setBody(body);
                //保存接口返回参数
                eventBus.send(DataBaseVerticle.EXECUTE_SQL_UPDATE, routerVo.getUpdateJsonStr());
                routingContext.next();
            }
            return;
        });

    }

    /**
     * 转换响应数据,并发返回
     *
     * @param routingContext
     */
    private void convertAndReturn(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        log.info("response {} befor:\n {}", routerVo.getUuid(), routerVo.getBody());
        try {
            //验签
            Sign sign = CrtrUtils.sign(transmitConfig.getSignCode());
            boolean verify = sign.verify(routerVo.getBody());
            //取响应参数 和 转换 按照Post形式(从 body 中解析)
            Param param = CrtrUtils.param(HttpMethod.POST, transmitConfig.getApiResType());
            Convert convert = CrtrUtils.convert(transmitConfig.getApiResType(), transmitConfig.getResType());
            if (param == null || convert == null) {
                routingContext.response().end(error(new RuntimeException("config error")));
                return;
            }
            Map<String, Object> map = param.params(routerVo);
            //在参数中添加签名是否验证成功的标示, 以便在模板文件中查看
            map.put(VERIFY_KEY, verify ? VERIFY_SUCCESS : VERIFY_ERROR);
            String value = convert.convert(map, transmitConfig.getApiResFtlText(), transmitConfig.getCode() + "RES");
            log.info("response {} after:\n {}", routerVo.getUuid(), value);
            //返回响应结果
            routingContext.response().end(value);
        } catch (Exception e) {
            log.error("request {} error:\n {}", routerVo.getUuid(), e);
            routingContext.response().end(error(e));
        }
    }

    /**
     * 获取异常中的信息
     * @param throwable
     * @return
     */
    private String error(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"error_code\": \"0\", \"error_msg\": \"");
        if (throwable == null) {
            builder.append("Internal Server Error");
        } else {
            builder.append(throwable.getMessage());
        }
        builder.append("\" }");
        return builder.toString();
    }

}
