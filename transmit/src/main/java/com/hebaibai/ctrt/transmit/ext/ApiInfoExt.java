package com.hebaibai.ctrt.transmit.ext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import com.hebaibai.ctrt.transmit.util.ext.Exts;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 项目配置展示接口
 */
public final class ApiInfoExt implements Ext {

    private Vertx vertx;

    private Map<String, Object> transmitJson;

    /**
     * 分组标志
     */
    private String group;

    @Override
    public void init(Vertx vertx, Map<String, Object> transmitJson) {
        this.vertx = vertx;
        this.transmitJson = transmitJson;
        Map<String, Object> api = (Map<String, Object>) transmitJson.get("api");
        if (api.containsKey("group")) {
            Object group = api.get("group");
            if (group != null) {
                this.group = group.toString();
            }
        }
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
    public Handler<Promise<Map<String, Object>>> outRequestBodyMap(String value, Map<String, Object> valueMap) {
        return promise -> {
            promise.complete(valueMap);
        };
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
    public Handler<Promise<Map<String, Object>>> apiResponseBodyMap(String value, Map<String, Object> valueMap) {
        return promise -> {
            //从系统变量中获取配置文件路径
            String configFilePath = System.getProperty("file.hjx.ctrt.config");
            if (configFilePath == null) {
                valueMap.put(DataReader.ROOT_NAME, "请添加启动参数 file.hjx.ctrt.config 来设置配置文件地址");
                promise.complete(valueMap);
                return;
            }
            try {
                JSONObject configInfo = getConfigInfo(configFilePath);
                valueMap.put(DataReader.ROOT_NAME, configInfo.toJSONString());
            } catch (Exception e) {
                valueMap.put(DataReader.ROOT_NAME, e.getMessage());
            }
            promise.complete(valueMap);
        };

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

    public JSONObject getConfigInfo(String configFilePath) throws IOException {
        String configJsonStr = CrtrUtils.getFileText(configFilePath);
        JSONObject configJson = JSONObject.parseObject(configJsonStr);
        JSONObject config = configJson.getJSONObject("config");
        configJson.remove("config");
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
        //根据分组标志分组
        JSONObject result = new JSONObject();
        for (String key : configJson.keySet()) {
            JSONObject obj = configJson.getJSONObject(key);
            if (obj.containsKey(this.group)) {
                String groupKey = obj.getString(this.group);
                if (!result.containsKey(groupKey)) {
                    result.put(groupKey, new JSONObject());
                }
                //去除分组节点
                obj.remove(this.group);
                JSONObject groupJson = result.getJSONObject(groupKey);
                groupJson.put(key, obj);
                result.put(groupKey, groupJson);
            } else {
                result.put(key, obj);
            }
        }
        config = new JSONObject();
        JSONObject ext = new JSONObject();
        for (String code : Exts.codes()) {
            ext.put(code, Exts.explain(code));
        }
        config.put("ext", ext);
        result.put("config", config);
        return result;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
