package com.hebaibai.ctrt.convert.reader;

import org.junit.Test;

public class XmlDataReaderTest {

    @Test
    public void name() {

        XmlDataReader dataReader = new XmlDataReader();
        dataReader.read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ReturnInfo>\n" +
                "  <GeneralInfoReturn>\n" +
                "    <UUID></UUID>\n" +
                "    <PlateformCode></PlateformCode>\n" +
                "    <ErrorCode>07</ErrorCode>\n" +
                "    <ErrorMessage>起保时间校验错误</ErrorMessage>\n" +
                "  </GeneralInfoReturn>\n" +
                "</ReturnInfo>");
        System.out.println(dataReader.getRequestData());
    }
}