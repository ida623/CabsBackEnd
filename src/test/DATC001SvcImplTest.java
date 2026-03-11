
package cab.bff.ivt.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cab.bff.ivt.auth.UserObject;
import cab.bff.ivt.dto.DATC001Tranrq;
import cab.bff.ivt.dto.base.EmptyTranrs;
import cab.bff.ivt.entity.DataSettingWindowEntity;
import cab.bff.ivt.exception.DataNotFoundException;
import cab.bff.ivt.exception.RestException;
import cab.bff.ivt.repo.DataSettingWindowRepo;
import cab.bff.ivt.returncode.ReturnCodeAndDescEnum;

/**
 * DATC001SvcImpl 單元測試
 * 
 * @author 00550354
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DATC001SvcImpl 單元測試")
class DATC001SvcImplTest {

    // 測試常數
    private static final String VALID_START_DATE = "2026/03/15";
    private static final String VALID_END_DATE = "2026/03/20";
    private static final String VALID_CUTOFF_DATE = "2026/03/10 17:30";
    private static final String INVALID_START_DATE = "2026/03/20";
    private static final String INVALID_END_DATE = "2026/03/15";
    private static final String USER_NAME_CHEN = "陳阿仁";
    private static final String USER_NAME_WU = "吳小昌";
    private static final String ERROR_MSG_DATA_NOT_FOUND = "查無目前最新日期設定";
    private static final String ERROR_MSG_INVALID_DATE_RANGE = "預計執行變更區間－起日不可大於預計執行變更區間－迄日";
    private static final long EXISTING_ID = 100L;
    private static final long EXPECTED_NEW_ID = 101L;
    private static final long MAX_ID = 9999L;
    private static final long EXPECTED_MAX_NEW_ID = 10000L;
    
    @Mock
    private DataSettingWindowRepo dataSettingWindowRepo;

    @Mock
    private UserObject userObject;

    @InjectMocks
    private DATC001SvcImpl datc001SvcImpl;

    private DATC001Tranrq validTranrq;
    private DataSettingWindowEntity existingEntity;

    @BeforeEach
    void setUp() {
        // 準備測試資料 - 正常的上行資料
        validTranrq = new DATC001Tranrq();
        validTranrq.setWindowStartDate(VALID_START_DATE);
        validTranrq.setWindowEndDate(VALID_END_DATE);
        validTranrq.setCutoffDate(VALID_CUTOFF_DATE);

        // 準備測試資料 - 資料庫中既有的最大 ID 資料
        existingEntity = new DataSettingWindowEntity();
        existingEntity.setId(EXISTING_ID);
        existingEntity.setWindowStartDate(LocalDate.of(2026, 3, 1));
        existingEntity.setWindowEndDate(LocalDate.of(2026, 3, 10));
        existingEntity.setCutoffDate(LocalDateTime.of(2026, 2, 28, 17, 30));
        existingEntity.setCreatedBy("測試使用者");
        existingEntity.setCreatedAt(LocalDateTime.now());

        // 設定 UserObject 的預設行為
        when(userObject.getEmpName()).thenReturn(USER_NAME_CHEN);
    }

    @Test
    @DisplayName("成功新增日期設定 - 正常流程")
    void shouldCreateDateSettingsSuccessfully() throws RestException, DataNotFoundException {
        // Given: 模擬資料庫查詢回傳既有資料
        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行新增日期設定
        EmptyTranrs result = datc001SvcImpl.createDateSettings(validTranrq);

        // Then: 驗證回傳結果
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result instanceof EmptyTranrs, "回傳結果應為 EmptyTranrs 類型");

        // Then: 驗證 Repository 方法被呼叫
        verify(dataSettingWindowRepo, times(1)).findTopByOrderByIdDesc();
        verify(dataSettingWindowRepo, times(1)).save(any(DataSettingWindowEntity.class));

        // Then: 驗證儲存的資料內容
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(EXPECTED_NEW_ID, savedEntity.getId(), "ID 應為既有資料的 ID + 1");
        assertEquals(LocalDate.of(2026, 3, 15), savedEntity.getWindowStartDate(), "起日應正確轉換");
        assertEquals(LocalDate.of(2026, 3, 20), savedEntity.getWindowEndDate(), "迄日應正確轉換");
        assertEquals(LocalDateTime.of(2026, 3, 10, 17, 30), savedEntity.getCutoffDate(), "截止時間應正確轉換");
        assertEquals(USER_NAME_CHEN, savedEntity.getCreatedBy(), "建立者應為當前登入使用者");
        assertNotNull(savedEntity.getCreatedAt(), "建立時間不應為 null");
    }

    @Test
    @DisplayName("失敗 - 起日大於迄日時拋出 RestException")
    void shouldThrowRestExceptionWhenStartDateAfterEndDate() {
        // Given: 準備起日大於迄日的資料
        DATC001Tranrq invalidTranrq = new DATC001Tranrq();
        invalidTranrq.setWindowStartDate(INVALID_START_DATE);
        invalidTranrq.setWindowEndDate(INVALID_END_DATE);
        invalidTranrq.setCutoffDate(VALID_CUTOFF_DATE);

        // When & Then: 執行並驗證拋出例外
        RestException exception = assertThrows(RestException.class, 
            () -> datc001SvcImpl.createDateSettings(invalidTranrq),
            "起日大於迄日應拋出 RestException");

        // Then: 驗證例外訊息
        assertEquals(ReturnCodeAndDescEnum.SE903.getCode(), exception.getRtnCode(), 
            "錯誤碼應為 SE903");
        assertEquals(ReturnCodeAndDescEnum.SE903.getDesc(), exception.getRtnMsg(), 
            "錯誤訊息應為 SE903 的描述");
        assertEquals(ERROR_MSG_INVALID_DATE_RANGE, exception.getTranrs(), 
            "tranrs 訊息應正確");

        // Then: 驗證不應執行儲存動作
        verify(dataSettingWindowRepo, never()).save(any(DataSettingWindowEntity.class));
    }

    @Test
    @DisplayName("失敗 - 資料庫查無既有資料時拋出 DataNotFoundException")
    void shouldThrowDataNotFoundExceptionWhenNoExistingData() {
        // Given: 模擬資料庫查詢無資料
        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        // When & Then: 執行並驗證拋出例外
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
            () -> datc001SvcImpl.createDateSettings(validTranrq),
            "查無既有資料應拋出 DataNotFoundException");

        // Then: 驗證例外訊息
        assertEquals(ERROR_MSG_DATA_NOT_FOUND, exception.getMessage(), 
            "例外訊息應正確");

        // Then: 驗證查詢被呼叫，但儲存未被呼叫
        verify(dataSettingWindowRepo, times(1)).findTopByOrderByIdDesc();
        verify(dataSettingWindowRepo, never()).save(any(DataSettingWindowEntity.class));
    }

    @Test
    @DisplayName("成功新增 - 起日等於迄日的邊界情況")
    void shouldCreateSuccessfullyWhenStartDateEqualsEndDate() throws RestException, DataNotFoundException {
        // Given: 準備起日等於迄日的資料
        DATC001Tranrq sameDateTranrq = new DATC001Tranrq();
        sameDateTranrq.setWindowStartDate(VALID_START_DATE);
        sameDateTranrq.setWindowEndDate(VALID_START_DATE);
        sameDateTranrq.setCutoffDate(VALID_CUTOFF_DATE);

        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行新增
        EmptyTranrs result = datc001SvcImpl.createDateSettings(sameDateTranrq);

        // Then: 應成功執行
        assertNotNull(result, "回傳結果不應為 null");
        verify(dataSettingWindowRepo, times(1)).save(any(DataSettingWindowEntity.class));

        // Then: 驗證儲存的資料
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(LocalDate.of(2026, 3, 15), savedEntity.getWindowStartDate(), "起日應正確");
        assertEquals(LocalDate.of(2026, 3, 15), savedEntity.getWindowEndDate(), "迄日應正確");
    }

    @Test
    @DisplayName("成功新增 - 驗證 ID 遞增邏輯")
    void shouldIncrementIdCorrectly() throws RestException, DataNotFoundException {
        // Given: 準備不同的最大 ID
        DataSettingWindowEntity entityWithMaxId = new DataSettingWindowEntity();
        entityWithMaxId.setId(MAX_ID);
        entityWithMaxId.setWindowStartDate(LocalDate.of(2026, 3, 1));
        entityWithMaxId.setWindowEndDate(LocalDate.of(2026, 3, 10));
        entityWithMaxId.setCutoffDate(LocalDateTime.now());
        entityWithMaxId.setCreatedBy("測試");
        entityWithMaxId.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(entityWithMaxId));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行新增
        datc001SvcImpl.createDateSettings(validTranrq);

        // Then: 驗證新 ID 為 9999 + 1 = 10000
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(EXPECTED_MAX_NEW_ID, savedEntity.getId(), "ID 應為 9999 + 1");
    }

    @Test
    @DisplayName("成功新增 - 驗證建立時間自動設定")
    void shouldSetCreatedAtAutomatically() throws RestException, DataNotFoundException {
        // Given
        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeCreate = LocalDateTime.now();

        // When
        datc001SvcImpl.createDateSettings(validTranrq);

        LocalDateTime afterCreate = LocalDateTime.now();

        // Then: 驗證建立時間在執行前後之間
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertNotNull(savedEntity.getCreatedAt(), "建立時間應自動設定");
        assertTrue(savedEntity.getCreatedAt().isAfter(beforeCreate.minusSeconds(1)), 
            "建立時間應在執行後");
        assertTrue(savedEntity.getCreatedAt().isBefore(afterCreate.plusSeconds(1)), 
            "建立時間應在執行前");
    }

    @Test
    @DisplayName("成功新增 - 驗證 UserObject 的 empName 被正確使用")
    void shouldUseCreatedByFromUserObject() throws RestException, DataNotFoundException {
        // Given: 設定不同的使用者名稱
        when(userObject.getEmpName()).thenReturn(USER_NAME_WU);
        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        datc001SvcImpl.createDateSettings(validTranrq);

        // Then: 驗證建立者為 UserObject 的 empName
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(USER_NAME_WU, savedEntity.getCreatedBy(), "建立者應為當前登入使用者");
        
        // 驗證 UserObject 被呼叫
        verify(userObject, times(1)).getEmpName();
    }

    @Test
    @DisplayName("失敗 - 起日只比迄日晚一天也應拋出例外")
    void shouldThrowExceptionWhenStartDateOneDayAfterEndDate() {
        // Given: 起日比迄日晚一天
        DATC001Tranrq invalidTranrq = new DATC001Tranrq();
        invalidTranrq.setWindowStartDate("2026/03/16");
        invalidTranrq.setWindowEndDate(VALID_START_DATE);
        invalidTranrq.setCutoffDate(VALID_CUTOFF_DATE);

        // When & Then
        RestException exception = assertThrows(RestException.class,
            () -> datc001SvcImpl.createDateSettings(invalidTranrq),
            "起日晚於迄日應拋出 RestException");

        assertEquals(ReturnCodeAndDescEnum.SE903.getCode(), exception.getRtnCode());
        verify(dataSettingWindowRepo, never()).save(any(DataSettingWindowEntity.class));
    }

    @Test
    @DisplayName("成功新增 - 跨月份的日期區間")
    void shouldHandleCrossMonthDateRange() throws RestException, DataNotFoundException {
        // Given: 跨月份的日期區間
        DATC001Tranrq crossMonthTranrq = new DATC001Tranrq();
        crossMonthTranrq.setWindowStartDate("2026/03/25");
        crossMonthTranrq.setWindowEndDate("2026/04/05");
        crossMonthTranrq.setCutoffDate("2026/03/20 17:30");

        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        EmptyTranrs result = datc001SvcImpl.createDateSettings(crossMonthTranrq);

        // Then
        assertNotNull(result);
        
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(LocalDate.of(2026, 3, 25), savedEntity.getWindowStartDate());
        assertEquals(LocalDate.of(2026, 4, 5), savedEntity.getWindowEndDate());
    }

    @Test
    @DisplayName("成功新增 - 截止時間在不同時段")
    void shouldHandleDifferentCutoffTime() throws RestException, DataNotFoundException {
        // Given: 測試不同的截止時間
        DATC001Tranrq morningTimeTranrq = new DATC001Tranrq();
        morningTimeTranrq.setWindowStartDate(VALID_START_DATE);
        morningTimeTranrq.setWindowEndDate(VALID_END_DATE);
        morningTimeTranrq.setCutoffDate("2026/03/10 09:00");

        when(dataSettingWindowRepo.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingEntity));
        when(dataSettingWindowRepo.save(any(DataSettingWindowEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        datc001SvcImpl.createDateSettings(morningTimeTranrq);

        // Then
        ArgumentCaptor<DataSettingWindowEntity> entityCaptor = ArgumentCaptor.forClass(DataSettingWindowEntity.class);
        verify(dataSettingWindowRepo).save(entityCaptor.capture());
        
        DataSettingWindowEntity savedEntity = entityCaptor.getValue();
        assertEquals(LocalDateTime.of(2026, 3, 10, 9, 0), savedEntity.getCutoffDate(), 
            "截止時間應正確轉換為早上 9:00");
    }
}
