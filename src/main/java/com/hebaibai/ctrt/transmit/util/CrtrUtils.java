package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.convert.BaseConvert;
import com.hebaibai.ctrt.transmit.util.param.GetRequestParam;
import com.hebaibai.ctrt.transmit.util.param.PostFormParam;
import com.hebaibai.ctrt.transmit.util.param.PostJsonParam;
import com.hebaibai.ctrt.transmit.util.param.PostXmlParam;
import com.hebaibai.ctrt.transmit.util.request.GetRequest;
import com.hebaibai.ctrt.transmit.util.request.PostFormRequest;
import com.hebaibai.ctrt.transmit.util.request.PostJsonRequest;
import com.hebaibai.ctrt.transmit.util.request.PostXmlRequest;
import io.vertx.core.http.HttpMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class CrtrUtils {

    public static final String CHARSET_NAME = "utf-8";

    private static final List<Param> PARAM_LIST = Arrays.asList(
            new GetRequestParam(),
            new PostFormParam(),
            new PostJsonParam(),
            new PostXmlParam()
    );
    private static final List<Request> REQUEST_LIST = Arrays.asList(
            new GetRequest(),
            new PostFormRequest(),
            new PostJsonRequest(),
            new PostXmlRequest()
    );
    private static final List<Convert> CONVERT_LIST = Arrays.asList(
            new BaseConvert()
    );

    public static Param param(HttpMethod method, DataType dataType) {
        for (Param param : PARAM_LIST) {
            if (param.support(method, dataType)) {
                return param;
            }
        }
        return null;
    }

    public static Request request(HttpMethod method, DataType dataType) {
        for (Request request : REQUEST_LIST) {
            if (request.support(method, dataType)) {
                return request;
            }
        }
        return null;
    }

    public static Convert convert(DataType from, DataType to) {
        for (Convert convert : CONVERT_LIST) {
            if (convert.support(from, to)) {
                return convert;
            }
        }
        return null;
    }

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
