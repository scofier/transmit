package com.hebaibai.ctrt.convert.reader;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

public class XmlDataReaderTest {

    @Test
    public void name() {

        XmlDataReader dataReader = new XmlDataReader();
        dataReader.read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ReturnInfo>\n" +
                "  <GeneralInfoReturn>\n" +
                "    <UUID>d83a011a-958d-4310-a51b-0fb3a4228ef1</UUID>\n" +
                "    <PlateformCode>ECP00056</PlateformCode>\n" +
                "    <ErrorCode>00</ErrorCode>\n" +
                "    <ErrorMessage>校验成功</ErrorMessage>\n" +
                "  </GeneralInfoReturn>\n" +
                "  <PolicyInfoReturns>\n" +
                "    <PolicyInfoReturn>\n" +
                "      <SerialNo>0</SerialNo>\n" +
                "      <ProposalNo>TEAK201935020000011116</ProposalNo>\n" +
                "      <PolicyNo>                      </PolicyNo>\n" +
                "      <SaveResult>00</SaveResult>\n" +
                "      <SaveMessage>投保单TEAK201935020000011116自动核保通过，需见费转保单!交费通知单号为：3502190905900007,微信交费链接为：weixin://wxpay/bizpayurl?pr=VGrxsL4</SaveMessage>\n" +
                "    </PolicyInfoReturn>\n" +
                "  </PolicyInfoReturns>\n" +
                "</ReturnInfo>");
        System.out.println(JSONObject.toJSONString(dataReader.getRequestData()));
    }
}