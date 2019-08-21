package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.CtrtLancher;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TransmitLancherTest {
    @Test
    public void name() throws MalformedURLException {

        URL url = new URL("http://127.0.0.1:9090/test");
        String host = url.getHost();
        String path = url.getPath();
        System.out.println(host);
        System.out.println(path);
    }

    public static void main(String[] args) throws IOException {
        CtrtLancher ctrtLancher = new CtrtLancher();
        Config config = new Config();
        config.setPort(9090);
        config.put(new TransmitConfig() {{
            setCode("text-post");
            //路由地址
            setReqPath("/post");
            //路由地址请求方式
            setReqMethod(HttpMethod.GET);
            //入参类型 表单提交
            setReqType(DataType.QUERY);
            //出参类型 json
            setResType(DataType.JSON);
            //=========================================
            //转发接口地址
            setApiPath("http://47.98.105.202:9003/api/transit");
            //转发接口请求方式 POST
            setApiMethod(HttpMethod.POST);
            //转发接口请求参数类型 表单
            setApiReqType(DataType.XML);
            //转发接口响应参数类型 JSON
            setApiResType(DataType.XML);
            //转发接口请求参数转换模板
            setApiReqFtlText(CrtrUtils.getFileText("/home/hjx/work/myProject/transmit/file/post-req.ftl"));
            //转发接口响应参数转换模板
            setApiResFtlText(CrtrUtils.getFileText("/home/hjx/work/myProject/transmit/file/post-res.ftl"));
        }});

        ctrtLancher.start(config);
    }
}