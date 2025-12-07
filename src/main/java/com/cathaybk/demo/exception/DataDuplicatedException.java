package com.cathaybk.demo.exception;

/**
 * 資料重複異常
 */
public class DataDuplicatedException extends Exception {

    public DataDuplicatedException() {
    }

    public DataDuplicatedException(String message) {
        super(message);
    }

    public DataDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
