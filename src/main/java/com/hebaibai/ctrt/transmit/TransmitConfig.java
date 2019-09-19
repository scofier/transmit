package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import io.vertx.core.http.HttpMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Data
public class TransmitConfig {

    /**
     * 配置编号
     */
    private String code;

    /**
     * 插件编号
     */
    private String extCode;

    /**
     * 是否缓存模板文件
     */
    @Getter
    @Setter
    private boolean cache;

    /**
     * 请求路径
     */
    private String reqPath;

    /**
     * 请求方式
     */
    private HttpMethod reqMethod;

    /**
     * 请求参数类型
     */
    private DataType reqType;

    /**
     * 请求返回参数类型
     */
    private DataType resType;

    /**
     * 转发路径
     */
    private String apiPath;

    /**
     * 转发方式
     */
    private HttpMethod apiMethod;

    /**
     * 转发数据请求类型
     */
    private DataType apiReqType;

    /**
     * 转发数据响应类型
     */
    private DataType apiResType;

    /**
     * 转发请求数据转换模板
     */
    private String apiReqFtlText;

    /**
     * 转发请求数据转换模板文件路径
     */
    private String apiReqFtlPath;

    /**
     * 转发响应数据转换模板
     */
    private String apiResFtlText;

    /**
     * 转发响应数据转换模板文件路径
     */
    private String apiResFtlPath;

    /**
     * 请求超时时间
     */
    private int timeout;

    /**
     * 接口调用出错重试
     * TODO 没有实现
     */
    private int retries;

    /**
     * 判断是否缓存模板文件
     *
     * @return
     */
    public String getApiReqFtlText() {
        if (cache) {
            return apiReqFtlText;
        } else {
            try {
                return CrtrUtils.getFileText(apiReqFtlPath);
            } catch (IOException e) {
                throw new RuntimeException("");
            }
        }

    }

    /**
     * 判断是否缓存模板文件
     *
     * @return
     */
    public String getApiResFtlText() {
        if (cache) {
            return apiResFtlText;
        } else {
            try {
                return CrtrUtils.getFileText(apiResFtlPath);
            } catch (IOException e) {
                throw new RuntimeException("");
            }
        }
    }

}
