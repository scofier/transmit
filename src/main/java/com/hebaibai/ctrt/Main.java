package com.hebaibai.ctrt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.convert.reader.JsonDataReader;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.DataConfig;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

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
     * 配置
     */
    private static Config config = new Config();


    /**
     * 启动入口
     * 需要传入 -c 配置文件路径 参数
     * 例如 -c /home/config.json
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        //获取配置文件内容
        JSONObject jsonObject = getConf(args);

        //获取系统配置
        JSONObject configJson = jsonObject.getJSONObject("config");

        //获取prop
        if (configJson.containsKey("prop")) {
            String propJson = configJson.getString("prop");
            JsonDataReader dataReader = new JsonDataReader();
            dataReader.read(propJson);
            Object prop = dataReader.getRequestData().get(DataReader.ROOT_NAME);
            config.setProp(prop);
        }

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
        addTransmitConfig(jsonObject);

        //import加载
        importLoad(configJson);

        //配置日志数据库
        if (configJson.containsKey("db")) {
            DataConfig db = configJson.getObject("db", DataConfig.class);
            config.setDataConfig(db);
        }

        //启动
        CtrtLancher ctrtLancher = new CtrtLancher();
        ctrtLancher.start(config);
        for (TransmitConfig router : config.getRouters()) {
            log.info("mapping: {} {};", router.getReqMethod().name(), router.getReqPath());
        }
    }


    /**
     * 获取配置文件
     *
     * @param args
     * @return
     * @throws ParseException
     */
    private static JSONObject getConf(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("c", "conf", true, "config file path");
        CommandLine parse = new BasicParser().parse(options, args);
        if (!parse.hasOption("conf")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Options", options);
            System.exit(0);
        }
        String conf = parse.getOptionValue("conf");
        String congText = CrtrUtils.getFileText(conf);
        JSONObject jsonObject = JSONObject.parseObject(congText);
        return jsonObject;
    }

    /**
     * 获取配置 HttpMethod
     *
     * @param request
     * @return
     */
    private static HttpMethod getHttpMethod(JSONObject request) {
        String method = request.getString("method");
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.GET) {
            return httpMethod;
        }
        throw new UnsupportedOperationException("请求方式不支持: " + method);
    }

    /**
     * 获取配置 DataType
     *
     * @param request
     * @return
     */
    private static DataType getRequestType(JSONObject request) {
        String requestType = request.getString("request-type");
        DataType dataType = DataType.valueOf(requestType.toUpperCase());
        return dataType;
    }

    /**
     * 获取配置 DataType
     *
     * @param request
     * @return
     */
    private static DataType getResponseType(JSONObject request) {
        String responseType = request.getString("response-type");
        DataType dataType = DataType.valueOf(responseType.toUpperCase());
        return dataType;
    }

    /**
     * 加载 ext
     *
     * @param configJson
     */
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

    /**
     * 加载 import
     *
     * @param configJson
     * @throws IOException
     */
    private static void importLoad(JSONObject configJson) throws Exception {
        if (!configJson.containsKey("import")) {
            return;
        }
        JSONArray importJsons = configJson.getJSONArray("import");
        for (int i = 0; i < importJsons.size(); i++) {
            String importFilePath = importJsons.getString(i);
            log.info("load import file {}", importFilePath);
            String json = CrtrUtils.getFileText(getStringConfig(importFilePath));
            JSONObject jsonObject = JSONObject.parseObject(json);
            addTransmitConfig(jsonObject);
        }
    }

    /**
     * 添加 TransmitConfig
     *
     * @param configJson
     * @throws IOException
     */
    private static void addTransmitConfig(JSONObject configJson) throws Exception {
        Set<String> keys = configJson.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String code = iterator.next();
            JSONObject transmitJson = configJson.getJSONObject(code);
            TransmitConfig transmitConfig = getTransmitConfig(code, transmitJson);
            //加入配置
            config.put(transmitConfig);
        }
    }

    /**
     * 从配置中获取 TransmitConfig
     *
     * @param code
     * @param transmitJson
     * @return
     * @throws IOException
     */
    private static TransmitConfig getTransmitConfig(String code, JSONObject transmitJson) throws Exception {
        TransmitConfig transmitConfig = new TransmitConfig();
        transmitConfig.setCode(code);
        transmitConfig.setCache(config.isCache());
        //request 配置
        JSONObject request = transmitJson.getJSONObject("request");
        //配置中路径去重
        String path = new File(request.getString("path")).getPath();
        transmitConfig.setReqMethod(getHttpMethod(request));
        transmitConfig.setReqType(getRequestType(request));
        transmitConfig.setResType(getRequestType(request));
        transmitConfig.setReqPath(getStringConfig(path));
        //api配置
        JSONObject api = transmitJson.getJSONObject("api");
        transmitConfig.setApiPath(getStringConfig(api.getString("url")));
        transmitConfig.setExtCode(api.getString("extCode"));
        //接口调用超时时间， 默认3秒
        int timeout = 3000;
        if (api.containsKey("timeout")) {
            timeout = api.getIntValue("timeout");
        }
        transmitConfig.setTimeout(timeout);
        transmitConfig.setApiMethod(getHttpMethod(api));
        transmitConfig.setApiReqType(getRequestType(api));
        transmitConfig.setApiResType(getResponseType(api));
        //请求转换模板文件
        String requestFtlPath = getStringConfig(api.getString("request-ftl"));
        transmitConfig.setApiReqFtlPath(requestFtlPath);
        transmitConfig.setApiReqFtlText(CrtrUtils.getFileText(requestFtlPath));
        //响应转换模板文件
        String responseFtlPath = getStringConfig(api.getString("response-ftl"));
        transmitConfig.setApiResFtlPath(responseFtlPath);
        transmitConfig.setApiResFtlText(CrtrUtils.getFileText(responseFtlPath));
        return transmitConfig;
    }

    /**
     * 将prop转换进字符串配置中，使用freemarker
     * <p>
     * 转换的节点：
     * config.import
     * request.path
     * api.url
     * api.request-ftl
     * api.response-ftl
     *
     * @param value
     * @return
     * @throws Exception
     */
    private static String getStringConfig(String value) throws Exception {
        Object prop = config.getProp();
        if (prop == null) {
            return value;
        }
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String name = "[" + value + "]";
        String format = FreeMarkerUtils.format(prop, new FreeMarkerFtl() {{
            setTemplateText(value);
            setTemplateName(name);
        }});
        return format;
    }

}
