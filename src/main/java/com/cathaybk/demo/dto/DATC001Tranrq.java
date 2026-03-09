package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-DATC001 新增日期設定
 * 上行／請求電文
 *
 * @author system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DATC001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 預計執行變更區間－起日（yyyy/MM/dd） */
    @NotBlank(message = "預計執行變更區間－起日 不得為空")
    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "預計執行變更區間－起日 格式必須為 yyyy/MM/dd")
    @JsonProperty("windowStartDate")
    private String windowStartDate;

    /** 預計執行變更區間－迄日（yyyy/MM/dd） */
    @NotBlank(message = "預計執行變更區間－迄日 不得為空")
    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "預計執行變更區間－迄日 格式必須為 yyyy/MM/dd")
    @JsonProperty("windowEndDate")
    private String windowEndDate;

    /** 截止申請日時間（YYYY/MM/DD HH:mm） */
    @NotBlank(message = "截止申請日時間 不得為空")
    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}$", message = "截止申請日時間 格式必須為 yyyy/MM/dd HH:mm")
    @JsonProperty("cutoffDate")
    private String cutoffDate;
}