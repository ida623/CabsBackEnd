package com.cathaybk.demo.controller;

import java.io.IOException;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.service.DATC001Svc;
import com.cathaybk.demo.service.DATQ001Svc;
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
@RequestMapping(value = "/api/date-setting-windows", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class DateSettingController extends BaseController {

    /** DATC001Svc */
    private final DATC001Svc theDatc001Svc;

    /** DATQ001Svc */
    private final DATQ001Svc theDatq001Svc;

    /**
     * CABS-B-DATC001 新增日期設定
     *
     * @param req
     * @param err
     * @return
     * @throws RestException
     * @throws RequestValidException
     * @throws IOException
     */
    @PostMapping("/create")
    @Operation(summary = "CABS-B-DATC001 新增日期設定")
    public ResponseTemplate<EmptyTranrs> createDateSetting(
            @Valid @RequestBody RequestTemplate<DATC001Tranrq> req, Errors err)
            throws RestException, RequestValidException, IOException {
        validateRequest(err);
        return theDatc001Svc.createDateSetting(req);
    }

    /**
     * CABS-B-DATQ001 查詢日期設定
     *
     * @param req
     * @return
     * @throws DataNotFoundException
     * @throws IOException
     * @throws RestException
     */
    @PostMapping("/queryLatest")
    @Operation(summary = "CABS-B-DATQ001 查詢日期設定")
    public ResponseTemplate<DATQ001Tranrs> queryLatestDateSetting(
            @RequestBody RequestTemplate<EmptyTranrq> req)
            throws DataNotFoundException, IOException, RestException {
        return theDatq001Svc.queryLatestDateSetting(req);
    }
}
