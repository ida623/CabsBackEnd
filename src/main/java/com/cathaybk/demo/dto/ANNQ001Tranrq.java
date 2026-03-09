package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNQ001 查詢公告清單
 * 上行／請求電文
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 頁碼（從 1 開始） */
    @NotNull(message = "頁碼 不得為空")
    @Min(value = 1, message = "頁碼 必須大於等於 1")
    @JsonProperty("pageNumber")
    private Integer pageNumber;

    /** 每頁筆數 */
    @NotNull(message = "每頁筆數 不得為空")
    @Min(value = 1, message = "每頁筆數 必須介於 1~100")
    @Max(value = 100, message = "每頁筆數 必須介於 1~100")
    @JsonProperty("pageSize")
    private Integer pageSize;

    /** 排序方向：ASC / DESC（預設 DESC） */
    @Pattern(regexp = "^(ASC|DESC)$", message = "排序方向 僅允許 ASC 或 DESC")
    @JsonProperty("sortDirection")
    private String sortDirection;

}