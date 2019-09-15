package com.hebaibai.ctrt.convert;

import com.hebaibai.ctrt.convert.reader.XmlDataReader;
import org.junit.Test;

import java.util.Map;

public class FreeMarkerUtilsTest {

    @Test
    public void name() throws Exception {

        XmlDataReader dataReader = new XmlDataReader();
        dataReader.read("" +
                "<ReturnInfo>\n" +
                "  <name>120009</name>\n" +
                "  <GeneralInfoReturn>\n" +
                "    <UUID>1b438618-1118-4e88-a20f-63a712077050</UUID>\n" +
                "    <PlateformCode>ECP00056</PlateformCode>\n" +
                "    <ErrorCode>00</ErrorCode>\n" +
                "    <ErrorMessage>校验成功</ErrorMessage>\n" +
                "  </GeneralInfoReturn>\n" +
                "  <PolicyInfoReturns>\n" +
                "    <PolicyInfoReturn>\n" +
                "      <SerialNo>0</SerialNo>\n" +
                "      <ProposalNo>TEAK201935020000011171</ProposalNo>\n" +
                "      <PolicyNo>                      </PolicyNo>\n" +
                "      <SaveResult>00</SaveResult>\n" +
                "      <SaveMessage>投保单TEAK201935020000011171自动核保通过，需见费转保单!交费通知单号为：3502190915900001,微信交费链接为：weixin://wxpay/bizpayurl?pr=z4Q557N</SaveMessage>\n" +
                "    </PolicyInfoReturn>\n" +
                "  </PolicyInfoReturns>\n" +
                "</ReturnInfo>\n");

        Map<String, Object> requestData = dataReader.getRequestData();

        System.out.println(FreeMarkerUtils.format(requestData, new FreeMarkerFtl() {{
            setTemplateName("test");
            setTemplateText("" +
                    "<#assign map={\n" +
                    "    \"120010\":\"16\",\n" +
                    "    \"120003\":\"04\",\n" +
                    "    \"120005\":\"10\",\n" +
                    "    \"120006\":\"06\",\n" +
                    "    \"120009\":\"99\",\n" +
                    "    \"120001\":\"01\",\n" +
                    "    \"120002\":\"03\",\n" +
                    "    \"120007\":\"99\"\n" +
                    "    }/>" +
                    "${map[ROOT.name]}" +
                    "{\n" +
                    "    \"header\": {\n" +
                    "        \"type\": \"\",\n" +
                    "        \"uuid\": \"${ROOT.GeneralInfoReturn.UUID}\",\n" +
                    "        \"errorMessage\": \"${ROOT.GeneralInfoReturn.ErrorCode}\",\n" +
                    "        <#if ROOT.PolicyInfoReturns.PolicyInfoReturn.ProposalNo != null>\n" +
                    "        \"code\": \"1\",\n" +
                    "        <#else>\n" +
                    "        \"code\": \"0\",\n" +
                    "        </#if>\n" +
                    "        \"tradeTime\": \"${.now?string['yyyy-MM-dd HH:mm:ss']}\"\n" +
                    "\n" +
                    "    },\n" +
                    "    \"body\": {\n" +
                    "        \"type\": \"PAY\",\n" +
                    "        \"outOrderNo\": \"${ROOT.PolicyInfoReturns.PolicyInfoReturn.ProposalNo}\",\n" +
                    "        \"payUrl\": \"<@regular pattern='投保单(.*)自动核保通过，需见费转保单!交费通知单号为：(.*),微信交费链接为：(.*)' group='3'>${ROOT.PolicyInfoReturns.PolicyInfoReturn.SaveMessage}</@regular>\"\n" +
                    "    }\n" +
                    "}");
        }}));
    }
}