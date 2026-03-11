package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNU001 修改公告
 * 上行／請求電文
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNU001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @NotNull(message = "公告ID 不得為空")
    @Min(value = 1, message = "公告ID 必須為正整數")
    @JsonProperty("id")
    private Long id;

    /** 公告內容 */
    @NotBlank(message = "公告內容 不得為空")
    @Size(max = 1000, message = "公告內容 最大長度為1000")
    @JsonProperty("content")
    private String content;

}