package com.hebaibai.ctrt.transmit.util.param;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import com.hebaibai.ctrt.transmit.util.Param;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hjx
 */
public class PostFormParam implements Param {

    @Override
    public boolean support(HttpMethod method, DataType dataType) {
        return method == HttpMethod.POST && dataType == DataType.FORM;
    }

    @Override
    public Map<String, Object> params(RouterVo routerVo) {
        Map<String, Object> root = new HashMap();
        Map<String, Object> params = new HashMap();
        root.put(DataReader.ROOT_NAME, params);
        MultiMap queryParams = routerVo.getParams();
        for (Map.Entry<String, String> entry : queryParams.entries()) {
            params.put(entry.getKey(), entry.getValue());
        }
        String requestBody = routerVo.getBody();
        if (StringUtils.isBlank(requestBody)) {
            return root;
        }
        String[] split = requestBody.split("&");
        for (String nameParam : split) {
            String[] p = nameParam.split("=");
            if (p.length == 2) {
                params.put(p[0], p[1]);
            } else {
                params.put(p[0], null);
            }
        }
        return root;
    }

}
