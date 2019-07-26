package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.config.ConvertMapping;

public class JsonBodyResponse {

    private ConvertMapping mapping;

    private String jsonBody;

    public JsonBodyResponse(ConvertMapping mapping, String jsonBody) {
        this.mapping = mapping;
        this.jsonBody = jsonBody;
    }

    @Override
    public String toString() {


        return "JsonBodyResponse{" +
                "mapping=" + mapping +
                ", jsonBody='" + jsonBody + '\'' +
                '}';
    }
}
