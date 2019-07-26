package com.hebaibai.ctrt.transmit.config;

public enum DataType {
    from("application/x-www-form-urlencoded"),
    json("application/json"),
    xml("application/xml"),
    text("text/plain");

    private String contentType;

    DataType(String contentType) {
        this.contentType = contentType;
    }

    public String val() {
        return this.contentType;
    }
}