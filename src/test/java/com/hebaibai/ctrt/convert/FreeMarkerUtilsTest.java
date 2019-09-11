package com.hebaibai.ctrt.convert;

import org.junit.Test;

import java.util.HashMap;

public class FreeMarkerUtilsTest {

    @Test
    public void name() throws Exception {

        System.out.println(FreeMarkerUtils.format(new HashMap() {{
            put("name", null);
        }}, new FreeMarkerFtl() {{
            setTemplateName("test");
            setTemplateText("" +
                    "<@regular pattern='<xml>(.*)<xml>(.*)<xml>'  >${name}</@regular>" +
                    "<@has name='name'></@has>" +
                    "");
        }}));
    }
}