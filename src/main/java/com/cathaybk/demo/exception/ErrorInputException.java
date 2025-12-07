package com.cathaybk.demo.exception;

import java.io.Serial;

/**
 * 使用者欄位輸入錯誤異常
 */
public class ErrorInputException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ErrorInputException(String message) {
        super(message);
    }
}
