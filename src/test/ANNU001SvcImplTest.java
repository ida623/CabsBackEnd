package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import com.cathaybk.demo.dto.ANNU001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.user.UserObject;

/**
 * ANNU001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ANNU001SvcImpl 單元測試")
class ANNU001SvcImplTest {

    private static final Long VALID_ID = 1L;
    private static final String VALID_EMP_NAME = "陳阿仁";
    private static final String ORIGINAL_CONTENT = "原始公告內容";
    private static final String UPDATED_CONTENT = "更新後公告內容";
    private static final String ERROR_CODE_E300 = "E300";
    private static final String ERROR_MSG_NO_USER = "未取得使用者資訊";
    private static final String ERROR_MSG_NOT_FOUND = "查無該筆公告事項";

    @Mock
    private AnnouncementRepo announcementRepo;

    @Mock
    private UserObject userObject;

    @InjectMocks
    private ANNU001SvcImpl annu001SvcImpl;

    private ANNU001Tranrq validTranrq;
    private AnnouncementEntity existingEntity;

    @BeforeEach
    void setUp() {
        validTranrq = new ANNU001Tranrq();
        validTranrq.setId(VALID_ID);
        validTranrq.setContent(UPDATED_CONTENT);

        existingEntity = new AnnouncementEntity();
        existingEntity.setId(VALID_ID);
        existingEntity.setContent(ORIGINAL_CONTENT);
        existingEntity.setCreatedBy("原始建立者");
        existingEntity.setCreatedAt(LocalDateTime.of(2026, 3, 1, 9, 0));
        existingEntity.setUpdatedBy("原始修改者");
        existingEntity.setUpdatedAt(LocalDateTime.of(2026, 3, 1, 9, 0));

        when(userObject.getEmpName()).thenReturn(VALID_EMP_NAME);
    }

    @Test
    @DisplayName("成功修改公告 - 正常流程")
    void shouldUpdateAnnouncementSuccessfully() throws RestException, DataNotFoundException {
        // Given: 模擬資料庫查詢到既有公告，以及 Repository 儲存行為
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));
        when(announcementRepo.save(any(AnnouncementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When: 執行修改公告
        EmptyTranrs result = annu001SvcImpl.updateAnnouncement(validTranrq);

        LocalDateTime afterUpdate = LocalDateTime.now();

        // Then: 驗證回傳型別正確
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result instanceof EmptyTranrs, "回傳結果應為 EmptyTranrs 類型");

        // Then: 驗證儲存的公告資料內容與修改時間
        ArgumentCaptor<AnnouncementEntity> captor = ArgumentCaptor.forClass(AnnouncementEntity.class);
        verify(announcementRepo).save(captor.capture());

        AnnouncementEntity saved = captor.getValue();
        assertEquals(UPDATED_CONTENT, saved.getContent(), "內容應更新為新內容");
        assertEquals(VALID_EMP_NAME, saved.getUpdatedBy(), "修改者應為當前登入使用者");
        assertNotNull(saved.getUpdatedAt(), "修改時間不應為 null");
        assertTrue(saved.getUpdatedAt().isAfter(beforeUpdate.minusSeconds(1)), "修改時間應在執行後");
        assertTrue(saved.getUpdatedAt().isBefore(afterUpdate.plusSeconds(1)), "修改時間應在執行前");

        verify(userObject, times(1)).getEmpName();
        verify(announcementRepo, times(1)).findById(VALID_ID);
        verify(announcementRepo, times(1)).save(any(AnnouncementEntity.class));
    }

    @Test
    @DisplayName("成功修改 - 驗證 createdBy 及 createdAt 不被更動")
    void shouldNotModifyCreatedByAndCreatedAt() throws RestException, DataNotFoundException {
        // Given: 記錄既有公告的原始建立者與建立時間
        String originalCreatedBy = existingEntity.getCreatedBy();
        LocalDateTime originalCreatedAt = existingEntity.getCreatedAt();

        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));
        when(announcementRepo.save(any(AnnouncementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行修改公告
        annu001SvcImpl.updateAnnouncement(validTranrq);

        // Then: 驗證 createdBy 與 createdAt 未被異動
        ArgumentCaptor<AnnouncementEntity> captor = ArgumentCaptor.forClass(AnnouncementEntity.class);
        verify(announcementRepo).save(captor.capture());

        AnnouncementEntity saved = captor.getValue();
        assertEquals(originalCreatedBy, saved.getCreatedBy(), "建立者不應被修改");
        assertEquals(originalCreatedAt, saved.getCreatedAt(), "建立時間不應被修改");
    }

    @Test
    @DisplayName("失敗 - empName 為 null 時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsNull() {
        // Given: 模擬 UserObject 回傳 null 的使用者名稱
        when(userObject.getEmpName()).thenReturn(null);

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annu001SvcImpl.updateAnnouncement(validTranrq),
                "empName 為 null 應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode(), "錯誤碼應為 E300");
        assertEquals(ERROR_MSG_NO_USER, exception.getRtnMsg(), "錯誤訊息應正確");
        // Then: 驗證未執行查詢與儲存
        verify(announcementRepo, never()).findById(any());
        verify(announcementRepo, never()).save(any());
    }

    @Test
    @DisplayName("失敗 - empName 為空白時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsBlank() {
        // Given: 模擬 UserObject 回傳全空白的使用者名稱
        when(userObject.getEmpName()).thenReturn("  ");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annu001SvcImpl.updateAnnouncement(validTranrq));

        assertEquals(ERROR_CODE_E300, exception.getRtnCode());
        // Then: 驗證未執行儲存
        verify(announcementRepo, never()).save(any());
    }

    @Test
    @DisplayName("失敗 - empName 為空字串時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsEmpty() {
        // Given: 模擬 UserObject 回傳空字串的使用者名稱
        when(userObject.getEmpName()).thenReturn("");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annu001SvcImpl.updateAnnouncement(validTranrq));

        assertEquals(ERROR_CODE_E300, exception.getRtnCode());
        // Then: 驗證未執行儲存
        verify(announcementRepo, never()).save(any());
    }

    @Test
    @DisplayName("失敗 - 查無公告資料時拋出 DataNotFoundException")
    void shouldThrowDataNotFoundExceptionWhenNotFound() {
        // Given: 使用者驗證通過，但資料庫查無該筆公告
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.empty());

        // When & Then: 執行並驗證拋出 DataNotFoundException
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> annu001SvcImpl.updateAnnouncement(validTranrq),
                "查無資料應拋出 DataNotFoundException");

        assertEquals(ERROR_MSG_NOT_FOUND, exception.getMessage(), "例外訊息應正確");
        // Then: 驗證查詢被呼叫，但未執行儲存
        verify(announcementRepo, times(1)).findById(VALID_ID);
        verify(announcementRepo, never()).save(any());
    }

    @Test
    @DisplayName("成功修改 - 驗證不同使用者名稱被正確寫入修改者")
    void shouldUseCorrectEmpNameAsUpdatedBy() throws RestException, DataNotFoundException {
        // Given: 設定不同的登入使用者名稱
        String anotherEmp = "吳小昌";
        when(userObject.getEmpName()).thenReturn(anotherEmp);
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));
        when(announcementRepo.save(any(AnnouncementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行修改公告
        annu001SvcImpl.updateAnnouncement(validTranrq);

        // Then: 驗證修改者為當前登入使用者
        ArgumentCaptor<AnnouncementEntity> captor = ArgumentCaptor.forClass(AnnouncementEntity.class);
        verify(announcementRepo).save(captor.capture());

        AnnouncementEntity saved = captor.getValue();
        assertEquals(anotherEmp, saved.getUpdatedBy(), "修改者應為 " + anotherEmp);
        verify(userObject, times(1)).getEmpName();
    }
}