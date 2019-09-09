package com.hebaibai.ctrt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.DataConfig;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.sign.BaseSign;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * 启动器
 *
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
            //获取配置文件内容
            JSONObject jsonObject = JSONObject.parseObject(getConf(args));
            //获取系统配置
            JSONObject configJson = jsonObject.getJSONObject("config");
            //插件加载
            extLoad(configJson);
            //获取系统端口配置
            Integer port = configJson.getInteger("port");
            config.setPort(port);
            //移出配置文件中的config节点
            jsonObject.remove("config");
            log.info("init port: {}", port);
            //配置日志数据库
            DataConfig db = configJson.getObject("db", DataConfig.class);
            config.setDataConfig(db);
            //转发配置
            Set<String> keys = jsonObject.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String code = iterator.next();
                JSONObject transmitJson = jsonObject.getJSONObject(code);
                TransmitConfig transmitConfig = getTransmitConfig(code, transmitJson);
                //加入配置
                config.put(transmitConfig);
            }
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

    private static void extLoad(JSONObject configJson) {
        if (!configJson.containsKey("ext")) {
            return;
        }
        JSONArray ext = configJson.getJSONArray("ext");
        for (int i = 0; i < ext.size(); i++) {
            String extClassName = ext.getString(i);
            log.info("load ext class {}", extClassName);
            try {
                Class.forName(extClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
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
    private static TransmitConfig getTransmitConfig(String code, JSONObject transmitJson) throws IOException {
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
        transmitConfig.setSignCode(api.getString("signCode"));
        //接口调用超时时间， 默认3秒
        Integer timeout = (Integer) api.getOrDefault("timeout", 3000);
        transmitConfig.setTimeout(timeout);
        transmitConfig.setApiMethod(getHttpMethod(api));
        transmitConfig.setApiReqType(getRequestType(api));
        transmitConfig.setApiResType(getResponseType(api));
        transmitConfig.setApiReqFtlText(CrtrUtils.getFileText(api.getString("request-ftl")));
        transmitConfig.setApiResFtlText(CrtrUtils.getFileText(api.getString("response-ftl")));
        return transmitConfig;
    }

}
