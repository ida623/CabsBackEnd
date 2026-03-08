package com.cathaybk.demo.exception;

/**
 * REST API 異常
 * RETURNCODE: E300
 * 
 * @author 00550396
 */
public class RestException extends Exception {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * 建構子
     * 
     * @param message 異常訊息
     */
    public RestException(String message) {
        super(message);
    }
    
    /**
     * 建構子
     * 
     * @param message 異常訊息
     * @param cause 原因
     */
    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}