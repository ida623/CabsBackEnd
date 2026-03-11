package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
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

import com.cathaybk.demo.dto.ANNQ001TranrsEntity;
import com.cathaybk.demo.dto.ANNQ001Tranrq;
import com.cathaybk.demo.dto.ANNQ001Tranrs;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;

/**
 * ANNQ001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ANNQ001SvcImpl 單元測試")
class ANNQ001SvcImplTest {

    @Mock
    private SqlUtils sqlUtils;

    @Mock
    private SqlAction sqlAction;

    @InjectMocks
    private ANNQ001SvcImpl annq001SvcImpl;

    private ANNQ001Tranrq tranrq;

    @BeforeEach
    void setUp() {
        tranrq = new ANNQ001Tranrq();
        tranrq.setPageNumber(1);
        tranrq.setPageSize(10);
        tranrq.setSortDirection("DESC");
    }

    private ANNQ001TranrsEntity buildEntity(Long id, String content, long totalElements) {
        ANNQ001TranrsEntity e = new ANNQ001TranrsEntity();
        e.setId(id);
        e.setContent(content);
        e.setCreatedBy("陳阿仁");
        e.setCreatedAt(LocalDateTime.of(2026, 3, 1, 9, 0));
        e.setUpdatedBy("陳阿仁");
        e.setUpdatedAt(LocalDateTime.of(2026, 3, 2, 10, 0));
        e.setTotalElements(totalElements);
        return e;
    }

    @Test
    @DisplayName("成功查詢公告清單 - 回傳多筆資料")
    void shouldReturnAnnouncementListSuccessfully() throws IOException {
        // Given: 模擬資料庫回傳兩筆公告清單資料
        List<ANNQ001TranrsEntity> dbResult = List.of(
                buildEntity(1L, "公告一", 2L),
                buildEntity(2L, "公告二", 2L));

        when(sqlUtils.getDynamicQuerySql(eq(ANNQ001SvcImpl.ANNQ001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(dbResult);

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證分頁資訊與清單內容正確
        assertNotNull(result, "回傳結果不應為 null");
        assertEquals(1, result.getPageNumber(), "頁碼應正確");
        assertEquals(10, result.getPageSize(), "每頁筆數應正確");
        assertEquals(2L, result.getTotalElements(), "總筆數應正確");
        assertEquals(1, result.getTotalPages(), "總頁數應正確");
        assertEquals(2, result.getItems().size(), "清單筆數應正確");
        assertEquals(1L, result.getItems().get(0).getId(), "第一筆 ID 應正確");
        assertEquals("公告一", result.getItems().get(0).getContent(), "第一筆內容應正確");
        assertEquals(2L, result.getItems().get(1).getId(), "第二筆 ID 應正確");
    }

    @Test
    @DisplayName("成功查詢 - 查無資料時回傳空清單與 0 筆數")
    void shouldReturnEmptyResultWhenNoData() throws IOException {
        // Given: 模擬資料庫查詢回傳空清單
        when(sqlUtils.getDynamicQuerySql(eq(ANNQ001SvcImpl.ANNQ001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(Collections.emptyList());

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證分頁資訊歸零且清單為空
        assertNotNull(result);
        assertEquals(1, result.getPageNumber(), "頁碼應正確");
        assertEquals(10, result.getPageSize(), "每頁筆數應正確");
        assertEquals(0L, result.getTotalElements(), "總筆數應為 0");
        assertEquals(0, result.getTotalPages(), "總頁數應為 0");
        assertTrue(result.getItems().isEmpty(), "清單應為空");
    }

    @Test
    @DisplayName("成功查詢 - resultList 為 null 時回傳空清單")
    void shouldReturnEmptyResultWhenResultListIsNull() throws IOException {
        // Given: 模擬資料庫查詢回傳 null
        when(sqlUtils.getDynamicQuerySql(eq(ANNQ001SvcImpl.ANNQ001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(null);

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證回傳結果不為 null 且清單為空
        assertNotNull(result);
        assertEquals(0L, result.getTotalElements(), "總筆數應為 0");
        assertTrue(result.getItems().isEmpty(), "清單應為空");
    }

    @Test
    @DisplayName("成功查詢 - 驗證 offset 計算正確 (第 2 頁)")
    void shouldCalculateOffsetCorrectlyForPage2() throws IOException {
        // Given: 設定第 2 頁、每頁 5 筆，模擬資料庫回傳結果
        tranrq.setPageNumber(2);
        tranrq.setPageSize(5);

        List<ANNQ001TranrsEntity> dbResult = List.of(buildEntity(6L, "公告六", 12L));

        when(sqlUtils.getDynamicQuerySql(eq(ANNQ001SvcImpl.ANNQ001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(dbResult);

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證 queryMap 中 offset = (2-1) * 5 = 5
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(sqlUtils).getDynamicQuerySql(eq(ANNQ001SvcImpl.ANNQ001_QUERY_001), argThat(map -> {
            Object offset = map.get("offset");
            Object pageSize = map.get("pageSize");
            return Integer.valueOf(5).equals(offset) && Integer.valueOf(5).equals(pageSize);
        }));

        assertNotNull(result);
        assertEquals(2, result.getPageNumber(), "頁碼應為 2");
        assertEquals(3, result.getTotalPages(), "總頁數應為 ceil(12/5) = 3");
    }

    @Test
    @DisplayName("成功查詢 - 驗證 totalPages 無條件進位計算")
    void shouldCalculateTotalPagesWithCeiling() throws IOException {
        // Given: totalElements=11, pageSize=5，預期 totalPages=3（無條件進位）
        tranrq.setPageSize(5);
        List<ANNQ001TranrsEntity> dbResult = List.of(buildEntity(1L, "公告", 11L));

        when(sqlUtils.getDynamicQuerySql(anyString(), anyMap())).thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(dbResult);

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證 totalPages 為無條件進位的結果
        assertEquals(3, result.getTotalPages(), "11筆/每頁5筆，總頁數應為3");
    }

    @Test
    @DisplayName("成功查詢 - 驗證 items 各欄位對應正確")
    void shouldMapItemFieldsCorrectly() throws IOException {
        // Given: 準備一筆包含完整欄位的公告資料
        ANNQ001TranrsEntity entity = buildEntity(99L, "完整公告內容", 1L);
        entity.setCreatedBy("建立者");
        entity.setUpdatedBy("修改者");

        when(sqlUtils.getDynamicQuerySql(anyString(), anyMap())).thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(), eq(ANNQ001TranrsEntity.class), eq(false)))
                .thenReturn(List.of(entity));

        // When: 執行查詢公告清單
        ANNQ001Tranrs result = annq001SvcImpl.queryAnnouncementList(tranrq);

        // Then: 驗證 items 各欄位對應至回傳 DTO 的值正確
        assertEquals(1, result.getItems().size());
        var item = result.getItems().get(0);
        assertEquals(99L, item.getId(), "ID 應正確");
        assertEquals("完整公告內容", item.getContent(), "內容應正確");
        assertEquals("建立者", item.getCreatedBy(), "建立者應正確");
        assertEquals("修改者", item.getUpdatedBy(), "修改者應正確");
        assertNotNull(item.getCreatedAt(), "建立時間不應為 null");
        assertNotNull(item.getUpdatedAt(), "修改時間不應為 null");
    }
}