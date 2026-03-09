package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNC001 新增公告
 * 上行／請求電文
 *
 * @author 張育誠
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNC001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告內容 */
    @NotBlank(message = "公告內容 不得為空")
    @Size(max = 1000, message = "公告內容 最大長度為1000")
    @JsonProperty("content")
    private String content;

}