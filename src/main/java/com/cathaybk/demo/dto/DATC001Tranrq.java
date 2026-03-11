package com.cathaybk.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * CABS-B-DATC001 新增日期設定
 * 上行／請求電文
 * @author system
 */
@Data
public class DATC001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * 預計執行變更區間－起日（yyyy/MM/dd）
     */
    @NotBlank(message = "預計執行變更區間－起日不可為空")
    @Size(min = 10, max = 10, message = "預計執行變更區間－起日長度必須為 10")
    @Pattern(regexp = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])$", message = "預計執行變更區間－起日格式錯誤，須為 yyyy/MM/dd")
    private String windowStartDate;

    /**
     * 預計執行變更區間－迄日（yyyy/MM/dd）
     */
    @NotBlank(message = "預計執行變更區間－迄日不可為空")
    @Size(min = 10, max = 10, message = "預計執行變更區間－迄日長度必須為 10")
    @Pattern(regexp = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])$", message = "預計執行變更區間－迄日格式錯誤，須為 yyyy/MM/dd")
    private String windowEndDate;

    /**
     * 截止申請日時間（yyyy/MM/dd HH:mm）
     */
    @NotBlank(message = "截止申請日時間不可為空")
    @Size(min = 16, max = 16, message = "截止申請日時間長度必須為 16")
    @Pattern(regexp = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01]) ([01]\\d|2[0-3]):[0-5]\\d$", message = "截止申請日時間格式錯誤，須為 yyyy/MM/dd HH:mm")
    private String cutoffDate;

}