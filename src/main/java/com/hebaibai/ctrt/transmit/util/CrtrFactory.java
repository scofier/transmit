package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import io.vertx.core.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class CrtrFactory {

    public static final List<? extends Support> SUPPORT_LIST = Arrays.asList(
            new GetParamPath(),
            new PostFormBody(),
            new PostJsonBody(),
            new PostXmlBody()
    );

    public static ParamGet paramGet(HttpMethod method, DataType dataType) {
        for (Support support : SUPPORT_LIST) {
            if (support.support(method, dataType)) {
                return (ParamGet) support;
            }
        }
        return null;
    }

    public static Request request(HttpMethod method, DataType dataType) {
        for (Support support : SUPPORT_LIST) {
            if (support.support(method, dataType)) {
                return (Request) support;
            }
        }
        return null;
    }

    public static Convert convert(HttpMethod method, DataType dataType) {
        for (Support support : SUPPORT_LIST) {
            if (support.support(method, dataType)) {
                return (Convert) support;
            }
        }
        return null;
    }
}
