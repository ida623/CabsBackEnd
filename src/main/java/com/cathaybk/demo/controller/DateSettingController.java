package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.DATC001Svc;
import com.cathaybk.demo.service.DATQ001Svc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/date-setting-windows")
@RequiredArgsConstructor
public class DateSettingWindowsController extends BaseController {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /**CABS-B-DATQ001 查詢日期設定 */
    private final DATQ001Svc datq001Svc;

    /** CABS-B-DATC001 新增日期設定 */
    private final DATC001Svc datc001Svc;

    /**
     * CABS-B-DATQ001 查詢日期設定
     * 查詢最新日期設定
     *
     * @param req 請求物件
     * @return 日期設定資料
     * @throws DataNotFoundException 查無資料時拋出
     */
    @PostMapping("/queryLatest")
    public ResponseTemplate<DATQ001Tranrs> queryDateSettings(@RequestBody
                                                             RequestTemplate<EmptyTranrq> req) throws DataNotFoundException {

        DATQ001Tranrs tranrs = datq001Svc.queryDateSettings(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-DATC001 新增日期設定
     *
     * @param req 請求物件
     * @return 日期設定資料
     * @throws DataNotFoundException 查無資料時拋出
     * @throws RestException
     * @throws RequestValidException
     */
    @PostMapping("/create")
    public ResponseTemplate<EmptyTranrs> createDateSettings(@Valid
                                                            @RequestBody
                                                            RequestTemplate<DATC001Tranrq> req, Errors errors) throws DataNotFoundException, RestException, RequestValidException {

        validateRequest(req, errors);

        EmptyTranrs tranrs = datc001Svc.createDateSettings(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}
