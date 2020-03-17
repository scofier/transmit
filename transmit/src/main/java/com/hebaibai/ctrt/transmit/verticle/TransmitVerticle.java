package com.hebaibai.ctrt.transmit.verticle;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.RouterVo;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.Convert;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.Param;
import com.hebaibai.ctrt.transmit.util.Request;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

import static com.hebaibai.ctrt.transmit.util.CrtrUtils.CHARSET_NAME;

/**
 * 请求转发Verticle
 *
 * @author hjx
 */
@Slf4j
public class TransmitVerticle extends AbstractVerticle {


    @Getter
    @Setter
    private Config config;

    private HttpServer httpServer;

    private WebClient webClient;

    private EventBus eventBus;

    private WorkerExecutor extApiWorker;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        //适配https
        HttpClientOptions httpOptions = new HttpClientOptions();
        httpOptions.setSsl(true).setVerifyHost(false).setTrustAll(true);
        HttpClient httpClient = vertx.createHttpClient(httpOptions);
        this.webClient = WebClient.wrap(httpClient);
        extApiWorker = vertx.createSharedWorkerExecutor("ext-api-worker-executor");
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(this::requestBody);
        //处理数据 或者 返回页面并结束
        router.route().handler(this::processData);
        //请求数据
        router.route().handler(this::request);
        //转换响应数据,并发返回
        router.route().handler(this::convertAndReturn);
        //开启路由
        httpServer.requestHandler(router).listen(config.getPort());
        //事件总线
        eventBus = vertx.eventBus();
    }

    @Override
    public void stop() {
    }

    /**
     * 获取转换所需要的数据
     *
     * @param routingContext
     */
    private void requestBody(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpMethod method = request.method();
        //去除重复的'/'符号
        String path = new File(request.path()).getPath();
        TransmitConfig transmitConfig = config.get(method, path);
        //没有找到配置
        if (transmitConfig == null) {
            routingContext.response().end(error("not find config"), CHARSET_NAME);
            return;
        }
        //接受请求的参数
        Param param = CrtrUtils.param(method, transmitConfig.getReqType());
        if (param == null) {
            routingContext.response().end(error("not find param util"), CHARSET_NAME);
            return;
        }
        Ext ext = transmitConfig.getExt();
        request.bodyHandler(event -> {
            String requestBody = event.toString(CHARSET_NAME);
            RouterVo routerVo = new RouterVo();
            routerVo.setTransmitConfig(transmitConfig);
            routerVo.setMethod(method);
            routerVo.setParams(request.params());
            routerVo.setBody(requestBody);
            routerVo.setPath(path);
            routerVo.setTypeCode(transmitConfig.getCode());
            log.info("request {} befor:\n{}", routerVo.getUuid(), requestBody);
            //API类型保存请求记录
            if (transmitConfig.getConfigType() == TransmitConfig.ConfigType.API) {
                eventBus.send(DataBaseVerticle.EXECUTE_SQL_INSERT, routerVo.getInsertJsonStr());
            }
            try {
                Map<String, Object> requestMap = param.params(routerVo);
                log.info("request {} requestMap:\n{}", routerVo.getUuid(), requestMap);
                routerVo.setRequestMap(requestMap);
                //插件,获取请求体后,转换参数格式前
                ext.beforRequestConvert(routerVo.getBody(), requestMap);
            } catch (Exception e) {
                log.error("request " + routerVo.getUuid() + " error", e);
                routingContext.response().end(error(e), CHARSET_NAME);
                return;
            }
            routingContext.put(RouterVo.class.getName(), routerVo);
            routingContext.next();
        });
    }

    /**
     * 转换请求数据
     *
     * @param routingContext
     */
    private void processData(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        Ext ext = transmitConfig.getExt();
        //原始请求中的参数
        Map<String, Object> requestMap = routerVo.getRequestMap();
        Convert convert = CrtrUtils.convert(transmitConfig.getReqType(), transmitConfig.getApiReqType());
        if (convert == null) {
            routingContext.response().end(error("not find convert util"), CHARSET_NAME);
            return;
        }
        //如果是返回TEXT类型, 直接直接执行下一步
        if (transmitConfig.getConfigType() == TransmitConfig.ConfigType.TEXT) {
            routingContext.next();
            return;
        }
        try {
            //执行数据转换,使其符合目标接口
            String apiReqFtlText = transmitConfig.getApiReqFtlText();
            log.debug("request {} ftl:\n{}", routerVo.getUuid(), apiReqFtlText);
            String value = convert.convert(requestMap, apiReqFtlText, transmitConfig.getCode() + "-REQ");
            //插件, 数据转换后, 请求接口前
            value = ext.beforRequest(value, requestMap);
            log.info("request {} after:\n{}", routerVo.getUuid(), value);
            //更新body
            routerVo.setBody(value);
            routingContext.put(RouterVo.class.getName(), routerVo);
            routingContext.next();
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
        }
    }

    /**
     * 发送请求
     *
     * @param routingContext
     */
    private void request(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        //如果是返回TEXT类型, 直接直接执行下一步
        if (transmitConfig.getConfigType() == TransmitConfig.ConfigType.TEXT) {
            routingContext.next();
            return;
        }
        String value = routerVo.getBody();
        //转发数据
        Request request = CrtrUtils.request(transmitConfig.getApiMethod(), transmitConfig.getApiReqType());
        if (request == null) {
            routingContext.response().end(error("not find request util"), CHARSET_NAME);
            return;
        }
        Ext ext = transmitConfig.getExt();
        try {
            //请求相应结果处理器
            Handler<AsyncResult<String>> handler = asyncResult -> {
                if (!asyncResult.succeeded()) {
                    routingContext.response().end(error(asyncResult.cause()), CHARSET_NAME);
                }
                //响应数据
                else {
                    String body = asyncResult.result();
                    if (StringUtils.isBlank(body)) {
                        log.error("request " + routerVo.getUuid() + " error no response body");
                        routingContext.response().end(error("no response body"), CHARSET_NAME);
                    } else {
                        //更新body中的值, 设置为接口返回值
                        routerVo.setBody(body);
                        //保存接口返回参数
                        eventBus.send(DataBaseVerticle.EXECUTE_SQL_UPDATE, routerVo.getUpdateJsonStr());
                        routingContext.next();
                    }
                }
            };
            Handler<Promise<String>> apiResult = ext.getApiResult(value);
            if (apiResult != null) {
                extApiWorker.executeBlocking(apiResult, handler);
            } else {
                request.request(webClient, transmitConfig, value, handler);
            }
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
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
        log.info("response {} befor:\n{}", routerVo.getUuid(), routerVo.getBody());
        Param param = null;
        if (transmitConfig.getConfigType() == TransmitConfig.ConfigType.API) {
            //API方式, 以POST方式, 和配置中的接口返回类型中获取参数
            param = CrtrUtils.param(HttpMethod.POST, transmitConfig.getApiResType());
        } else {
            //TEXT方式, 以配置中的原始请求方式, 和请求类型获取参数
            param = CrtrUtils.param(transmitConfig.getReqMethod(), transmitConfig.getReqType());
        }
        if (param == null) {
            routingContext.response().end(error("not find param util"), CHARSET_NAME);
            return;
        }
        Convert convert = CrtrUtils.convert(transmitConfig.getApiResType(), transmitConfig.getResType());
        if (convert == null) {
            routingContext.response().end(error("not find convert util"), CHARSET_NAME);
            return;
        }
        Ext ext = transmitConfig.getExt();
        try {
            //原始请求参数
            Map<String, Object> requestMap = routerVo.getRequestMap();
            //原始请求结果
            Map<String, Object> responseMap = param.params(routerVo);
            //组合参数
            responseMap.put("REQUEST", requestMap.get(DataReader.ROOT_NAME));
            //插件, 请求接口后, 转换响应前
            ext.afterResponse(routerVo.getBody(), responseMap);
            String apiResFtlText = transmitConfig.getApiResFtlText();
            log.debug("response {} ftl:\n{}", routerVo.getUuid(), apiResFtlText);
            String value = convert.convert(responseMap, apiResFtlText, transmitConfig.getCode() + "-RES");
            log.info("response {} after:\n{}", routerVo.getUuid(), value);
            //返回响应结果
            String ContentType = transmitConfig.getResType().val();
            routingContext.response().putHeader("Content-Type", ContentType);
            routingContext.response().end(value, CHARSET_NAME);
        } catch (Exception e) {
            log.error("response " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
        }
    }

    /**
     * 返回异常信息
     *
     * @param msg
     * @return
     */
    private String error(String msg) {
        return error(new RuntimeException(msg));
    }

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
