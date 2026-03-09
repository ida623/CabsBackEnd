package com.cathaybk.demo.controller;

import java.io.IOException;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.service.CODQ001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/drop-down-menu", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class DropDownMenuController extends BaseController {

    /** CODQ001Svc */
    private final CODQ001Svc theCODQ001Svc;

    /**
     * CABS-B-CODQ001 代碼查詢
     *
     * @param req
     * @return
     * @throws IOException
     * @throws RequestValidException
     */
    @PostMapping("/queryList")
    @Operation(summary = "CABS-B-CODQ001 代碼查詢")
    public ResponseTemplate<CODQ001Tranrs> queryList(@Valid @RequestBody RequestTemplate<CODQ001Tranrq> req,
                                                     Errors err) throws IOException, RequestValidException {
        validateRequest(err);
        return theCODQ001Svc.queryList(req);
    }

}