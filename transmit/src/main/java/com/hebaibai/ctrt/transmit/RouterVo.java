package com.hebaibai.ctrt.transmit;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * @author hjx
 */
@Data
public class RouterVo {

    /**
     * 每次请求的uuid
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 配置中的类型
     */
    private String typeCode;

    /**
     * 请求的路径
     */
    private String path;

    /**
     * 请求中的请求体
     */
    private String body;

    /**
     * get请求中的参数
     */
    private MultiMap params;

    /**
     * 请求的类型
     */
    private HttpMethod method;

    /**
     * 转换参数配置
     */
    private TransmitConfig transmitConfig;

    /**
     * 获取保存用的数据
     *
     * @return
     */
    public String getInsertJsonStr() {
        JSONObject entries = new JSONObject();
        entries.put("id", uuid);
        entries.put("type_code", typeCode);
        if (method == HttpMethod.GET) {
            entries.put("send_msg", getMultiMapStringValue(params));
        } else if (method == HttpMethod.POST) {
            entries.put("send_msg", body);
        }
        return entries.toJSONString();
    }

    /**
     * 获取更新用的数据
     *
     * @return
     */
    public String getUpdateJsonStr() {
        JSONObject entries = new JSONObject();
        entries.put("id", uuid);
        entries.put("receive", body);
        return entries.toJSONString();
    }

    private String getMultiMapStringValue(MultiMap params) {
        if (params == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append(key).append("=").append(value).append("&");
        }
        return builder.toString();
    }
}
