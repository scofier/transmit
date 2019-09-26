package com.hebaibai.ctrt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.DataConfig;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * 程序入口
 *
 * @author hjx
 */
@Slf4j
public class Main {

    /**
     * 启动入口
     * 需要传入 -c 配置文件路径 参数
     * 例如 -c /home/config.json
     *
     * @param args
     */
    public static void main(String[] args) {
        Config config = new Config();
        try {
            //获取配置文件内容
            JSONObject jsonObject = JSONObject.parseObject(getConf(args));
            //获取系统配置
            JSONObject configJson = jsonObject.getJSONObject("config");
            //插件加载
            extLoad(configJson);
            //获取系统端口配置
            Integer port = configJson.getInteger("port");
            log.info("init port: {}", port);
            config.setPort(port);
            //是否缓存模板
            config.setCache(true);
            if (configJson.containsKey("cache")) {
                boolean cache = configJson.getBoolean("cache");
                config.setCache(cache);
            }
            log.info("init cache: {}", config.isCache());
            //移出配置文件中的config节点
            jsonObject.remove("config");
            //转发配置
            addTransmitConfig(jsonObject, config);
            //import加载
            importLoad(configJson, config);
            //配置日志数据库
            DataConfig db = configJson.getObject("db", DataConfig.class);
            config.setDataConfig(db);
            //启动
            CtrtLancher ctrtLancher = new CtrtLancher();
            ctrtLancher.start(config);
            for (TransmitConfig router : config.getRouters()) {
                log.info("mapping: {} {};", router.getReqMethod().name(), router.getReqPath());
            }
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

    private static void extLoad(JSONObject configJson) {
        if (!configJson.containsKey("ext")) {
            return;
        }
        JSONArray ext = configJson.getJSONArray("ext");
        for (int i = 0; i < ext.size(); i++) {
            String extClassName = ext.getString(i);
            try {
                Class.forName(extClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        for (Ext extObj : CrtrUtils.EXT_LIST) {
            log.info("load ext code {}", extObj.getCode());
        }
    }

    private static void importLoad(JSONObject configJson, Config config) throws IOException {
        if (!configJson.containsKey("import")) {
            return;
        }
        JSONArray importJsons = configJson.getJSONArray("import");
        for (int i = 0; i < importJsons.size(); i++) {
            String importFilePath = importJsons.getString(i);
            log.info("load import file {}", importFilePath);
            String json = CrtrUtils.getFileText(importFilePath);
            JSONObject jsonObject = JSONObject.parseObject(json);
            addTransmitConfig(jsonObject, config);
        }
    }

    private static void addTransmitConfig(JSONObject configJson, Config config) throws IOException {
        Set<String> keys = configJson.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String code = iterator.next();
            JSONObject transmitJson = configJson.getJSONObject(code);
            TransmitConfig transmitConfig = getTransmitConfig(config, code, transmitJson);
            //加入配置
            config.put(transmitConfig);
        }
    }

    /**
     * 从配置中获取 TransmitConfig
     *
     * @param config
     * @param code
     * @param transmitJson
     * @return
     * @throws IOException
     */
    private static TransmitConfig getTransmitConfig(Config config, String code, JSONObject transmitJson) throws IOException {
        TransmitConfig transmitConfig = new TransmitConfig();
        transmitConfig.setCode(code);
        transmitConfig.setCache(config.isCache());
        //request 配置
        JSONObject request = transmitJson.getJSONObject("request");
        //配置中路径去重
        String path = request.getString("path");
        transmitConfig.setReqPath(new File(path).getPath());
        transmitConfig.setReqMethod(getHttpMethod(request));
        transmitConfig.setReqType(getRequestType(request));
        transmitConfig.setResType(getRequestType(request));
        //api配置
        JSONObject api = transmitJson.getJSONObject("api");
        transmitConfig.setApiPath(api.getString("url"));
        transmitConfig.setExtCode(api.getString("extCode"));
        //接口调用超时时间， 默认3秒
        Integer timeout = (Integer) api.getOrDefault("timeout", 3000);
        transmitConfig.setTimeout(timeout);
        transmitConfig.setApiMethod(getHttpMethod(api));
        transmitConfig.setApiReqType(getRequestType(api));
        transmitConfig.setApiResType(getResponseType(api));
        //请求转换模板文件
        String requestFtlPath = api.getString("request-ftl");
        transmitConfig.setApiReqFtlPath(requestFtlPath);
        transmitConfig.setApiReqFtlText(CrtrUtils.getFileText(requestFtlPath));
        //响应转换模板文件
        String responseFtlPath = api.getString("response-ftl");
        transmitConfig.setApiResFtlPath(responseFtlPath);
        transmitConfig.setApiResFtlText(CrtrUtils.getFileText(responseFtlPath));
        return transmitConfig;
    }

}
