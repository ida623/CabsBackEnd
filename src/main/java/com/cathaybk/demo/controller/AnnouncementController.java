package com.cathaybk.demo.controller;

import java.io.IOException;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.service.*;
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
@RequestMapping(value = "/api/announcements", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class AnnouncementController extends BaseController {

    /** ANNC001Svc */
    private final ANNC001Svc theANNC001Svc;

    /** ANNQ001Svc */
    private final ANNQ001Svc theANNQ001Svc;

    /** ANNQ002Svc */
    private final ANNQ002Svc theANNQ002Svc;

    /** ANNU001Svc */
    private final ANNU001Svc theANNU001Svc;

    /** ANND001Svc */
    private final ANND001Svc theANND001Svc;

    /**
     * CABS-B-ANNC001 新增公告
     *
     * @param req
     * @return
     * @throws IOException
     * @throws RestException
     * @throws RequestValidException
     */
    @PostMapping("/create")
    @Operation(summary = "CABS-B-ANNC001 新增公告")
    public ResponseTemplate<EmptyTranrs> create(@Valid @RequestBody RequestTemplate<ANNC001Tranrq> req, Errors err)
            throws IOException, RestException, RequestValidException {
        validateRequest(err);
        return theANNC001Svc.createAnnouncement(req);
    }

    /**
     * CABS-B-ANNQ001 查詢公告清單
     *
     * @param req
     * @return
     * @throws IOException
     * @throws RequestValidException
     */
    @PostMapping("/queryList")
    @Operation(summary = "CABS-B-ANNQ001 查詢公告清單")
    public ResponseTemplate<ANNQ001Tranrs> queryList(@Valid @RequestBody RequestTemplate<ANNQ001Tranrq> req, Errors err)
            throws IOException, RequestValidException {
        validateRequest(err);
        return theANNQ001Svc.queryList(req);
    }

    /**
     * CABS-B-ANNQ002 查詢公告
     *
     * @param req
     * @return
     * @throws DataNotFoundException
     * @throws RequestValidException
     */
    @PostMapping("/query")
    @Operation(summary = "CABS-B-ANNQ002 查詢公告")
    public ResponseTemplate<ANNQ002Tranrs> query(@Valid @RequestBody RequestTemplate<ANNQ002Tranrq> req, Errors err)
            throws DataNotFoundException, RequestValidException {
        validateRequest(err);
        return theANNQ002Svc.query(req);
    }

    /**
     * CABS-B-ANNU001 修改公告
     *
     * @param req
     * @return
     * @throws DataNotFoundException
     * @throws RestException
     * @throws RequestValidException
     */
    @PostMapping("/update")
    @Operation(summary = "CABS-B-ANNU001 修改公告")
    public ResponseTemplate<EmptyTranrs> update(@Valid @RequestBody RequestTemplate<ANNU001Tranrq> req, Errors err)
            throws DataNotFoundException, RestException, RequestValidException {
        validateRequest(err);
        return theANNU001Svc.update(req);
    }

    /**
     * CABS-B-ANND001 刪除公告
     *
     * @param req
     * @return
     * @throws DataNotFoundException
     * @throws RestException
     * @throws RequestValidException
     */
    @PostMapping("/delete")
    @Operation(summary = "CABS-B-ANND001 刪除公告")
    public ResponseTemplate<EmptyTranrs> delete(@Valid @RequestBody RequestTemplate<ANND001Tranrq> req, Errors err)
            throws DataNotFoundException, RestException, RequestValidException {
        validateRequest(err);
        return theANND001Svc.delete(req);
    }

}