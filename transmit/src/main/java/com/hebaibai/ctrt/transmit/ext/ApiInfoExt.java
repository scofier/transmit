package com.hebaibai.ctrt.transmit.ext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

import java.util.Map;
import java.util.Set;

/**
 * 项目配置展示接口
 */
public final class ApiInfoExt implements Ext {

    private Vertx vertx;

    private Map<String, Object> transmitJson;

    private FileSystem fileSystem;

    @Override
    public void init(Vertx vertx, Map<String, Object> transmitJson) {
        this.vertx = vertx;
        this.transmitJson = transmitJson;
        this.fileSystem = vertx.fileSystem();

    }

    /**
     * 默认原样返回
     *
     * @param value    请求体中的数据
     * @param valueMap 原始数据经过解析后得到的map(放进freemarker的数据)
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> outRequestBodyMap(String value, Map<String, Object> valueMap) throws Exception {
        return valueMap;
    }

    /**
     * 默认原样返回
     *
     * @param value
     * @param valueMap:{ ROOT:     api接口返回数据
     *                   REQUEST:  第三方系统的请求数据
     *                   }
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> apiResponseBodyMap(String value, Map<String, Object> valueMap) throws Exception {
        //从系统变量中获取配置文件路径
        String configFilePath = System.getProperty("file.hjx.ctrt.config");
        if (configFilePath == null) {
            valueMap.put(DataReader.ROOT_NAME, "请添加启动参数 file.hjx.ctrt.config 来设置配置文件地址");
            return valueMap;
        }
        try {
            String configJsonStr = CrtrUtils.getFileText(configFilePath);
            JSONObject configJson = JSONObject.parseObject(configJsonStr);
            JSONObject config = configJson.getJSONObject("config");
            //导入配置
            if (config.containsKey("import")) {
                JSONArray importArray = config.getJSONArray("import");
                for (int i = 0; i < importArray.size(); i++) {
                    String importFilePath = importArray.getString(i);
                    String importJsonStr = CrtrUtils.getFileText(importFilePath);
                    JSONObject importJson = JSONObject.parseObject(importJsonStr);
                    Set<String> keys = importJson.keySet();
                    for (String key : keys) {
                        configJson.put(key, importJson.getJSONObject(key));
                    }
                }
            }
            valueMap.put(DataReader.ROOT_NAME, configJson.toJSONString());
            return valueMap;
        } catch (Exception e) {
            valueMap.put(DataReader.ROOT_NAME, e.getMessage());
            return valueMap;
        }
    }

    /**
     * 不处理
     *
     * @param value 转换完成的请求参数
     * @return
     */
    @Override
    public Handler<Promise<String>> getApiResult(String value) {
        return null;
    }
}
