package com.hebaibai.ctrt;

import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * 启动器
 * @author hjx
 */
@Slf4j
public class Main {

    /**
     * 启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Config config = new Config();
        try {
            JSONObject jsonObject = JSONObject.parseObject(getConf(args));
            Integer port = jsonObject.getInteger("port");
            config.setPort(port);
            jsonObject.remove("port");
            log.info("init port: {}", port);
            //转发配置
            Set<String> keys = jsonObject.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String code = iterator.next();
                JSONObject transmitJson = jsonObject.getJSONObject(code);
                TransmitConfig transmitConfig = new TransmitConfig();
                transmitConfig.setCode(code);
                //request 配置
                JSONObject request = transmitJson.getJSONObject("request");
                transmitConfig.setReqPath(request.getString("path"));
                transmitConfig.setReqMethod(getHttpMethod(request));
                transmitConfig.setReqType(getRequestType(request));
                transmitConfig.setResType(getRequestType(request));
                //api配置
                JSONObject api = transmitJson.getJSONObject("api");
                transmitConfig.setApiPath(api.getString("url"));
                transmitConfig.setApiMethod(getHttpMethod(api));
                transmitConfig.setApiReqType(getRequestType(api));
                transmitConfig.setApiResType(getResponseType(api));
                transmitConfig.setApiReqFtlText(CrtrUtils.getFileText(api.getString("request-ftl")));
                transmitConfig.setApiResFtlText(CrtrUtils.getFileText(api.getString("response-ftl")));
                log.info("init transmitConfig: {}", transmitConfig);
                //加入配置
                config.put(transmitConfig);
            }
            log.info("init done ...");
            CtrtLancher ctrtLancher = new CtrtLancher();
            ctrtLancher.start(config);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 获取配置文件
     *
     * @param args
     * @return
     * @throws ParseException
     */
    private static String getConf(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("c", "conf", true, "config file path");
        CommandLine parse = new BasicParser().parse(options, args);
        if (!parse.hasOption("conf")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Options", options);
            System.exit(0);
        }
        String conf = parse.getOptionValue("conf");
        return CrtrUtils.getFileText(conf);
    }

    private static HttpMethod getHttpMethod(JSONObject request) {
        String method = request.getString("method");
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.GET) {
            return httpMethod;
        }
        throw new UnsupportedOperationException("请求方式不支持: " + method);
    }

    private static DataType getRequestType(JSONObject request) {
        String requestType = request.getString("request-type");
        DataType dataType = DataType.valueOf(requestType.toUpperCase());
        return dataType;
    }

    private static DataType getResponseType(JSONObject request) {
        String responseType = request.getString("response-type");
        DataType dataType = DataType.valueOf(responseType.toUpperCase());
        return dataType;
    }


}
