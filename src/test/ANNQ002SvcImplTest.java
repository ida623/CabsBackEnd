package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.ANNQ002Tranrq;
import com.cathaybk.demo.dto.ANNQ002Tranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.AnnouncementRepo;

/**
 * ANNQ002SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ANNQ002SvcImpl 單元測試")
class ANNQ002SvcImplTest {

    private static final Long VALID_ID = 1L;
    private static final String VALID_CONTENT = "測試公告內容";
    private static final String CREATOR = "陳阿仁";
    private static final String UPDATER = "吳小昌";
    private static final String ERROR_MSG_NOT_FOUND = "查無該筆公告事項";

    @Mock
    private AnnouncementRepo announcementRepo;

    @InjectMocks
    private ANNQ002SvcImpl annq002SvcImpl;

    private ANNQ002Tranrq tranrq;
    private AnnouncementEntity existingEntity;

    @BeforeEach
    void setUp() {
        tranrq = new ANNQ002Tranrq();
        tranrq.setId(VALID_ID);

        existingEntity = new AnnouncementEntity();
        existingEntity.setId(VALID_ID);
        existingEntity.setContent(VALID_CONTENT);
        existingEntity.setCreatedBy(CREATOR);
        existingEntity.setCreatedAt(LocalDateTime.of(2026, 3, 1, 9, 0, 0));
        existingEntity.setUpdatedBy(UPDATER);
        existingEntity.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 17, 30, 0));
    }

    @Test
    @DisplayName("成功查詢公告 - 回傳正確資料")
    void shouldReturnAnnouncementSuccessfully() throws DataNotFoundException {
        // Given: 模擬資料庫查詢到既有公告
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));

        // When: 執行查詢公告
        ANNQ002Tranrs result = annq002SvcImpl.queryAnnouncement(tranrq);

        // Then: 驗證回傳各欄位與日期格式正確
        assertNotNull(result, "回傳結果不應為 null");
        assertEquals(VALID_ID, result.getId(), "ID 應正確");
        assertEquals(VALID_CONTENT, result.getContent(), "內容應正確");
        assertEquals(CREATOR, result.getCreatedBy(), "建立者應正確");
        assertEquals(UPDATER, result.getUpdatedBy(), "修改者應正確");
        assertEquals("2026/03/01 09:00:00", result.getCreatedAt(), "建立時間格式應正確");
        assertEquals("2026/03/15 17:30:00", result.getUpdatedAt(), "修改時間格式應正確");

        verify(announcementRepo, times(1)).findById(VALID_ID);
    }

    @Test
    @DisplayName("失敗 - 查無公告時拋出 DataNotFoundException")
    void shouldThrowDataNotFoundExceptionWhenNotFound() {
        // Given: 模擬資料庫查無該筆公告
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.empty());

        // When & Then: 執行並驗證拋出 DataNotFoundException
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> annq002SvcImpl.queryAnnouncement(tranrq),
                "查無資料應拋出 DataNotFoundException");

        assertEquals(ERROR_MSG_NOT_FOUND, exception.getMessage(), "例外訊息應正確");
        verify(announcementRepo, times(1)).findById(VALID_ID);
    }

    @Test
    @DisplayName("成功查詢 - 驗證日期格式 yyyy/MM/dd HH:mm:ss")
    void shouldFormatDateTimeCorrectly() throws DataNotFoundException {
        // Given: 設定跨年底與年初的邊界時間點
        existingEntity.setCreatedAt(LocalDateTime.of(2026, 12, 31, 23, 59, 59));
        existingEntity.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 0, 0, 1));
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));

        // When: 執行查詢公告
        ANNQ002Tranrs result = annq002SvcImpl.queryAnnouncement(tranrq);

        // Then: 驗證時間格式符合 yyyy/MM/dd HH:mm:ss
        assertEquals("2026/12/31 23:59:59", result.getCreatedAt(), "建立時間格式應為 yyyy/MM/dd HH:mm:ss");
        assertEquals("2026/01/01 00:00:01", result.getUpdatedAt(), "修改時間格式應為 yyyy/MM/dd HH:mm:ss");
    }

    @Test
    @DisplayName("成功查詢 - 以不同 ID 查詢")
    void shouldQueryWithDifferentId() throws DataNotFoundException {
        // Given: 以不同 ID 查詢，模擬資料庫回傳對應公告
        Long anotherId = 999L;
        tranrq.setId(anotherId);
        existingEntity.setId(anotherId);
        when(announcementRepo.findById(anotherId)).thenReturn(Optional.of(existingEntity));

        // When: 執行查詢公告
        ANNQ002Tranrs result = annq002SvcImpl.queryAnnouncement(tranrq);

        // Then: 驗證回傳 ID 與查詢 ID 相符，且未查詢原始 ID
        assertNotNull(result);
        assertEquals(anotherId, result.getId(), "回傳 ID 應與查詢 ID 相符");
        verify(announcementRepo, times(1)).findById(anotherId);
        verify(announcementRepo, never()).findById(VALID_ID);
    }
}