package com.hebaibai.ctrt.ext.sign;

import com.hebaibai.ctrt.transmit.util.Assert;
import com.hebaibai.ctrt.transmit.util.Sign;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * 厦门人保, 产品签名验证接口
 */
public class PICC_XM_testSign implements Sign {

    private static final String key = "$ohr619LVb6IrwG";

    @Override
    public boolean support(String signCode) {
        return "PICC_XM".equals(signCode);
    }

    @Override
    public String sign(String value) throws Exception {
        Document doc = DocumentHelper.parseText(value);
        Element rootElt = doc.getRootElement();
        Element generalInfo = rootElt.element("GeneralInfo");
        String uuid = generalInfo.elementText("UUID");
        Assert.isNotBlank(uuid, "uuid is not null");
        String policyInfos = value.substring(value.indexOf("<PolicyInfo>"), value.indexOf("</PolicyInfos>"));
        policyInfos = policyInfos
                .replaceAll("\\r\\n", "")
                .replaceAll(" ", "")
                .replaceAll("\\n", "");
        String salt = uuid.substring(uuid.length() - 8, uuid.length());
        //第一次加密
        String MD5Dense = digestByMd5(policyInfos, key);
        //第二次加盐
        String MD5salt = digestByMd5(MD5Dense, salt);
        Element md5Value = generalInfo.element("Md5Value");
        md5Value.setText(MD5salt);
        OutputFormat format = OutputFormat.createCompactFormat();
        StringWriter writer = new StringWriter();
        XMLWriter output = new XMLWriter(writer, format);
        output.write(doc);
        writer.close();
        output.close();
        String newXml = writer.toString();
        return newXml;
    }

    @Override
    public boolean verify(String value) throws Exception {
        return true;
    }


    private static String digestByMd5(String data, String charset) throws UnsupportedEncodingException {
        data = data + charset;
        return DigestUtils.md5Hex(data.getBytes("UTF-8"));
    }

}
