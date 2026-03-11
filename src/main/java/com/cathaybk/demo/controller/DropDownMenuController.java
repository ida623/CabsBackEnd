package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.CODQ001Svc;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drop-down-menu")
@RequiredArgsConstructor
public class DropDownMenuController extends BaseController {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** CABS-B-CODQ001 代碼查詢 */
    private final CODQ001Svc codq001Svc;

    /**
     * CABS-B-CODQ001 代碼查詢
     * 依傳入 codeType 回傳對應下拉代碼清單
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 代碼清單
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/queryList")
    public ResponseTemplate<CODQ001Tranrs> queryList(
            @Valid @RequestBody RequestTemplate<CODQ001Tranrq> req,
            Errors errors) throws RequestValidException {

        validateRequest(req, errors);

        CODQ001Tranrs tranrs = codq001Svc.queryCodeList(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}