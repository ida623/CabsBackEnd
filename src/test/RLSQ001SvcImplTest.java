package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.RLSQ001Tranrq;
import com.cathaybk.demo.dto.RLSQ001Tranrs;
import com.cathaybk.demo.dto.RLSQueryEntity;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;

/**
 * RLSQ001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RLSQ001SvcImpl 單元測試")
class RLSQ001SvcImplTest {

    private static final String EFORM_A = "EFORM001";
    private static final String EFORM_B = "EFORM002";

    @Mock private SqlUtils   sqlUtils;
    @Mock private SqlAction  sqlAction;

    @InjectMocks
    private RLSQ001SvcImpl rlsq001SvcImpl;

    private RLSQ001Tranrq tranrq;

    @BeforeEach
    void setUp() {
        tranrq = new RLSQ001Tranrq();
        tranrq.setEformIds(List.of(EFORM_A, EFORM_B));
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

    /** 設定 SQL 查詢 mock */
    private void mockSqlQuery(List<RLSQueryEntity> result) throws IOException {
        when(sqlUtils.getDynamicQuerySql(eq(RLSQ001SvcImpl.RLSQ001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(),
                eq(RLSQueryEntity.class), eq(false)))
                .thenReturn(result);
    }

    @Test
    @DisplayName("成功查詢 - 回傳多筆放行清單")
    void shouldReturnReleaseListSuccessfully() throws IOException {
        // Given: 模擬資料庫回傳兩筆放行清單資料
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A), buildQueryEntity(EFORM_B)));

        // When: 執行產生放行清單
        RLSQ001Tranrs result = rlsq001SvcImpl.previewReleaseList(tranrq);

        // Then: 驗證回傳 items 筆數與欄位內容
        assertNotNull(result, "回傳結果不應為 null");
        assertNotNull(result.getItems(), "items 不應為 null");
        assertEquals(2, result.getItems().size(), "應回傳 2 筆");
        assertEquals(EFORM_A, result.getItems().get(0).getEformId(), "第一筆 eformId 應正確");
        assertEquals(EFORM_B, result.getItems().get(1).getEformId(), "第二筆 eformId 應正確");
    }

    @Test
    @DisplayName("成功查詢 - resultList 為空清單時回傳空 items")
    void shouldReturnEmptyItemsWhenResultListIsEmpty() throws IOException {
        // Given: 模擬資料庫查無資料，回傳空清單
        mockSqlQuery(Collections.emptyList());

        // When: 執行產生放行清單
        RLSQ001Tranrs result = rlsq001SvcImpl.previewReleaseList(tranrq);

        // Then: 驗證回傳 items 為空清單
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result.getItems().isEmpty(), "查無資料時 items 應為空清單");
    }

    @Test
    @DisplayName("成功查詢 - resultList 為 null 時回傳空 items")
    void shouldReturnEmptyItemsWhenResultListIsNull() throws IOException {
        // Given: 模擬資料庫查詢回傳 null
        mockSqlQuery(null);

        // When: 執行產生放行清單
        RLSQ001Tranrs result = rlsq001SvcImpl.previewReleaseList(tranrq);

        // Then: 驗證回傳 items 為空清單
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result.getItems().isEmpty(), "resultList 為 null 時 items 應為空清單");
    }

    @Test
    @DisplayName("成功查詢 - eformIds 重複時應自動去重再查詢")
    void shouldDeduplicateEformIdsBeforeQuery() throws IOException {
        // Given: 傳入含重複值的 eformIds
        tranrq.setEformIds(List.of(EFORM_A, EFORM_A, EFORM_B));
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A), buildQueryEntity(EFORM_B)));

        // When: 執行產生放行清單
        rlsq001SvcImpl.previewReleaseList(tranrq);

        // Then: 驗證傳入 SQL 的 queryMap 中 eformIds 已去重為 2 筆
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(sqlUtils).getDynamicQuerySql(eq(RLSQ001SvcImpl.RLSQ001_QUERY_001),
                argThat(map -> {
                    @SuppressWarnings("unchecked")
                    List<String> ids = (List<String>) ((java.util.Map<?, ?>) map).get("eformIds");
                    return ids != null && ids.size() == 2
                            && ids.contains(EFORM_A) && ids.contains(EFORM_B);
                }));
    }

    @Test
    @DisplayName("成功查詢 - eformIds 前後空白應被 trim 處理")
    void shouldTrimEformIdsBeforeQuery() throws IOException {
        // Given: eformId 含有前後空白
        tranrq.setEformIds(List.of("  " + EFORM_A, EFORM_B + "  "));
        mockSqlQuery(List.of(buildQueryEntity(EFORM_A), buildQueryEntity(EFORM_B)));

        // When: 執行產生放行清單
        rlsq001SvcImpl.previewReleaseList(tranrq);

        // Then: 驗證傳入 SQL 的 queryMap 中 eformIds 已去除空白
        verify(sqlUtils).getDynamicQuerySql(eq(RLSQ001SvcImpl.RLSQ001_QUERY_001),
                argThat(map -> {
                    @SuppressWarnings("unchecked")
                    List<String> ids = (List<String>) ((java.util.Map<?, ?>) map).get("eformIds");
                    return ids != null
                            && ids.contains(EFORM_A)
                            && ids.contains(EFORM_B)
                            && ids.stream().noneMatch(id -> id.startsWith(" ") || id.endsWith(" "));
                }));
    }

    @Test
    @DisplayName("成功查詢 - 驗證 RLSQTranrsItem 各欄位對應正確")
    void shouldMapAllItemFieldsCorrectly() throws IOException {
        // Given: 準備包含完整欄位的一筆查詢資料
        RLSQueryEntity entity = buildQueryEntity(EFORM_A);
        mockSqlQuery(List.of(entity));

        // When: 執行產生放行清單
        RLSQ001Tranrs result = rlsq001SvcImpl.previewReleaseList(tranrq);

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
