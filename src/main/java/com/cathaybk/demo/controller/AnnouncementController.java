package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController extends BaseController {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** CABS-B-ANNC001 新增公告 */
    private final ANNC001Svc annc001Svc;

    /** CABS-B-ANND001 刪除公告 */
    private final ANND001Svc annd001Svc;

    /** CABS-B-ANNQ001 查詢公告清單 */
    private final ANNQ001Svc annq001Svc;

    /** CABS-B-ANNQ002 查詢公告 */
    private final ANNQ002Svc annq002Svc;

    /** CABS-B-ANNU001 修改公告 */
    private final ANNU001Svc annu001Svc;

    /**
     * CABS-B-ANNC001 新增公告
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 空下行
     * @throws RestException         未取得使用者資訊時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/create")
    public ResponseTemplate<EmptyTranrs> createAnnouncement(
            @Valid @RequestBody RequestTemplate<ANNC001Tranrq> req,
            Errors errors) throws RestException, RequestValidException {

        validateRequest(req, errors);

        EmptyTranrs tranrs = annc001Svc.createAnnouncement(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-ANND001 刪除公告
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 空下行
     * @throws RestException         未取得使用者資訊時拋出
     * @throws DataNotFoundException 查無資料時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/delete")
    public ResponseTemplate<EmptyTranrs> deleteAnnouncement(
            @Valid @RequestBody RequestTemplate<ANND001Tranrq> req,
            Errors errors) throws RestException, DataNotFoundException, RequestValidException {

        validateRequest(req, errors);

        EmptyTranrs tranrs = annd001Svc.deleteAnnouncement(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-ANNQ001 查詢公告清單
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 公告清單（分頁）
     * @throws IOException           SQL 讀取失敗時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/queryList")
    public ResponseTemplate<ANNQ001Tranrs> queryAnnouncementList(
            @Valid @RequestBody RequestTemplate<ANNQ001Tranrq> req,
            Errors errors) throws IOException, RequestValidException {

        validateRequest(req, errors);

        ANNQ001Tranrs tranrs = annq001Svc.queryAnnouncementList(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-ANNQ002 查詢公告（單筆）
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 公告單筆資料
     * @throws DataNotFoundException 查無資料時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/query")
    public ResponseTemplate<ANNQ002Tranrs> queryAnnouncement(
            @Valid @RequestBody RequestTemplate<ANNQ002Tranrq> req,
            Errors errors) throws DataNotFoundException, RequestValidException {

        validateRequest(req, errors);

        ANNQ002Tranrs tranrs = annq002Svc.queryAnnouncement(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-ANNU001 修改公告
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 空下行
     * @throws RestException         未取得使用者資訊時拋出
     * @throws DataNotFoundException 查無資料時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/update")
    public ResponseTemplate<EmptyTranrs> updateAnnouncement(
            @Valid @RequestBody RequestTemplate<ANNU001Tranrq> req,
            Errors errors) throws RestException, DataNotFoundException, RequestValidException {

        validateRequest(req, errors);

        EmptyTranrs tranrs = annu001Svc.updateAnnouncement(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}