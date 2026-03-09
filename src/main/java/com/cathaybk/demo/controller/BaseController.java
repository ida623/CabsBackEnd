package com.cathaybk.demo.controller;

import cfc.bff.platform.exception.RequestValidException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class BaseController {

    protected void validateRequest(Errors errors) throws RequestValidException {
        if (errors.hasErrors())
            throw new RequestValidException(
                    errors.getAllErrors().stream()
                            .collect(Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.joining(";"))),
                    errors);
    }

}