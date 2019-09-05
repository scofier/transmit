package com.hebaibai.ctrt.convert;

import org.junit.Test;

import java.util.HashMap;

public class FreeMarkerUtilsTest {

    @Test
    public void name() throws Exception {

        System.out.println(FreeMarkerUtils.format(new HashMap() {{
            put("name", "<xml>你好啊<xml>");
        }}, new FreeMarkerFtl() {{
            setTemplateName("test");
            setTemplateText("===<@regular pattern='<xml>(.*)<xml>'>${name}</@regular>===");
        }}));
    }
}