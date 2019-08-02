package com.hebaibai.ctrt.transmit.util.convert;

import com.hebaibai.ctrt.convert.FreeMarkerFtl;
import com.hebaibai.ctrt.convert.FreeMarkerUtils;
import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.util.Convert;

import java.util.Map;

public class BaseConvert implements Convert {

    @Override
    public boolean support(DataType from, DataType to) {
        return true;
    }

    @Override
    public String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception {
        String format = FreeMarkerUtils.format(objectMap, new FreeMarkerFtl() {{
            setTemplateName(ftlName);
            setTemplateText(ftl);
        }});
        return format;
    }
}
