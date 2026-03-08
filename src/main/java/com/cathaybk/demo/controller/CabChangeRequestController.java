package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.service.CCRC001Svc;
import com.cathaybk.demo.service.CCRQ001Svc;
import com.cathaybk.demo.service.CCRQ002Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CAB Change Request Controller
 *
 * @author 00550396
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/cab-change-requests", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CabChangeRequestController extends BaseController {

    /** CCRQ001Svc */
    private final CCRQ001Svc theCCRQ001Svc;

    /** CCRC001Svc */
    private final CCRC001Svc theCCRC001Svc;

    /** CCRE001Svc */
    private final CCRE001Svc theCCRE001Svc;

    /** CCRQ002Svc */
    private final CCRQ002Svc theCCRQ002Svc;

    /**
     * CABS-B-CCRQ001 查詢聯繫單列表
     *
     * @param req
     * @param err
     * @return
     * @throws Exception
     */
    @PostMapping("/queryList")
    @Operation(summary = "CABS-B-CCRQ001 查詢聯繫單列表")
    public ResponseTemplate<CCRQ001Tranrs> queryList(@Valid @RequestBody RequestTemplate<CCRQ001Tranrq> req,
                                                     Errors err) throws Exception {
        validateRequest(err);
        return theCCRQ001Svc.queryList(req);
    }

    /**
     * CABS-B-CCRC001 iContact聯繫單資料匯入
     *
     * @param req
     * @param err
     * @return
     * @throws Exception
     */
    @PostMapping("/sync")
    @Operation(summary = "CABS-B-CCRC001 iContact聯繫單資料匯入")
    public ResponseTemplate<EmptyTranrs> sync(@Valid @RequestBody RequestTemplate<CCRC001Tranrq> req,
                                              Errors err) throws Exception {
        validateRequest(err);
        return theCCRC001Svc.sync(req);
    }

    /**
     * CABS-B-CCRE001 匯出聯繫單清單
     *
     * @param req
     * @param err
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/exportExcel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "CABS-B-CCRE001 匯出聯繫單清單")
    public ResponseEntity<Resource> exportExcel(@Valid @RequestBody RequestTemplate<CCRE001Tranrq> req,
            Errors err) throws Exception {
        validateRequest(err);
        
        byte[] excelBytes = theCCRE001Svc.exportExcel(req);
        
        String fileName = "CABS_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excelBytes));
    }

    /**
     * CABS-B-CCRQ002 查詢聯繫單
     *
     * @param req
     * @param err
     * @return
     * @throws Exception
     */
    @PostMapping("/query")
    @Operation(summary = "CABS-B-CCRQ002 查詢聯繫單")
    public ResponseTemplate<CCRQ002Tranrs> query(@Valid @RequestBody RequestTemplate<CCRQ002Tranrq> req,
            Errors err) throws Exception {
        validateRequest(err);
        return theCCRQ002Svc.query(req);
    }

}