package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.convert.freemarker.Has;
import com.hebaibai.ctrt.convert.freemarker.Regular;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.convert.BaseConvert;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import com.hebaibai.ctrt.transmit.util.param.*;
import com.hebaibai.ctrt.transmit.util.request.*;
import com.hebaibai.ctrt.transmit.util.ext.BaseExt;
import freemarker.template.TemplateDirectiveModel;
import io.vertx.core.http.HttpMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 工具集合
 *
 * @author hjx
 */
public class CrtrUtils {

    public static final String CHARSET_NAME = "utf-8";

    /**
     * freemarker 自定义指令
     */
    public static Map<String, TemplateDirectiveModel> FREEMARKER_DIRECTIVE_MODEL = new HashMap() {{
        put("regular", new Regular());
        put("has", new Has());
    }};

    private static final Ext BASE_EXT = new BaseExt();


    /**
     * 参数获取工具
     */
    private static final List<Param> PARAM_LIST = Arrays.asList(
            new GetRequestParam(),
            new PostFormParam(),
            new PostJsonParam(),
            new PostTextParam(),
            new PostXmlParam()
    );
    /**
     * 发起请求获取工具
     */
    private static final List<Request> REQUEST_LIST = Arrays.asList(
            new GetQueryRequest(),
            new PostFormRequest(),
            new PostJsonRequest(),
            new PostTextRequest(),
            new PostXmlRequest()
    );

    /**
     * 参数转换工具
     */
    private static final List<Convert> CONVERT_LIST = Arrays.asList(
            new BaseConvert()
    );


    /**
     * 获取参数获取器
     *
     * @param method
     * @param dataType
     * @return
     */
    public static Param param(HttpMethod method, DataType dataType) {
        for (Param param : PARAM_LIST) {
            if (param.support(method, dataType)) {
                return param;
            }
        }
        return null;
    }

    /**
     * 获取请求发起器
     *
     * @param method
     * @param dataType
     * @return
     */
    public static Request request(HttpMethod method, DataType dataType) {
        for (Request request : REQUEST_LIST) {
            if (request.support(method, dataType)) {
                return request;
            }
        }
        return null;
    }

    /**
     * 获取数据转换器
     *
     * @param from
     * @param to
     * @return
     */
    public static Convert convert(DataType from, DataType to) {
        for (Convert convert : CONVERT_LIST) {
            if (convert.support(from, to)) {
                return convert;
            }
        }
        return null;
    }


    /**
     * 获取插件
     *
     * @param signCode
     * @return
     */
    public static Ext ext(String signCode) {
        for (Ext ext : Ext.EXT_LIST) {
            if (ext.support(signCode)) {
                return ext;
            }
        }
        return BASE_EXT;
    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String getFileText(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = System.getProperty("user.dir") + "/" + path;
        }
        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return new String(bytes);
    }

}
