package com.hebaibai.ctrt.transmit.util.request;

import org.junit.Test;

public class GetQueryRequestTest {

    @Test
    public void request() {
        GetQueryRequest request = new GetQueryRequest();
        request.request(null, null, null, stringAsyncResult -> {
        });
    }
}
