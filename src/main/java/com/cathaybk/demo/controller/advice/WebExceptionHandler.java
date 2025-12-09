package com.cathaybk.demo.controller.advice;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.HEADER;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全域異常處理器
 * 統一處理所有 API 的異常回應
 */
@RestControllerAdvice
public class WebExceptionHandler {

    /**
     * 處理查無資料異常
     * RETURNCODE: E702
     */
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleDataNotFoundException(DataNotFoundException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.DATA_NOT_FOUND.getCode(),
                ReturnCodeAndDescEnum.DATA_NOT_FOUND.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理輸入錯誤異常
     * RETURNCODE: E001
     */
    @ExceptionHandler(ErrorInputException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleErrorInputException(ErrorInputException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.ERROR_INPUT.getCode(),
                ReturnCodeAndDescEnum.ERROR_INPUT.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理資料格式錯誤異常
     * RETURNCODE: E002
     */
    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleDataFormatException(DataFormatException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.DATA_FORMAT_ERROR.getCode(),
                ReturnCodeAndDescEnum.DATA_FORMAT_ERROR.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理新增失敗異常 (資料重複)
     * RETURNCODE: E003
     */
    @ExceptionHandler(InsertFailException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleInsertFailException(InsertFailException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.DATA_DUPLICATE.getCode(),
                ReturnCodeAndDescEnum.DATA_DUPLICATE.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理更新失敗異常
     * RETURNCODE: E005
     */
    @ExceptionHandler(UpdateFailException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleUpdateFailException(UpdateFailException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.FAIL.getCode(),
                ReturnCodeAndDescEnum.FAIL.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理刪除失敗異常
     * RETURNCODE: E006
     */
    @ExceptionHandler(DeleteFailException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleDeleteFailException(DeleteFailException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.DELETE_ERROR.getCode(),
                ReturnCodeAndDescEnum.DELETE_ERROR.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理驗證異常
     * RETURNCODE: E001
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleValidationException(Exception e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.ERROR_INPUT.getCode(),
                ReturnCodeAndDescEnum.ERROR_INPUT.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理運行時異常
     * RETURNCODE: 9999
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleRuntimeException(RuntimeException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.S9999.getCode(),
                ReturnCodeAndDescEnum.S9999.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理所有其他異常
     * RETURNCODE: 9999
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleException(Exception e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.S9999.getCode(),
                ReturnCodeAndDescEnum.S9999.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 建立錯誤回應
     */
    private ResponseTemplate<EmptyTranrs> createErrorResponse(String code, String desc) {
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setReturncode(code);
        header.setReturndesc(desc);
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());
        return response;
    }
}