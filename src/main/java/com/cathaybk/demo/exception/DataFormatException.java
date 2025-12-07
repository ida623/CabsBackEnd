package com.cathaybk.demo.exception;

/**
 * 資料格式錯誤異常
 * RETURNCODE: E005
 */
public class DataFormatException extends Exception {
    
    public DataFormatException(String message) {
        super(message);
    }
    
    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}