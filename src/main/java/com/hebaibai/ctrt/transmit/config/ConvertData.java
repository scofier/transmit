package com.hebaibai.ctrt.transmit.config;

import io.vertx.core.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 请求来源
 *
 * @author hjx
 */
public class ConvertData {

    /**
     * 请求地址
     */
    @Getter
    @Setter
    private String path;

    /**
     * 请求方式
     */
    @Getter
    @Setter
    private HttpMethod method;

    /**
     * 数据类型
     * json/xml
     */
    @Getter
    @Setter
    private DataType dataType;


    /**
     * 数据转换模板文件路径
     */
    private String convertFilePath;

    /**
     * 数据转换模板内容
     */
    @Getter
    private String ftlText;

    public String getConvertFilePath() {
        return convertFilePath;
    }

    public void setConvertFilePath(String convertFilePath) throws IOException {
        this.convertFilePath = convertFilePath;
        try (FileInputStream inputStream = new FileInputStream(new File(convertFilePath))) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            this.ftlText = new String(bytes, "utf-8");
        }
    }


}
