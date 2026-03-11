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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.ANND001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.user.UserObject;

/**
 * ANND001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ANND001SvcImpl 單元測試")
class ANND001SvcImplTest {

    private static final Long VALID_ID = 1L;
    private static final String VALID_EMP_NAME = "陳阿仁";
    private static final String ERROR_CODE_E300 = "E300";
    private static final String ERROR_MSG_NO_USER = "未取得使用者資訊";
    private static final String ERROR_MSG_NOT_FOUND = "查無該筆公告事項";

    @Mock
    private AnnouncementRepo announcementRepo;

    @Mock
    private UserObject userObject;

    @InjectMocks
    private ANND001SvcImpl annd001SvcImpl;

    private ANND001Tranrq validTranrq;
    private AnnouncementEntity existingEntity;

    @BeforeEach
    void setUp() {
        validTranrq = new ANND001Tranrq();
        validTranrq.setId(VALID_ID);

        existingEntity = new AnnouncementEntity();
        existingEntity.setId(VALID_ID);
        existingEntity.setContent("測試公告內容");
        existingEntity.setCreatedBy(VALID_EMP_NAME);
        existingEntity.setCreatedAt(LocalDateTime.now());
        existingEntity.setUpdatedBy(VALID_EMP_NAME);
        existingEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("成功刪除公告 - 正常流程")
    void shouldDeleteAnnouncementSuccessfully() throws RestException, DataNotFoundException {
        // Given: 模擬使用者登入資訊，以及資料庫查詢到既有公告
        when(userObject.getEmpName()).thenReturn(VALID_EMP_NAME);
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.of(existingEntity));

        // When: 執行刪除公告
        EmptyTranrs result = annd001SvcImpl.deleteAnnouncement(validTranrq);

        // Then: 驗證回傳結果正確，且 findById 與 delete 各被呼叫一次
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result instanceof EmptyTranrs, "回傳結果應為 EmptyTranrs 類型");

        verify(userObject, times(1)).getEmpName();
        verify(announcementRepo, times(1)).findById(VALID_ID);
        verify(announcementRepo, times(1)).delete(existingEntity);
    }

    @Test
    @DisplayName("失敗 - empName 為 null 時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsNull() {
        // Given: 模擬 UserObject 回傳 null 的使用者名稱
        when(userObject.getEmpName()).thenReturn(null);

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annd001SvcImpl.deleteAnnouncement(validTranrq),
                "empName 為 null 應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode(), "錯誤碼應為 E300");
        assertEquals(ERROR_MSG_NO_USER, exception.getRtnMsg(), "錯誤訊息應正確");
        // Then: 驗證未執行查詢與刪除
        verify(announcementRepo, never()).findById(any());
        verify(announcementRepo, never()).delete(any());
    }

    @Test
    @DisplayName("失敗 - empName 為空白時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsBlank() {
        // Given: 模擬 UserObject 回傳全空白的使用者名稱
        when(userObject.getEmpName()).thenReturn("  ");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annd001SvcImpl.deleteAnnouncement(validTranrq),
                "empName 為空白應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode());
        // Then: 驗證未執行刪除
        verify(announcementRepo, never()).delete(any());
    }

    @Test
    @DisplayName("失敗 - empName 為空字串時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsEmpty() {
        // Given: 模擬 UserObject 回傳空字串的使用者名稱
        when(userObject.getEmpName()).thenReturn("");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annd001SvcImpl.deleteAnnouncement(validTranrq));

        assertEquals(ERROR_CODE_E300, exception.getRtnCode());
        // Then: 驗證未執行刪除
        verify(announcementRepo, never()).delete(any());
    }

    @Test
    @DisplayName("失敗 - 查無公告資料時拋出 DataNotFoundException")
    void shouldThrowDataNotFoundExceptionWhenAnnouncementNotFound() {
        // Given: 使用者驗證通過，但資料庫查無該筆公告
        when(userObject.getEmpName()).thenReturn(VALID_EMP_NAME);
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.empty());

        // When & Then: 執行並驗證拋出 DataNotFoundException
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> annd001SvcImpl.deleteAnnouncement(validTranrq),
                "查無資料應拋出 DataNotFoundException");

        assertEquals(ERROR_MSG_NOT_FOUND, exception.getMessage(), "例外訊息應正確");
        // Then: 驗證查詢被呼叫，但未執行刪除
        verify(announcementRepo, times(1)).findById(VALID_ID);
        verify(announcementRepo, never()).delete(any());
    }

    @Test
    @DisplayName("失敗 - 查無資料時不應執行刪除")
    void shouldNotDeleteWhenEntityNotFound() {
        // Given: 使用者驗證通過，但資料庫查無該筆公告
        when(userObject.getEmpName()).thenReturn(VALID_EMP_NAME);
        when(announcementRepo.findById(VALID_ID)).thenReturn(Optional.empty());

        // When & Then: 執行並驗證拋出例外
        assertThrows(DataNotFoundException.class,
                () -> annd001SvcImpl.deleteAnnouncement(validTranrq));

        // Then: 驗證 delete 方法完全未被呼叫
        verify(announcementRepo, never()).delete(any(AnnouncementEntity.class));
    }
}