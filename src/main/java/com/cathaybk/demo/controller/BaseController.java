// file: src/main/java/cfc/bff/platform/controller/BaseController.java
package com.cathaybk.demo.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

/**
 * Base Controller
 * 提供共用的 Controller 功能
 *
 * @author 00550396
 */
public abstract class BaseController {

    /**
     * 驗證請求參數
     *
     * @param errors Spring Validation Errors
     * @throws RequestValidException 當驗證失敗時拋出
     */
    protected void validateRequest(Errors errors) throws RequestValidException {
        if (errors.hasErrors()) {
            String errorMessages = errors.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.joining("; "));
            
            throw new RequestValidException("E102", "Validation failed: " + errorMessages);
        }
    }

}