package com.cathaybk.demo.controller.advice;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.HEADER;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.ErrorInputException;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.exception.DataFormatException;
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
     * 處理新增失敗異常
     * RETURNCODE: E003
     */
    @ExceptionHandler(InsertFailException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleInsertFailException(InsertFailException e) {
        ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                ReturnCodeAndDescEnum.INSERT_FAIL.getCode(),
                ReturnCodeAndDescEnum.INSERT_FAIL.getDesc()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 處理資料格式錯誤異常
     * RETURNCODE: E005
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
     * 處理驗證異常（Bean Validation）
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
     * 根據異常訊息判斷錯誤類型
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseTemplate<EmptyTranrs>> handleRuntimeException(RuntimeException e) {
        // 更新失敗
        if (e.getMessage() != null && e.getMessage().contains("更新失敗")) {
            ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                    ReturnCodeAndDescEnum.UPDATE_FAIL.getCode(),
                    ReturnCodeAndDescEnum.UPDATE_FAIL.getDesc()
            );
            return ResponseEntity.ok(response);
        }
        // 刪除失敗
        else if (e.getMessage() != null && e.getMessage().contains("刪除失敗")) {
            ResponseTemplate<EmptyTranrs> response = createErrorResponse(
                    ReturnCodeAndDescEnum.DELETE_FAIL.getCode(),
                    ReturnCodeAndDescEnum.DELETE_FAIL.getDesc()
            );
            return ResponseEntity.ok(response);
        }

        // 其他系統異常
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
     * @param code 錯誤代碼
     * @param desc 錯誤描述
     * @return 錯誤回應物件
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