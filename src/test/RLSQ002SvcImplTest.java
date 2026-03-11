package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.cathaybk.demo.dto.RLSQ002Tranrs;
import com.cathaybk.demo.dto.RLSQueryEntity;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;

/**
 * RLSQ002SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RLSQ002SvcImpl 單元測試")
class RLSQ002SvcImplTest {

    private static final String EFORM_A = "EFORM001";
    private static final String EFORM_B = "EFORM002";

    @Mock private SqlUtils      sqlUtils;
    @Mock private SqlAction     sqlAction;
    @Mock private MultipartFile mockFile;

    @InjectMocks
    private RLSQ002SvcImpl rlsq002SvcImpl;

    // ── 輔助方法 ──────────────────────────────────────────────

    /**
     * 建立含有指定 eformId 的 Excel（.xlsx）byte array。
     * 第 0 列為標頭，從第 1 列起寫入 eformIds。
     */
    private byte[] buildExcelBytes(List<String> eformIds) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // 第 0 列：標頭（應被略過）
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("聯繫單號");

            // 從第 1 列起寫入 eformIds
            for (int i = 0; i < eformIds.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(eformIds.get(i));
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /** 設定 mockFile 回傳指定 bytes 的 inputStream */
    private void mockFileInputStream(byte[] bytes) throws IOException {
        when(mockFile.getInputStream())
                .thenReturn(new ByteArrayInputStream(bytes));
    }

    /** 設定 SQL 查詢 mock */
    private void mockSqlQuery(List<RLSQueryEntity> result) throws IOException {
        when(sqlUtils.getDynamicQuerySql(eq(RLSQ002SvcImpl.RLSQ002_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(),
                eq(RLSQueryEntity.class), eq(false)))
                .thenReturn(result);
    }

    /** 建立一筆 RLSQueryEntity 測試資料 */
    private RLSQueryEntity buildQueryEntity(String eformId) {
        RLSQueryEntity e = new RLSQueryEntity();
        e.setEformId(eformId);
        e.setExecuteDate("2026/03/01");
        e.setEndDate("2026/03/31");
        e.setCreatedEmpid("EMP001");
        e.setCreatedEmpName("陳阿仁");
        e.setCreatedDept("IT");
        e.setStatus("Y");
        e.setChangeDetails("變更說明");
        e.setApid("APID001");
        e.setSysName("測試系統");
        return e;
    }

    @Test
    @DisplayName("成功匯入 - Excel 含多筆 eformId，回傳對應清單")
    void shouldImportExcelAndReturnReleaseList() throws IOException {
        // Given: 建立含兩筆 eformId 的 Excel，並模擬資料庫回傳結果
        byte[] excelBytes = buildExcelBytes(List.of(EFORM_A, EFORM_B));
        mockFileInputStream(excelBytes);
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A), buildQueryEntity(EFORM_B)));

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證回傳 items 筆數與第一筆 eformId 正確
        assertNotNull(result, "回傳結果不應為 null");
        assertNotNull(result.getItems(), "items 不應為 null");
        assertEquals(2, result.getItems().size(), "應回傳 2 筆");
        assertEquals(EFORM_A, result.getItems().get(0).getEformId(), "第一筆 eformId 應正確");
        assertEquals(EFORM_B, result.getItems().get(1).getEformId(), "第二筆 eformId 應正確");
    }

    @Test
    @DisplayName("成功匯入 - Excel 只有標頭列，無資料列時回傳空清單")
    void shouldReturnEmptyItemsWhenExcelHasOnlyHeader() throws IOException {
        // Given: 建立只有標頭、無資料列的 Excel
        byte[] excelBytes = buildExcelBytes(Collections.emptyList());
        mockFileInputStream(excelBytes);

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證解析不到任何 eformId 時直接回傳空清單，不執行 SQL 查詢
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result.getItems().isEmpty(), "無資料列時 items 應為空清單");
        verify(sqlAction, never()).queryForListVO(anyString(), anyMap(), any(), anyBoolean());
    }

    @Test
    @DisplayName("成功匯入 - SQL 查詢回傳空清單時回傳空 items")
    void shouldReturnEmptyItemsWhenSqlResultIsEmpty() throws IOException {
        // Given: Excel 含有 eformId，但資料庫查無對應資料
        byte[] excelBytes = buildExcelBytes(List.of(EFORM_A));
        mockFileInputStream(excelBytes);
        mockSqlQuery(Collections.emptyList());

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證回傳 items 為空清單
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result.getItems().isEmpty(), "SQL 查無資料時 items 應為空清單");
    }

    @Test
    @DisplayName("成功匯入 - SQL 查詢回傳 null 時回傳空 items")
    void shouldReturnEmptyItemsWhenSqlResultIsNull() throws IOException {
        // Given: Excel 含有 eformId，但資料庫查詢回傳 null
        byte[] excelBytes = buildExcelBytes(List.of(EFORM_A));
        mockFileInputStream(excelBytes);
        mockSqlQuery(null);

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證回傳 items 為空清單
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result.getItems().isEmpty(), "SQL 回傳 null 時 items 應為空清單");
    }

    @Test
    @DisplayName("成功匯入 - Excel 中重複的 eformId 應自動去重")
    void shouldDeduplicateEformIdsFromExcel() throws IOException {
        // Given: Excel 含有重複的 eformId
        byte[] excelBytes = buildExcelBytes(List.of(EFORM_A, EFORM_A, EFORM_B));
        mockFileInputStream(excelBytes);
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A), buildQueryEntity(EFORM_B)));

        // When: 執行匯入 Excel 並產生放行清單
        rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證傳入 SQL 的 eformIds 只有 2 筆（去重後）
        verify(sqlUtils).getDynamicQuerySql(eq(RLSQ002SvcImpl.RLSQ002_QUERY_001),
                argThat(map -> {
                    @SuppressWarnings("unchecked")
                    List<String> ids = (List<String>) ((java.util.Map<?, ?>) map).get("eformIds");
                    return ids != null && ids.size() == 2
                            && ids.contains(EFORM_A) && ids.contains(EFORM_B);
                }));
    }

    @Test
    @DisplayName("成功匯入 - Excel 中前後空白的 eformId 應被 trim")
    void shouldTrimEformIdsFromExcel() throws IOException {
        // Given: Excel 中 eformId 含有前後空白（由 cell.toString().trim() 處理）
        byte[] excelBytes = buildExcelBytes(List.of("  " + EFORM_A + "  "));
        mockFileInputStream(excelBytes);
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A)));

        // When: 執行匯入 Excel 並產生放行清單
        rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證傳入 SQL 的 eformIds 已去除空白
        verify(sqlUtils).getDynamicQuerySql(eq(RLSQ002SvcImpl.RLSQ002_QUERY_001),
                argThat(map -> {
                    @SuppressWarnings("unchecked")
                    List<String> ids = (List<String>) ((java.util.Map<?, ?>) map).get("eformIds");
                    return ids != null && ids.size() == 1 && ids.get(0).equals(EFORM_A);
                }));
    }

    @Test
    @DisplayName("成功匯入 - Excel 中空白 cell 應被略過")
    void shouldSkipBlankCellsInExcel() throws IOException {
        // Given: 建立含空白 cell（值為空字串）的 Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            sheet.createRow(0).createCell(0).setCellValue("標頭");  // header
            sheet.createRow(1).createCell(0).setCellValue("");       // 空白 cell，應略過
            sheet.createRow(2).createCell(0).setCellValue(EFORM_A); // 有效 eformId
            workbook.write(out);
            mockFileInputStream(out.toByteArray());
        }

        mockSqlQuery(List.of(buildQueryEntity(EFORM_A)));

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證空白 cell 被略過，只有 EFORM_A 被處理
        assertEquals(1, result.getItems().size(), "空白 cell 應被略過，只有 1 筆有效資料");
    }

    @Test
    @DisplayName("成功匯入 - Excel 中 row 為 null 應被略過")
    void shouldSkipNullRowsInExcel() throws IOException {
        // Given: 建立含空列（createRow 未呼叫造成 null row）的 Excel
        // 注意：POI 的 getRow(idx) 在未建立的列回傳 null；此處模擬空列後接有效列
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            sheet.createRow(0).createCell(0).setCellValue("標頭");  // header
            // 跳過 rowIdx=1（不建立，getRow 回傳 null）
            sheet.createRow(2).createCell(0).setCellValue(EFORM_A); // 有效列
            workbook.write(out);
            mockFileInputStream(out.toByteArray());
        }

        mockSqlQuery(List.of(buildQueryEntity(EFORM_A)));

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證 null 列被略過，只有 EFORM_A 被處理
        assertEquals(1, result.getItems().size(), "null row 應被略過，只有 1 筆有效資料");
    }

    @Test
    @DisplayName("成功匯入 - Excel 中 cell 為 null 應被略過")
    void shouldSkipNullCellsInExcel() throws IOException {
        // Given: 建立 A 欄 cell 為 null 的列（只建立 row，不建立 cell）
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            sheet.createRow(0).createCell(0).setCellValue("標頭");  // header
            sheet.createRow(1);                                      // row 存在但 cell 為 null
            sheet.createRow(2).createCell(0).setCellValue(EFORM_A); // 有效列
            workbook.write(out);
            mockFileInputStream(out.toByteArray());
        }

        mockSqlQuery(List.of(buildQueryEntity(EFORM_A)));

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證 null cell 被略過，只有 EFORM_A 被處理
        assertEquals(1, result.getItems().size(), "null cell 應被略過，只有 1 筆有效資料");
    }

    @Test
    @DisplayName("成功匯入 - 驗證 RLSQTranrsItem 各欄位對應正確")
    void shouldMapAllItemFieldsCorrectly() throws IOException {
        // Given: 建立 Excel 並準備包含完整欄位的一筆查詢資料
        byte[] excelBytes = buildExcelBytes(List.of(EFORM_A));
        mockFileInputStream(excelBytes);
        RLSQueryEntity entity = buildQueryEntity(EFORM_A);
        mockSqlQuery(List.of(entity));

        // When: 執行匯入 Excel 並產生放行清單
        RLSQ002Tranrs result = rlsq002SvcImpl.importExcelAndPreview(mockFile);

        // Then: 驗證 item 各欄位與來源 entity 對應正確
        assertEquals(1, result.getItems().size());
        RLSQTranrsItem item = result.getItems().get(0);
        assertEquals(entity.getEformId(),        item.getEformId(),        "eformId 應正確");
        assertEquals(entity.getExecuteDate(),    item.getExecuteDate(),    "executeDate 應正確");
        assertEquals(entity.getEndDate(),        item.getEndDate(),        "endDate 應正確");
        assertEquals(entity.getCreatedEmpid(),   item.getCreatedEmpid(),   "createdEmpid 應正確");
        assertEquals(entity.getCreatedEmpName(), item.getCreatedEmpName(), "createdEmpName 應正確");
        assertEquals(entity.getCreatedDept(),    item.getCreatedDept(),    "createdDept 應正確");
        assertEquals(entity.getStatus(),         item.getStatus(),         "status 應正確");
        assertEquals(entity.getChangeDetails(),  item.getChangeDetails(),  "changeDetails 應正確");
        assertEquals(entity.getApid(),           item.getApid(),           "apid 應正確");
        assertEquals(entity.getSysName(),        item.getSysName(),        "sysName 應正確");
    }
}