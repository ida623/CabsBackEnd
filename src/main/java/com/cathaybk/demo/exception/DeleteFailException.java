package com.cathaybk.demo.exception;

/**
 * 刪除失敗異常
 * RETURNCODE: E004
 */
public class DeleteFailException extends RuntimeException {
    
    public DeleteFailException(String message) {
        super(message);
    }
    
    public DeleteFailException(String message, Throwable cause) {
        super(message, cause);
    }
}