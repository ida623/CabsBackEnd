package com.cathaybk.demo.exception;

/**
 * 查無資料異常
 */
public class DataNotFoundException extends Exception {

    public DataNotFoundException() {
    }

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
