package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.RLSC001BeforeStatusEntity;
import com.cathaybk.demo.dto.RLSC001Tranrq;
import com.cathaybk.demo.dto.RLSC001Tranrs;
import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.repository.ReleaseActionLogRepo;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import com.cathaybk.demo.user.UserObject;

/**
 * RLSC001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RLSC001SvcImpl 單元測試")
class RLSC001SvcImplTest {

    // 測試常數
    private static final String EMP_NAME       = "陳阿仁";
    private static final String ACTION_PASS    = "PASS";
    private static final String ACTION_REJECT  = "REJECT";
    private static final String REQ_SOURCE     = "WEB";
    private static final String APPROVED_AT    = "2026/03/15";
    private static final String EFORM_A        = "EFORM001";
    private static final String EFORM_B        = "EFORM002";
    private static final String ICONTACT_OK    = "0000";
    private static final String ICONTACT_ERR   = "9999";
    private static final String AFTER_Y        = "Y";
    private static final String AFTER_N        = "N";

    @Mock private ReleaseActionLogRepo releaseActionLogRepo;
    @Mock private UserObject           userObject;
    @Mock private SqlUtils             sqlUtils;
    @Mock private SqlAction            sqlAction;
    @Mock private IContactClient       iContactClient;

    @InjectMocks
    private RLSC001SvcImpl rlsc001SvcImpl;

    private RLSC001Tranrq validPassTranrq;

    @BeforeEach
    void setUp() {
        // 準備測試資料 - PASS 動作的上行請求
        validPassTranrq = new RLSC001Tranrq();
        validPassTranrq.setEformIds(List.of(EFORM_A, EFORM_B));
        validPassTranrq.setActionType(ACTION_PASS);
        validPassTranrq.setRequestSource(REQ_SOURCE);
        validPassTranrq.setApprovedAt(APPROVED_AT);

        // 設定操作者預設行為
        when(userObject.getEmpName()).thenReturn(EMP_NAME);
    }

    // ── 輔助方法 ──────────────────────────────────────────────

    /** 建立前次狀態實體 */
    private RLSC001BeforeStatusEntity buildStatusEntity(String eformId, String status) {
        RLSC001BeforeStatusEntity e = new RLSC001BeforeStatusEntity();
        e.setEformId(eformId);
        e.setStatus(status);
        return e;
    }

    /** 建立已儲存的 log 實體 */
    private ReleaseActionLogEntity buildSavedEntity(String eformId) {
        ReleaseActionLogEntity e = new ReleaseActionLogEntity();
        e.setEformId(eformId);
        return e;
    }

    /** 設定 SQL 查詢 mock：回傳 statusResultList */
    private void mockSqlQuery(List<RLSC001BeforeStatusEntity> statusList)
            throws IOException {
        when(sqlUtils.getDynamicQuerySql(eq(RLSC001SvcImpl.RLSC001_QUERY_001), anyMap()))
                .thenReturn("SELECT ...");
        when(sqlAction.queryForListVO(anyString(), anyMap(),
                eq(RLSC001BeforeStatusEntity.class), eq(false)))
                .thenReturn(statusList);
    }

    /** 設定 saveAll mock：原樣回傳 */
    private void mockSaveAll() {
        when(releaseActionLogRepo.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ── 測試案例 ──────────────────────────────────────────────

    @Test
    @DisplayName("成功放行 - PASS 動作，iContact 回傳 0000")
    void shouldProcessPassSuccessfully() throws IOException, RequestValidException {
        // Given: 模擬前次狀態查詢結果與 iContact 呼叫成功
        List<RLSC001BeforeStatusEntity> statusList = List.of(
                buildStatusEntity(EFORM_A, "N"),
                buildStatusEntity(EFORM_B, "Y"));
        mockSqlQuery(statusList);
        mockSaveAll();

        CabsListO001Tranrs iContactRs = new CabsListO001Tranrs(ICONTACT_OK, "交易成功");
        when(iContactClient.callCabsListO001(any())).thenReturn(iContactRs);

        // When: 執行放行處理
        RLSC001Tranrs result = rlsc001SvcImpl.processRelease(validPassTranrq);

        // Then: 驗證回傳結果為成功
        assertNotNull(result, "回傳結果不應為 null");
        assertEquals(AFTER_Y, result.getSuccess(), "PASS 且 iContact 成功，success 應為 Y");
        assertEquals(ICONTACT_OK, result.getReturnCode(), "returnCode 應為 0000");
        assertEquals("交易成功", result.getReturnMsg(), "returnMsg 應正確");

        // Then: 驗證 saveAll 被呼叫兩次（第一次寫 log，第二次更新 iContact 結果）
        verify(releaseActionLogRepo, times(2)).saveAll(anyList());
    }

    @Test
    @DisplayName("成功放行 - REJECT 動作，afterStatus 應為 N")
    void shouldProcessRejectWithAfterStatusN() throws IOException, RequestValidException {
        // Given: 準備 REJECT 動作的請求
        RLSC001Tranrq rejectTranrq = new RLSC001Tranrq();
        rejectTranrq.setEformIds(List.of(EFORM_A));
        rejectTranrq.setActionType(ACTION_REJECT);
        rejectTranrq.setRequestSource(REQ_SOURCE);
        rejectTranrq.setApprovedAt(APPROVED_AT);

        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "Y")));
        mockSaveAll();

        CabsListO001Tranrs iContactRs = new CabsListO001Tranrs(ICONTACT_OK, "交易成功");
        when(iContactClient.callCabsListO001(any())).thenReturn(iContactRs);

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(rejectTranrq);

        // Then: 驗證儲存的 log 中 afterStatus 為 N
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        List<ReleaseActionLogEntity> firstSave = captor.getAllValues().get(0);
        assertEquals(AFTER_N, firstSave.get(0).getAfterStatus(), "REJECT 動作的 afterStatus 應為 N");
    }

    @Test
    @DisplayName("成功放行 - eformIds 重複時應自動去重")
    void shouldDeduplicateEformIds() throws IOException, RequestValidException {
        // Given: 傳入含重複值的 eformIds
        RLSC001Tranrq dupTranrq = new RLSC001Tranrq();
        dupTranrq.setEformIds(List.of(EFORM_A, EFORM_A, EFORM_B));
        dupTranrq.setActionType(ACTION_PASS);
        dupTranrq.setRequestSource(REQ_SOURCE);
        dupTranrq.setApprovedAt(APPROVED_AT);

        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N"), buildStatusEntity(EFORM_B, "N")));
        mockSaveAll();

        CabsListO001Tranrs iContactRs = new CabsListO001Tranrs(ICONTACT_OK, "交易成功");
        when(iContactClient.callCabsListO001(any())).thenReturn(iContactRs);

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(dupTranrq);

        // Then: 驗證儲存的 log 只有 2 筆（去重後）
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        List<ReleaseActionLogEntity> firstSave = captor.getAllValues().get(0);
        assertEquals(2, firstSave.size(), "去重後應只儲存 2 筆");
    }

    @Test
    @DisplayName("成功放行 - eformIds 前後空白應被 trim 處理")
    void shouldTrimEformIds() throws IOException, RequestValidException {
        // Given: eformId 含有前後空白
        RLSC001Tranrq trimTranrq = new RLSC001Tranrq();
        trimTranrq.setEformIds(List.of("  " + EFORM_A + "  ", " " + EFORM_B));
        trimTranrq.setActionType(ACTION_PASS);
        trimTranrq.setRequestSource(REQ_SOURCE);
        trimTranrq.setApprovedAt(APPROVED_AT);

        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N"), buildStatusEntity(EFORM_B, "N")));
        mockSaveAll();

        CabsListO001Tranrs iContactRs = new CabsListO001Tranrs(ICONTACT_OK, "交易成功");
        when(iContactClient.callCabsListO001(any())).thenReturn(iContactRs);

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(trimTranrq);

        // Then: 驗證 iContact 傳入的 eformIds 已去除空白
        ArgumentCaptor<CabsListO001Tranrq> iContactCaptor =
                ArgumentCaptor.forClass(CabsListO001Tranrq.class);
        verify(iContactClient).callCabsListO001(iContactCaptor.capture());

        List<String> passedIds = iContactCaptor.getValue().getEformIds();
        assertTrue(passedIds.contains(EFORM_A), "trim 後應包含 EFORM_A");
        assertTrue(passedIds.contains(EFORM_B), "trim 後應包含 EFORM_B");
        passedIds.forEach(id -> assertFalse(id.startsWith(" "), "eformId 不應有前導空白"));
    }

    @Test
    @DisplayName("成功放行 - 驗證 ReleaseActionLog 各欄位正確寫入")
    void shouldSaveLogWithCorrectFields() throws IOException, RequestValidException {
        // Given: 模擬前次狀態為 Y
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "Y")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        CabsListO001Tranrs iContactRs = new CabsListO001Tranrs(ICONTACT_OK, "交易成功");
        when(iContactClient.callCabsListO001(any())).thenReturn(iContactRs);

        LocalDateTime beforeCall = LocalDateTime.now();

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(singleTranrq);

        LocalDateTime afterCall = LocalDateTime.now();

        // Then: 驗證第一次 saveAll 儲存的 log 各欄位
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        ReleaseActionLogEntity saved = captor.getAllValues().get(0).get(0);
        assertEquals(EFORM_A,       saved.getEformId(),       "eformId 應正確");
        assertEquals(ACTION_PASS,   saved.getActionType(),    "actionType 應正確");
        assertEquals(REQ_SOURCE,    saved.getRequestSource(), "requestSource 應正確");
        assertEquals("Y",           saved.getBeforeStatus(),  "beforeStatus 應取自前次狀態查詢結果");
        assertEquals(AFTER_Y,       saved.getAfterStatus(),   "afterStatus 應為 Y");
        assertEquals(EMP_NAME,      saved.getActionBy(),      "actionBy 應為登入使用者");
        assertNotNull(saved.getActionAt(), "actionAt 不應為 null");
        assertTrue(saved.getActionAt().isAfter(beforeCall.minusSeconds(1)), "actionAt 應在執行後");
        assertTrue(saved.getActionAt().isBefore(afterCall.plusSeconds(1)), "actionAt 應在執行前");
        assertNull(saved.getIContactRefStatus(),  "初始 iContactRefStatus 應為 null");
        assertNull(saved.getIContactRefMessage(), "初始 iContactRefMessage 應為 null");

        // Then: 驗證 approvedAt 正確解析為 2026-03-15 00:00:00
        assertEquals(
                LocalDateTime.parse("2026/03/15 00:00:00",
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")),
                saved.getApprovedAt(), "approvedAt 應正確解析");
    }

    @Test
    @DisplayName("成功放行 - 前次狀態 null 時 beforeStatus 預設為 N")
    void shouldDefaultBeforeStatusToNWhenNull() throws IOException, RequestValidException {
        // Given: 前次狀態查詢回傳 status 為 null
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, null)));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        when(iContactClient.callCabsListO001(any()))
                .thenReturn(new CabsListO001Tranrs(ICONTACT_OK, "交易成功"));

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證 beforeStatus 被設為預設值 N
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        ReleaseActionLogEntity saved = captor.getAllValues().get(0).get(0);
        assertEquals("N", saved.getBeforeStatus(), "status 為 null 時 beforeStatus 應預設為 N");
    }

    @Test
    @DisplayName("成功放行 - eformId 不在前次狀態查詢結果時 beforeStatus 預設為 N")
    void shouldDefaultBeforeStatusToNWhenEformIdNotInStatusMap()
            throws IOException, RequestValidException {
        // Given: 前次狀態查詢結果不包含 EFORM_A
        mockSqlQuery(List.of(buildStatusEntity(EFORM_B, "Y")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        when(iContactClient.callCabsListO001(any()))
                .thenReturn(new CabsListO001Tranrs(ICONTACT_OK, "交易成功"));

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證查無前次狀態時 beforeStatus 使用 getOrDefault 的預設值 N
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        ReleaseActionLogEntity saved = captor.getAllValues().get(0).get(0);
        assertEquals("N", saved.getBeforeStatus(),
                "eformId 不在 statusMap 時 beforeStatus 應預設為 N");
    }

    @Test
    @DisplayName("成功放行 - iContact 回傳非 0000 時 success 為 N")
    void shouldReturnFailureWhenIContactReturnsNonZero()
            throws IOException, RequestValidException {
        // Given: iContact 回傳業務失敗碼
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        CabsListO001Tranrs failRs = new CabsListO001Tranrs(ICONTACT_ERR, "系統錯誤");
        when(iContactClient.callCabsListO001(any())).thenReturn(failRs);

        // When: 執行放行處理
        RLSC001Tranrs result = rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證回傳 success 為 N，並帶有 iContact 錯誤訊息
        assertEquals(AFTER_N, result.getSuccess(),    "iContact 非 0000 時 success 應為 N");
        assertEquals(ICONTACT_ERR, result.getReturnCode(), "returnCode 應為 9999");
        assertEquals("系統錯誤",   result.getReturnMsg(),  "returnMsg 應為 iContact 的 returnDesc");

        // Then: 驗證 iContactRefStatus 與 iContactRefMessage 被正確回寫
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());
        ReleaseActionLogEntity updatedEntity = captor.getAllValues().get(1).get(0);
        assertEquals(ICONTACT_ERR, updatedEntity.getIContactRefStatus(),  "iContactRefStatus 應為 9999");
        assertEquals("系統錯誤",   updatedEntity.getIContactRefMessage(), "iContactRefMessage 應為錯誤說明");
    }

    @Test
    @DisplayName("iContact 呼叫拋出例外 - 回傳 E999 且記錄錯誤原因")
    void shouldReturnE999WhenIContactThrowsException()
            throws IOException, RequestValidException {
        // Given: iContact 呼叫拋出 RuntimeException
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        String errorMsg = "Connection timeout";
        when(iContactClient.callCabsListO001(any()))
                .thenThrow(new RuntimeException(errorMsg));

        // When: 執行放行處理
        RLSC001Tranrs result = rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證回傳 E999 並包含錯誤訊息
        assertNotNull(result, "回傳結果不應為 null");
        assertEquals(AFTER_N,   result.getSuccess(),    "iContact 例外時 success 應為 N");
        assertEquals("E999",    result.getReturnCode(), "iContact 例外時 returnCode 應為 E999");
        assertEquals(errorMsg,  result.getReturnMsg(),  "returnMsg 應為例外訊息");

        // Then: 驗證例外路徑中 saveAll 被呼叫兩次（第一次寫 log，第二次寫 E999 錯誤）
        verify(releaseActionLogRepo, times(2)).saveAll(anyList());

        // Then: 驗證 iContactRefStatus 與 iContactRefMessage 被正確回寫
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());
        ReleaseActionLogEntity errorEntity = captor.getAllValues().get(1).get(0);
        assertEquals("E999",   errorEntity.getIContactRefStatus(),  "iContactRefStatus 應為 E999");
        assertEquals(errorMsg, errorEntity.getIContactRefMessage(), "iContactRefMessage 應為例外訊息");
    }

    @Test
    @DisplayName("成功放行 - 驗證 iContact 請求的 reviewDate 格式（去除斜線）")
    void shouldFormatReviewDateCorrectlyForIContact()
            throws IOException, RequestValidException {
        // Given: approvedAt 為 2026/03/15，預期 iContact 收到 20260315
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        when(iContactClient.callCabsListO001(any()))
                .thenReturn(new CabsListO001Tranrs(ICONTACT_OK, "交易成功"));

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證 iContact 收到的 reviewDate 為去除斜線後的格式
        ArgumentCaptor<CabsListO001Tranrq> iContactCaptor =
                ArgumentCaptor.forClass(CabsListO001Tranrq.class);
        verify(iContactClient).callCabsListO001(iContactCaptor.capture());

        assertEquals("20260315", iContactCaptor.getValue().getReviewDate(),
                "reviewDate 應去除斜線，格式為 yyyyMMdd");
    }

    @Test
    @DisplayName("成功放行 - iContact 成功後回寫 refStatus 與 refMessage")
    void shouldUpdateIContactRefFieldsAfterSuccess()
            throws IOException, RequestValidException {
        // Given: iContact 回傳成功
        mockSqlQuery(List.of(buildStatusEntity(EFORM_A, "N")));

        RLSC001Tranrq singleTranrq = new RLSC001Tranrq();
        singleTranrq.setEformIds(List.of(EFORM_A));
        singleTranrq.setActionType(ACTION_PASS);
        singleTranrq.setRequestSource(REQ_SOURCE);
        singleTranrq.setApprovedAt(APPROVED_AT);

        mockSaveAll();
        when(iContactClient.callCabsListO001(any()))
                .thenReturn(new CabsListO001Tranrs(ICONTACT_OK, "交易成功"));

        // When: 執行放行處理
        rlsc001SvcImpl.processRelease(singleTranrq);

        // Then: 驗證第二次 saveAll 時 iContactRefStatus 與 iContactRefMessage 已回填
        ArgumentCaptor<List<ReleaseActionLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(releaseActionLogRepo, times(2)).saveAll(captor.capture());

        ReleaseActionLogEntity updatedEntity = captor.getAllValues().get(1).get(0);
        assertEquals(ICONTACT_OK, updatedEntity.getIContactRefStatus(),  "iContactRefStatus 應為 0000");
        assertEquals("交易成功",  updatedEntity.getIContactRefMessage(), "iContactRefMessage 應為 交易成功");
    }
}