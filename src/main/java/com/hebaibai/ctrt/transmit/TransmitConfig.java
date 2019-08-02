package com.hebaibai.ctrt.transmit;

import io.vertx.core.http.HttpMethod;

import java.io.*;

public class TransmitConfig {

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
    private File apiReqFtl;

    /**
     * 抓发响应数据转换模板
     */
    private File apiResFtl;

    private String apiReqFtlText;

    private String apiResFtlText;

    public String getReqPath() {
        return reqPath;
    }

    public void setReqPath(String reqPath) {
        this.reqPath = reqPath;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public HttpMethod getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(HttpMethod reqMethod) {
        this.reqMethod = reqMethod;
    }

    public DataType getReqType() {
        return reqType;
    }

    public void setReqType(DataType reqType) {
        this.reqType = reqType;
    }

    public HttpMethod getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(HttpMethod apiMethod) {
        this.apiMethod = apiMethod;
    }

    public DataType getApiReqType() {
        return apiReqType;
    }

    public void setApiReqType(DataType apiReqType) {
        this.apiReqType = apiReqType;
    }

    public File getApiReqFtl() {
        return apiReqFtl;
    }

    public void setApiReqFtl(File apiReqFtl) throws IOException {
        this.apiReqFtl = apiReqFtl;
        InputStream inputStream = new FileInputStream(apiReqFtl);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        this.apiReqFtlText = new String(bytes);
    }

    public File getApiResFtl() {
        return apiResFtl;
    }

    public void setApiResFtl(File apiResFtl) throws IOException {
        this.apiResFtl = apiResFtl;
        InputStream inputStream = new FileInputStream(apiResFtl);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        this.apiResFtlText = new String(bytes);
    }

    public String getApiReqFtlText() {
        return apiReqFtlText;
    }

    public String getApiResFtlText() {
        return apiResFtlText;
    }

    public DataType getResType() {
        return resType;
    }

    public void setResType(DataType resType) {
        this.resType = resType;
    }

    public DataType getApiResType() {
        return apiResType;
    }

    public void setApiResType(DataType apiResType) {
        this.apiResType = apiResType;
    }
}
