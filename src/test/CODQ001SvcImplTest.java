package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;
import com.cathaybk.demo.entity.CodeLookupEntity;
import com.cathaybk.demo.repository.CodeLookupRepo;

/**
 * CODQ001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CODQ001SvcImpl 單元測試")
class CODQ001SvcImplTest {

    private static final String CODE_TYPE_CURRENCY = "CURRENCY";
    private static final String CODE_VALUE_USD = "USD";
    private static final String CODE_NAME_US_DOLLAR = "美元";
    private static final String CODE_VALUE_TWD = "TWD";
    private static final String CODE_NAME_TWD = "台幣";

    @Mock
    private CodeLookupRepo codeLookupRepo;

    @InjectMocks
    private CODQ001SvcImpl codq001SvcImpl;

    private CODQ001Tranrq tranrq;

    @BeforeEach
    void setUp() {
        tranrq = new CODQ001Tranrq();
        tranrq.setCodeType(CODE_TYPE_CURRENCY);
    }

    @Test
    @DisplayName("成功查詢 - 回傳多筆代碼清單")
    void shouldReturnCodeListSuccessfully() {
        // Given: 準備兩筆代碼資料，模擬資料庫回傳結果
        CodeLookupEntity e1 = new CodeLookupEntity();
        e1.setCodeValue(CODE_VALUE_TWD);
        e1.setCodeName(CODE_NAME_TWD);

        CodeLookupEntity e2 = new CodeLookupEntity();
        e2.setCodeValue(CODE_VALUE_USD);
        e2.setCodeName(CODE_NAME_US_DOLLAR);

        when(codeLookupRepo.findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY))
                .thenReturn(List.of(e1, e2));

        // When: 執行代碼查詢
        CODQ001Tranrs result = codq001SvcImpl.queryCodeList(tranrq);

        // Then: 驗證回傳結果筆數與各欄位內容
        assertNotNull(result, "回傳結果不應為 null");
        assertNotNull(result.getItems(), "items 不應為 null");
        assertEquals(2, result.getItems().size(), "應回傳 2 筆");
        assertEquals(CODE_VALUE_TWD, result.getItems().get(0).getCodeValue(), "第一筆 codeValue 應正確");
        assertEquals(CODE_NAME_TWD, result.getItems().get(0).getCodeName(), "第一筆 codeName 應正確");
        assertEquals(CODE_VALUE_USD, result.getItems().get(1).getCodeValue(), "第二筆 codeValue 應正確");
        assertEquals(CODE_NAME_US_DOLLAR, result.getItems().get(1).getCodeName(), "第二筆 codeName 應正確");

        verify(codeLookupRepo, times(1)).findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY);
    }

    @Test
    @DisplayName("成功查詢 - 查無資料時回傳空清單")
    void shouldReturnEmptyListWhenNoData() {
        // Given: 模擬資料庫查無任何代碼資料
        when(codeLookupRepo.findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY))
                .thenReturn(Collections.emptyList());

        // When: 執行代碼查詢
        CODQ001Tranrs result = codq001SvcImpl.queryCodeList(tranrq);

        // Then: 驗證回傳結果不為 null 且 items 為空清單
        assertNotNull(result, "回傳結果不應為 null");
        assertNotNull(result.getItems(), "items 不應為 null");
        assertTrue(result.getItems().isEmpty(), "items 應為空清單");

        verify(codeLookupRepo, times(1)).findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY);
    }

    @Test
    @DisplayName("成功查詢 - 只回傳一筆資料")
    void shouldReturnSingleItemList() {
        // Given: 模擬資料庫只回傳一筆代碼資料
        CodeLookupEntity entity = new CodeLookupEntity();
        entity.setCodeValue(CODE_VALUE_USD);
        entity.setCodeName(CODE_NAME_US_DOLLAR);

        when(codeLookupRepo.findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY))
                .thenReturn(List.of(entity));

        // When: 執行代碼查詢
        CODQ001Tranrs result = codq001SvcImpl.queryCodeList(tranrq);

        // Then: 驗證 items 只有一筆且欄位正確
        assertNotNull(result);
        assertEquals(1, result.getItems().size(), "應回傳 1 筆");
        assertEquals(CODE_VALUE_USD, result.getItems().get(0).getCodeValue());
    }

    @Test
    @DisplayName("成功查詢 - 傳入不同 codeType 查詢")
    void shouldQueryWithDifferentCodeType() {
        // Given: 設定不同的 codeType，模擬查詢另一類代碼
        String codeType = "STATUS";
        tranrq.setCodeType(codeType);

        CodeLookupEntity entity = new CodeLookupEntity();
        entity.setCodeValue("01");
        entity.setCodeName("啟用");

        when(codeLookupRepo.findByCodeTypeOrderByCodeValueAsc(codeType))
                .thenReturn(List.of(entity));

        // When: 執行代碼查詢
        CODQ001Tranrs result = codq001SvcImpl.queryCodeList(tranrq);

        // Then: 驗證以正確的 codeType 查詢，且回傳資料正確
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("01", result.getItems().get(0).getCodeValue());
        assertEquals("啟用", result.getItems().get(0).getCodeName());

        verify(codeLookupRepo, times(1)).findByCodeTypeOrderByCodeValueAsc(codeType);
        verify(codeLookupRepo, never()).findByCodeTypeOrderByCodeValueAsc(CODE_TYPE_CURRENCY);
    }
}