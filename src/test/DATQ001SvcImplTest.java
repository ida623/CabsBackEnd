package cab.bff.ivt.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cab.bff.ivt.dto.DATQ001Tranrs;
import cab.bff.ivt.dto.base.EmptyTranrq;
import cab.bff.ivt.entity.DataSettingWindowEntity;
import cab.bff.ivt.exception.DataNotFoundException;
import cab.bff.ivt.repo.DataSettingWindowRepo;

/**
 * DATQ001SvcImpl 單元測試
 * 
 * @author 00550354
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DATQ001SvcImpl 單元測試")
class DATQ001SvcImplTest {

    // 測試常數
    private static final LocalDate TEST_START_DATE = LocalDate.of(2026, 3, 15);
    private static final LocalDate TEST_END_DATE = LocalDate.of(2026, 3, 20);
    private static final LocalDateTime TEST_CUTOFF_DATE = LocalDateTime.of(2026, 3, 10, 17, 30);
    private static final String EXPECTED_START_DATE = "2026/03/15";
    private static final String EXPECTED_END_DATE = "2026/03/20";
    private static final String EXPECTED_CUTOFF_DATE = "2026/03/10 17:30";
    private static final String ERROR_MSG_DATA_NOT_FOUND = "查無目前最新日期設定";
    private static final long TEST_ID = 100L;
    private static final String TEST_CREATED_BY = "測試使用者";

    @Mock
    private DataSettingWindowRepo dataSettingWindowRepo;

    @InjectMocks
    private DATQ001SvcImpl datq001SvcImpl;

    private EmptyTranrq emptyTranrq;
    private DataSettingWindowEntity testEntity;

    @BeforeEach
    void setUp() {
        // 準備測試資料 - 空的上行資料
        emptyTranrq = new EmptyTranrq();

        // 準備測試資料 - 資料庫中的日期設定資料
        testEntity = new DataSettingWindowEntity();
        testEntity.setId(TEST_ID);
        testEntity.setWindowStartDate(TEST_START_DATE);
        testEntity.setWindowEndDate(TEST_END_DATE);
        testEntity.setCutoffDate(TEST_CUTOFF_DATE);
        testEntity.setCreatedBy(TEST_CREATED_BY);
        testEntity.setCreatedAt(LocalDateTime.of(2026, 3, 1, 10, 0));
    }

    @Test
    @DisplayName("成功查詢日期設定 - 正常流程")
    void shouldQueryDateSettingsSuccessfully() throws DataNotFoundException {
        // Given: 模擬資料庫查詢回傳資料
        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(testEntity);

        // When: 執行查詢日期設定
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證回傳結果
        assertNotNull(result, "回傳結果不應為 null");
        assertEquals(EXPECTED_START_DATE, result.getWindowStartDate(), "起日格式應為 YYYY/MM/DD");
        assertEquals(EXPECTED_END_DATE, result.getWindowEndDate(), "迄日格式應為 YYYY/MM/DD");
        assertEquals(EXPECTED_CUTOFF_DATE, result.getCutoffDate(), "截止時間格式應為 YYYY/MM/DD HH:mm");

        // Then: 驗證 Repository 方法被呼叫
        verify(dataSettingWindowRepo, times(1)).findTopByOrderByCreatedAtDescIdDesc();
    }

    @Test
    @DisplayName("失敗 - 資料庫查無資料時拋出 DataNotFoundException")
    void shouldThrowDataNotFoundExceptionWhenNoData() {
        // Given: 模擬資料庫查詢無資料
        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(null);

        // When & Then: 執行並驗證拋出例外
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> datq001SvcImpl.queryDateSettings(emptyTranrq),
                "查無資料應拋出 DataNotFoundException");

        // Then: 驗證例外訊息
        assertEquals(ERROR_MSG_DATA_NOT_FOUND, exception.getMessage(), "例外訊息應正確");

        // Then: 驗證查詢被呼叫
        verify(dataSettingWindowRepo, times(1)).findTopByOrderByCreatedAtDescIdDesc();
    }

    @Test
    @DisplayName("成功查詢 - 日期格式轉換正確性測試（單日格式）")
    void shouldFormatSingleDigitDateCorrectly() throws DataNotFoundException {
        // Given: 準備單位數日期的資料
        DataSettingWindowEntity singleDigitEntity = new DataSettingWindowEntity();
        singleDigitEntity.setId(1L);
        singleDigitEntity.setWindowStartDate(LocalDate.of(2026, 1, 5));
        singleDigitEntity.setWindowEndDate(LocalDate.of(2026, 2, 9));
        singleDigitEntity.setCutoffDate(LocalDateTime.of(2026, 1, 1, 9, 5));
        singleDigitEntity.setCreatedBy(TEST_CREATED_BY);
        singleDigitEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(singleDigitEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證日期格式轉換正確（應補零）
        assertEquals("2026/01/05", result.getWindowStartDate(), "單位數月日應補零");
        assertEquals("2026/02/09", result.getWindowEndDate(), "單位數月日應補零");
        assertEquals("2026/01/01 09:05", result.getCutoffDate(), "單位數時分應補零");
    }

    @Test
    @DisplayName("成功查詢 - 跨年度的日期設定")
    void shouldHandleCrossYearDateRange() throws DataNotFoundException {
        // Given: 準備跨年度的日期資料
        DataSettingWindowEntity crossYearEntity = new DataSettingWindowEntity();
        crossYearEntity.setId(2L);
        crossYearEntity.setWindowStartDate(LocalDate.of(2025, 12, 25));
        crossYearEntity.setWindowEndDate(LocalDate.of(2026, 1, 5));
        crossYearEntity.setCutoffDate(LocalDateTime.of(2025, 12, 20, 23, 59));
        crossYearEntity.setCreatedBy(TEST_CREATED_BY);
        crossYearEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(crossYearEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證跨年度日期正確
        assertEquals("2025/12/25", result.getWindowStartDate(), "跨年起日應正確");
        assertEquals("2026/01/05", result.getWindowEndDate(), "跨年迄日應正確");
        assertEquals("2025/12/20 23:59", result.getCutoffDate(), "跨年截止時間應正確");
    }

    @Test
    @DisplayName("成功查詢 - 午夜時間的正確轉換")
    void shouldHandleMidnightTimeCorrectly() throws DataNotFoundException {
        // Given: 準備午夜時間的資料
        DataSettingWindowEntity midnightEntity = new DataSettingWindowEntity();
        midnightEntity.setId(3L);
        midnightEntity.setWindowStartDate(TEST_START_DATE);
        midnightEntity.setWindowEndDate(TEST_END_DATE);
        midnightEntity.setCutoffDate(LocalDateTime.of(2026, 3, 10, 0, 0));
        midnightEntity.setCreatedBy(TEST_CREATED_BY);
        midnightEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(midnightEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證午夜時間格式正確
        assertEquals("2026/03/10 00:00", result.getCutoffDate(), "午夜時間應為 00:00");
    }

    @Test
    @DisplayName("成功查詢 - 一天結束時間的正確轉換")
    void shouldHandleEndOfDayTimeCorrectly() throws DataNotFoundException {
        // Given: 準備一天結束時間的資料
        DataSettingWindowEntity endOfDayEntity = new DataSettingWindowEntity();
        endOfDayEntity.setId(4L);
        endOfDayEntity.setWindowStartDate(TEST_START_DATE);
        endOfDayEntity.setWindowEndDate(TEST_END_DATE);
        endOfDayEntity.setCutoffDate(LocalDateTime.of(2026, 3, 10, 23, 59));
        endOfDayEntity.setCreatedBy(TEST_CREATED_BY);
        endOfDayEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(endOfDayEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證一天結束時間格式正確
        assertEquals("2026/03/10 23:59", result.getCutoffDate(), "一天結束時間應為 23:59");
    }

    @Test
    @DisplayName("成功查詢 - 起日等於迄日的情況")
    void shouldHandleSameDateRange() throws DataNotFoundException {
        // Given: 準備起日等於迄日的資料
        DataSettingWindowEntity sameDateEntity = new DataSettingWindowEntity();
        sameDateEntity.setId(5L);
        sameDateEntity.setWindowStartDate(TEST_START_DATE);
        sameDateEntity.setWindowEndDate(TEST_START_DATE);
        sameDateEntity.setCutoffDate(TEST_CUTOFF_DATE);
        sameDateEntity.setCreatedBy(TEST_CREATED_BY);
        sameDateEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(sameDateEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證起日等於迄日的情況
        assertEquals(EXPECTED_START_DATE, result.getWindowStartDate(), "起日應正確");
        assertEquals(EXPECTED_START_DATE, result.getWindowEndDate(), "迄日應與起日相同");
        assertNotNull(result.getCutoffDate(), "截止時間不應為 null");
    }

    @Test
    @DisplayName("成功查詢 - 驗證 Tranrq 參數不影響查詢結果")
    void shouldIgnoreTranrqParameter() throws DataNotFoundException {
        // Given: 準備兩個不同的 EmptyTranrq 實例
        EmptyTranrq tranrq1 = new EmptyTranrq();
        EmptyTranrq tranrq2 = new EmptyTranrq();

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(testEntity);

        // When: 執行兩次查詢
        DATQ001Tranrs result1 = datq001SvcImpl.queryDateSettings(tranrq1);
        DATQ001Tranrs result2 = datq001SvcImpl.queryDateSettings(tranrq2);

        // Then: 驗證結果應相同（因為只依賴資料庫查詢結果）
        assertEquals(result1.getWindowStartDate(), result2.getWindowStartDate(), "起日應相同");
        assertEquals(result1.getWindowEndDate(), result2.getWindowEndDate(), "迄日應相同");
        assertEquals(result1.getCutoffDate(), result2.getCutoffDate(), "截止時間應相同");

        // Then: 驗證查詢被呼叫兩次
        verify(dataSettingWindowRepo, times(2)).findTopByOrderByCreatedAtDescIdDesc();
    }

    @Test
    @DisplayName("成功查詢 - 閏年 2 月 29 日的正確處理")
    void shouldHandleLeapYearDateCorrectly() throws DataNotFoundException {
        // Given: 準備閏年 2 月 29 日的資料
        DataSettingWindowEntity leapYearEntity = new DataSettingWindowEntity();
        leapYearEntity.setId(6L);
        leapYearEntity.setWindowStartDate(LocalDate.of(2024, 2, 28));
        leapYearEntity.setWindowEndDate(LocalDate.of(2024, 2, 29));
        leapYearEntity.setCutoffDate(LocalDateTime.of(2024, 2, 27, 12, 0));
        leapYearEntity.setCreatedBy(TEST_CREATED_BY);
        leapYearEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(leapYearEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證閏年日期正確
        assertEquals("2024/02/28", result.getWindowStartDate(), "閏年 2 月 28 日應正確");
        assertEquals("2024/02/29", result.getWindowEndDate(), "閏年 2 月 29 日應正確");
    }

    @Test
    @DisplayName("成功查詢 - 月末日期的正確處理")
    void shouldHandleEndOfMonthDateCorrectly() throws DataNotFoundException {
        // Given: 準備月末日期的資料（31 天的月份）
        DataSettingWindowEntity endOfMonthEntity = new DataSettingWindowEntity();
        endOfMonthEntity.setId(7L);
        endOfMonthEntity.setWindowStartDate(LocalDate.of(2026, 1, 30));
        endOfMonthEntity.setWindowEndDate(LocalDate.of(2026, 1, 31));
        endOfMonthEntity.setCutoffDate(LocalDateTime.of(2026, 1, 29, 18, 0));
        endOfMonthEntity.setCreatedBy(TEST_CREATED_BY);
        endOfMonthEntity.setCreatedAt(LocalDateTime.now());

        when(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc()).thenReturn(endOfMonthEntity);

        // When: 執行查詢
        DATQ001Tranrs result = datq001SvcImpl.queryDateSettings(emptyTranrq);

        // Then: 驗證月末日期正確
        assertEquals("2026/01/30", result.getWindowStartDate(), "月末前一天應正確");
        assertEquals("2026/01/31", result.getWindowEndDate(), "月末最後一天應正確");
    }
}
