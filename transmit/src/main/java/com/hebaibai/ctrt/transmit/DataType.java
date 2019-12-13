package com.hebaibai.ctrt.transmit;

/**
 * @author hjx
 */

public enum DataType {
    FORM("text/plain"),
    JSON("application/json"),
    QUERY("text/plain"),
    TEXT("text/plain"),
    XML("application/xml"),
    HTML("text/html");

    private final String val;

    DataType(String val) {
        this.val = val;
    }

    public String val() {
        return this.val;
    }
}