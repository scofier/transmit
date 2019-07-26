package com.hebaibai.ctrt.convert.reader;

import com.hebaibai.ctrt.convert.DataEnum;
import com.hebaibai.ctrt.convert.FreeMarkerFtl;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * 报文数据解析
 */
public interface DataReader {

    /**
     * 根节点
     */
    String ROOT_NAME = "ROOT";

    /**
     * 读取数据
     *
     * @param stringData
     * @return
     */
    void read(String stringData) throws Exception;

    /**
     * 读取流中的数据
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    void read(InputStream inputStream) throws Exception;

    /**
     * 获取数据的所有节点名称
     *
     * @return
     */
    Set<String> getDataNodeNames();

    /**
     * 设置数据转换枚举
     *
     * @param dataEnum
     */
    void setDataEnum(DataEnum dataEnum);

    /**
     * 根据freemarker 模板装换数据
     *
     * @param ftl
     * @return
     */
    String converterFormat(FreeMarkerFtl ftl) throws Exception;

    Map<String, Object> getRequestData();
}
