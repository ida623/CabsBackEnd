package com.cathaybk.demo.exception;

/**
 * 更新失敗異常
 * RETURNCODE: E002
 */
public class UpdateFailException extends RuntimeException {
    
    public UpdateFailException(String message) {
        super(message);
    }
    
    public UpdateFailException(String message, Throwable cause) {
        super(message, cause);
    }
}