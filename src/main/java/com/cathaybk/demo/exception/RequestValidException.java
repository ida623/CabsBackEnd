package com.cathaybk.demo.exception;

/**
 * 請求驗證異常
 *
 * @author 00550396
 */
public class RequestValidException extends Exception {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 錯誤代碼 */
    private String errorCode;

    /** 錯誤訊息 */
    private String errorMessage;

    /**
     * Constructor
     *
     * @param errorCode 錯誤代碼
     * @param errorMessage 錯誤訊息
     */
    public RequestValidException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor
     *
     * @param errorMessage 錯誤訊息
     */
    public RequestValidException(String errorMessage) {
        this("E102", errorMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}