package com.cathaybk.demo.exception;

/**
 * 新增資料異常
 */
public class InsertFailException extends Exception {

    public InsertFailException() {
    }

    public InsertFailException(String message) {
        super(message);
    }

    public InsertFailException(String message, Throwable cause) {
        super(message, cause);
    }

}
