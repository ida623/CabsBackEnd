package com.cathaybk.demo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cathaybk.demo.dto.ANNC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.user.UserObject;

/**
 * ANNC001SvcImpl 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ANNC001SvcImpl 單元測試")
class ANNC001SvcImplTest {

    private static final String VALID_EMP_NAME = "陳阿仁";
    private static final String VALID_CONTENT = "系統將於 2026/04/01 進行維護";
    private static final String ERROR_CODE_E300 = "E300";
    private static final String ERROR_MSG_NO_USER = "未取得使用者資訊";

    @Mock
    private AnnouncementRepo announcementRepo;

    @Mock
    private UserObject userObject;

    @InjectMocks
    private ANNC001SvcImpl annc001SvcImpl;

    private ANNC001Tranrq validTranrq;

    @BeforeEach
    void setUp() {
        validTranrq = new ANNC001Tranrq();
        validTranrq.setContent(VALID_CONTENT);
    }

    @Test
    @DisplayName("成功新增公告 - 正常流程")
    void shouldCreateAnnouncementSuccessfully() throws RestException {
        // Given: 模擬使用者登入資訊與 Repository 儲存行為
        when(userObject.getEmpName()).thenReturn(VALID_EMP_NAME);
        when(announcementRepo.save(any(AnnouncementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeCreate = LocalDateTime.now();

        // When: 執行新增公告
        EmptyTranrs result = annc001SvcImpl.createAnnouncement(validTranrq);

        LocalDateTime afterCreate = LocalDateTime.now();

        // Then: 驗證回傳型別正確
        assertNotNull(result, "回傳結果不應為 null");
        assertTrue(result instanceof EmptyTranrs, "回傳結果應為 EmptyTranrs 類型");

        // Then: 驗證儲存的公告資料內容與時間欄位
        ArgumentCaptor<AnnouncementEntity> captor = ArgumentCaptor.forClass(AnnouncementEntity.class);
        verify(announcementRepo).save(captor.capture());

        AnnouncementEntity saved = captor.getValue();
        assertEquals(VALID_CONTENT, saved.getContent(), "內容應正確");
        assertEquals(VALID_EMP_NAME, saved.getCreatedBy(), "建立者應為登入使用者");
        assertEquals(VALID_EMP_NAME, saved.getUpdatedBy(), "修改者應為登入使用者");
        assertNotNull(saved.getCreatedAt(), "建立時間不應為 null");
        assertNotNull(saved.getUpdatedAt(), "修改時間不應為 null");
        assertTrue(saved.getCreatedAt().isAfter(beforeCreate.minusSeconds(1)), "建立時間應在執行後");
        assertTrue(saved.getCreatedAt().isBefore(afterCreate.plusSeconds(1)), "建立時間應在執行前");
        assertEquals(saved.getCreatedAt(), saved.getUpdatedAt(), "建立時間與修改時間應相同");

        verify(userObject, times(1)).getEmpName();
        verify(announcementRepo, times(1)).save(any(AnnouncementEntity.class));
    }

    @Test
    @DisplayName("失敗 - empName 為 null 時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsNull() {
        // Given: 模擬 UserObject 回傳 null 的使用者名稱
        when(userObject.getEmpName()).thenReturn(null);

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annc001SvcImpl.createAnnouncement(validTranrq),
                "empName 為 null 應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode(), "錯誤碼應為 E300");
        assertEquals(ERROR_MSG_NO_USER, exception.getRtnMsg(), "錯誤訊息應正確");
        // Then: 驗證不應執行儲存動作
        verify(announcementRepo, never()).save(any(AnnouncementEntity.class));
    }

    @Test
    @DisplayName("失敗 - empName 為空白字串時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsBlank() {
        // Given: 模擬 UserObject 回傳全空白的使用者名稱
        when(userObject.getEmpName()).thenReturn("   ");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annc001SvcImpl.createAnnouncement(validTranrq),
                "empName 為空白應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode(), "錯誤碼應為 E300");
        assertEquals(ERROR_MSG_NO_USER, exception.getRtnMsg(), "錯誤訊息應正確");
        // Then: 驗證不應執行儲存動作
        verify(announcementRepo, never()).save(any(AnnouncementEntity.class));
    }

    @Test
    @DisplayName("失敗 - empName 為空字串時拋出 RestException")
    void shouldThrowRestExceptionWhenEmpNameIsEmpty() {
        // Given: 模擬 UserObject 回傳空字串的使用者名稱
        when(userObject.getEmpName()).thenReturn("");

        // When & Then: 執行並驗證拋出 RestException
        RestException exception = assertThrows(RestException.class,
                () -> annc001SvcImpl.createAnnouncement(validTranrq),
                "empName 為空字串應拋出 RestException");

        assertEquals(ERROR_CODE_E300, exception.getRtnCode());
        // Then: 驗證不應執行儲存動作
        verify(announcementRepo, never()).save(any(AnnouncementEntity.class));
    }

    @Test
    @DisplayName("成功新增 - 驗證不同使用者名稱被正確寫入")
    void shouldUseCorrectEmpNameFromUserObject() throws RestException {
        // Given: 設定不同的登入使用者名稱
        String anotherEmp = "吳小昌";
        when(userObject.getEmpName()).thenReturn(anotherEmp);
        when(announcementRepo.save(any(AnnouncementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: 執行新增公告
        annc001SvcImpl.createAnnouncement(validTranrq);

        // Then: 驗證建立者與修改者均為該使用者名稱
        ArgumentCaptor<AnnouncementEntity> captor = ArgumentCaptor.forClass(AnnouncementEntity.class);
        verify(announcementRepo).save(captor.capture());

        AnnouncementEntity saved = captor.getValue();
        assertEquals(anotherEmp, saved.getCreatedBy(), "建立者應為 " + anotherEmp);
        assertEquals(anotherEmp, saved.getUpdatedBy(), "修改者應為 " + anotherEmp);
    }
}