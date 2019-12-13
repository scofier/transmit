package com.hebaibai.ctrt.convert;

import com.hebaibai.ctrt.convert.reader.XmlDataReader;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerUtilsTest {


    @Test
    public void name1() throws Exception {
        System.out.println(FreeMarkerUtils.format(
                new HashMap() {{
//                    put("app", null);
                }},
                new FreeMarkerFtl() {{
                    setTemplateName("test");
                    setTemplateText("${app.asd!}");
                }}));
    }

    @Test
    public void name2() throws Exception {

        XmlDataReader dataReader = new XmlDataReader();
        dataReader.read(" \n" +
                "<soap:Envelope\n" +
                "    xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soap:Body>\n" +
                "        <ns2:savepropertyProposalResponse\n" +
                "            xmlns:ns2=\"http://property.provider.app.thirdparty.echannel.ebiz.isoftstone.com/\">\n" +
                "            <return>\n" +
                "                <responseBody>\n" +
                "                    <responseHead>\n" +
                "                        <errorMessage>成功</errorMessage>\n" +
                "                        <requestType>00001</requestType>\n" +
                "                        <responseCode>0000</responseCode>\n" +
                "                    </responseHead>\n" +
                "                    <propertyreturn>\n" +
//                "                        <orderCode>01010191212102484</orderCode>\n" +
                "                    </propertyreturn>\n" +
                "                </responseBody>\n" +
                "            </return>\n" +
                "        </ns2:savepropertyProposalResponse>\n" +
                "    </soap:Body>\n" +
                "</soap:Envelope>");

        Map<String, Object> requestData = dataReader.getRequestData();

        System.out.println(FreeMarkerUtils.format(requestData, new FreeMarkerFtl() {{
            setTemplateName("test");
            setTemplateText("{\n" +
                    "<#if ROOT.Body.Fault.faultcode?length gt 1 >\n" +
                    "    \"header\": {\n" +
                    "    \"type\": \"PAY\",\n" +
                    "    \"uuid\": null,\n" +
                    "    \"errorMessage\": \"${ROOT.Body.Fault.faultstring}\",\n" +
                    "    \"code\": \"0\",\n" +
                    "    \"tradeTime\": \"${.now?string['yyyy-MM-dd HH:mm:ss']}\"\n" +
                    "    }\n" +
                    "<#elseif ROOT.Body.savepropertyProposalResponse.return.responseBody.responseHead.responseCode != \"0000\" >\n" +
                    "    \"header\": {\n" +
                    "    \"type\": \"PAY\",\n" +
                    "    \"uuid\": null,\n" +
                    "    \"errorMessage\": \"${ROOT.Body.savepropertyProposalResponse.return.responseBody.responseHead.errorMessage}\",\n" +
                    "    \"code\": \"0\",\n" +
                    "    \"tradeTime\": \"${.now?string['yyyy-MM-dd HH:mm:ss']}\"\n" +
                    "    }\n" +
                    "<#elseif ROOT.Body.savepropertyProposalResponse.return.responseBody.responseHead.responseCode == \"0000\" >\n" +
                    "    \"header\": {\n" +
                    "    \"type\": \"PAY\",\n" +
                    "    \"uuid\": null,\n" +
                    "    \"errorMessage\": null,\n" +
                    "    \"code\": \"1\",\n" +
                    "    \"tradeTime\": \"${.now?string['yyyy-MM-dd HH:mm:ss']}\"\n" +
                    "    },\n" +
                    "    \"body\": {\n" +
                    "    \"msg\": \"${ROOT.Body.savepropertyProposalResponse.return.responseBody.responseHead.errorMessage}\",\n" +
                    "    \"payType\": \"PAY\",\n" +
                    "    <#if ROOT.Body.savepropertyProposalResponse.return.responseBody.propertyreturn?is_string>\n" +
                    "        \"outOrderNo\": null,\n" +
                    "    <#else >\n" +
                    "        \"outOrderNo\": \"${ROOT.Body.savepropertyProposalResponse.return.responseBody.propertyreturn.orderCode}\",\n" +
                    "    </#if>\n" +
                    "    \"payUrl\": null\n" +
                    "    }\n" +
                    "</#if>\n" +
                    "}");
        }}));
    }
}