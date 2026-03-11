package com.cathaybk.demo.controller;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.RLSC001Svc;
import com.cathaybk.demo.service.RLSQ001Svc;
import com.cathaybk.demo.service.RLSQ002Svc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/release")
@RequiredArgsConstructor
public class ReleaseController extends BaseController {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** CABS-B-RLSC001 放行處理 */
    private final RLSC001Svc rlsc001Svc;

    /** CABS-B-RLSQ001 產生放行清單 */
    private final RLSQ001Svc rlsq001Svc;

    /** CABS-B-RLSQ002 匯入EXCEL產生放行清單 */
    private final RLSQ002Svc rlsq002Svc;

    /**
     * CABS-B-RLSC001 放行處理
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return iContact 回寫結果
     * @throws IOException           SQL 讀取失敗時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/actions")
    public ResponseTemplate<RLSC001Tranrs> processRelease(
            @Valid @RequestBody RequestTemplate<RLSC001Tranrq> req,
            Errors errors) throws IOException, RequestValidException {

        validateRequest(req, errors);

        RLSC001Tranrs tranrs = rlsc001Svc.processRelease(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-RLSQ001 產生放行清單
     *
     * @param req    請求物件
     * @param errors 驗證結果
     * @return 放行預覽清單
     * @throws IOException           SQL 讀取失敗時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping("/preview")
    public ResponseTemplate<RLSQ001Tranrs> previewReleaseList(
            @Valid @RequestBody RequestTemplate<RLSQ001Tranrq> req,
            Errors errors) throws IOException, RequestValidException {

        validateRequest(req, errors);

        RLSQ001Tranrs tranrs = rlsq001Svc.previewReleaseList(req.getTranrq());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * CABS-B-RLSQ002 匯入EXCEL產生放行清單
     * Content-Type: multipart/form-data
     *
     * @param mwHeaderJson MWHEADER JSON 字串
     * @param file         Excel 檔案（.xlsx）
     * @return 放行預覽清單
     * @throws IOException           Excel 解析或 SQL 讀取失敗時拋出
     * @throws RequestValidException 欄位驗證失敗時拋出
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseTemplate<RLSQ002Tranrs> importExcel(
            @Valid @RequestBody RequestTemplate<?> req,
            @RequestPart("file") MultipartFile file,
            Errors errors) throws IOException, RequestValidException {

        validateRequest(req, errors);

        RLSQ002Tranrs tranrs = rlsq002Svc.importExcelAndPreview(file);

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}