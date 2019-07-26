package com.hebaibai.ctrt.transmit.config;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 数据转换关系
 *
 * @author hjx
 */
public class ConvertMapping {

    @Getter
    @Setter
    private ConvertData source;

    @Getter
    @Setter
    private ConvertData target;


    public DataType sourceDataType() {
        return source.getDataType();
    }

    public DataType targetDataType() {
        return target.getDataType();
    }

}
