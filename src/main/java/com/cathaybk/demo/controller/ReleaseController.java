package com.cathaybk.demo.controller;

import java.io.IOException;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.service.RLSC001Svc;
import com.cathaybk.demo.service.RLSQ001Svc;
import com.cathaybk.demo.service.RLSQ002Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/release", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReleaseController extends BaseController {

    /** RLSQ001Svc */
    private final RLSQ001Svc theRLSQ001Svc;

    /** RLSQ002Svc */
    private final RLSQ002Svc theRLSQ002Svc;

    /** RLSC001Svc */
    private final RLSC001Svc theRLSC001Svc;

    /**
     * CABS-B-RLSQ001 產生放行清單
     *
     * @param req
     * @return
     * @throws RestException
     * @throws RequestValidException
     * @throws IOException
     */
    @PostMapping(value = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "CABS-B-RLSQ001 產生放行清單")
    public ResponseTemplate<RLSQ001Tranrs> preview(@Valid @RequestBody RequestTemplate<RLSQ001Tranrq> req, Errors err)
            throws RestException, RequestValidException, IOException {
        validateRequest(err);
        return theRLSQ001Svc.preview(req);
    }

    /**
     * CABS-B-RLSQ002 匯入EXCEL產生放行清單
     *
     * @param file
     * @return
     * @throws RestException
     * @throws RequestValidException
     * @throws IOException
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "CABS-B-RLSQ002 匯入EXCEL產生放行清單")
    public ResponseTemplate<RLSQ002Tranrs> importExcel(@RequestPart("file") MultipartFile file)
            throws RestException, RequestValidException, IOException {
        return theRLSQ002Svc.importExcel(file);
    }

    /**
     * CABS-B-RLSC001 放行處理
     *
     * @param req
     * @return
     * @throws RestException
     * @throws RequestValidException
     * @throws IOException
     */
    @PostMapping(value = "/actions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "CABS-B-RLSC001 放行處理")
    public ResponseTemplate<RLSC001Tranrs> actions(@Valid @RequestBody RequestTemplate<RLSC001Tranrq> req, Errors err)
            throws RestException, RequestValidException, IOException {
        validateRequest(err);
        return theRLSC001Svc.actions(req);
    }
}
